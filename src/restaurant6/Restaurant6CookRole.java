package restaurant6;

import agent.Agent;   
import agent.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;
import market.Food;
import market.interfaces.MarketCashier;
import market.interfaces.MarketTruck;
import restaurant6.Restaurant6Order.OrderState;
import restaurant6.gui.Restaurant6CookGui;
import restaurant6.gui.Restaurant6CookGui.GuiState;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Cook;
import restaurant6.test.mock.EventLog;
import restaurant6.test.mock.LoggedEvent;
import utilities.restaurant.RestaurantCashier;
import utilities.restaurant.RestaurantCook;

/**
 * Restaurant Cook Agent
 */

public class Restaurant6CookRole extends Role implements Restaurant6Cook {
	// Timer to cook food
	Timer cookTimer = new Timer(); 
	
	// Timer to check stand
	Timer standTimer = new Timer();
	
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Map of all strings to food objects
	private Map<String, Restaurant6Food> foods = new HashMap<String, Restaurant6Food>();
	
	// Reference to the revolving stand
	private Restaurant6RevolvingStand revolvingStand;
	
	// Hack to set producer consumer monitor
	public void setStand(Restaurant6RevolvingStand p) {
		revolvingStand = p;
	}
	
	private String cookName;
	
	private Restaurant6CookGui cookGui = null;

	// Creating a list of orders
	public List<Restaurant6Order> cookOrders = Collections.synchronizedList(new ArrayList<Restaurant6Order>());
	
	// To prevent cook from placing multiple orders if it's already being fulfilled
	private boolean orderPlaced;
	
	private final int numMarkets = 3;
	private int numNeeded = 1;
	private final int threshold = 1;
	
	private Restaurant6Cashier cashier;
	
	// Hack to establish connection to the cashier
	public void setCashier(Restaurant6Cashier c) {
		cashier = c;
	}
	
	// To determine if the cook is at the fridge, grill or plating area
	private Semaphore atFridge = new Semaphore(0,true);
	private Semaphore atGrill = new Semaphore(0,true);
	private Semaphore atPlatingArea = new Semaphore(0,true);
	
	// Creating a list of markets to order from
	private List<MarketCashier> markets = Collections.synchronizedList(new ArrayList<MarketCashier>(numMarkets));
	
	// Creates private inner class of orders to market
	private static class MarketOrder {
//		public List<Restaurant6Restock> items = Collections.synchronizedList(new ArrayList<Restaurant6Restock>());
		public List<Food> items = Collections.synchronizedList(new ArrayList<Food>());
		public MarketTruck truck;
		
		public enum MarketOrderState {None, Partial, Fulfilled}
		private MarketOrderState state;
		
		public MarketOrder() {
			state = MarketOrderState.None;
		}
		
		public MarketOrder(MarketOrderState st) {
			state = st;
		}
	}
	
	// List of orders sent to market
	private List<MarketOrder> ordersToMarket = Collections.synchronizedList(new ArrayList<MarketOrder>());
	
	// Keeps track of how many markets have been ordering from
	private int marketIndex;
	
	// State created for the cook so he can check inventory if bored
	private enum CookState {Bored, Working}
	private CookState state;
	
	// Boolean to make cook check the revolving stand
	private boolean checkingStand;
	
	// Determines how long each food will be cooked for
	private final int chickenTime = 5;
	private final int steakTime = 8;
	private final int pizzaTime = 3;
	private final int saladTime = 1;
	
	// Determines how much inventory the cook has for each item
	private final int chickenAmount = 3;
	private final int steakAmount = 3;
	private final int pizzaAmount = 3;
	private final int saladAmount = 3;

	// CookAgent constructor
	public Restaurant6CookRole(String name, Person p) {
		super(p);
		cookName = name;
		
		// Set cook state to bored so that he can check inventory if no customer is there
		state = CookState.Bored;

		// Sets initial value of market index to 0 to order from default market
		marketIndex = 0;
		
		orderPlaced = false;
		checkingStand = false;
		
		// Checks inventory initially
		msgCheckInventory();
		
		// Creates map of food choices to food objects
		foods.put("Chicken", new Restaurant6Food("Chicken", chickenTime, chickenAmount));
		foods.put("Steak", new Restaurant6Food("Steak", steakTime, steakAmount));
		foods.put("Salad", new Restaurant6Food("Salad", saladTime, saladAmount));
		foods.put("Pizza", new Restaurant6Food("Pizza", pizzaTime, pizzaAmount));
	}
	
	// Sets the gui for the cook
	public void setGui(Restaurant6CookGui g) {
		cookGui = g;
	}
	
	// Returns the cook's name
	public String getName() {
		return cookName;
	}
	
	// Hack to create connection between the market and the cook
	public void addMarket(MarketCashier m) {
		markets.add(m);
	}

	// Messages	
	// Message telling cook to check the revolving stand
	public void msgCheckStand() {
		print("I have to go check the revolving stand for orders!");
		log.add(new LoggedEvent("I have to go check the revolving stand for orders!"));		
		checkingStand = true;
		stateChanged();
	}

	// Message telling cook to decrease inventory
	public void decreaseFoodOrderOneMarket() {
		state = CookState.Bored;
		
		// Sets number needed to 3 so that cook only orders from one market
		numNeeded = 3;
		
		// Sets all inventory to threshold
		Restaurant6Food chicken = foods.get("Chicken");
		chicken.setAmount(1);
		Restaurant6Food steak = foods.get("Steak");
		steak.setAmount(1);
		Restaurant6Food salad = foods.get("Salad");
		salad.setAmount(1);
		Restaurant6Food pizza = foods.get("Pizza");
		pizza.setAmount(1);
	
		stateChanged();
	}
	
	// Message telling cook to decrease inventory
	public void decreaseFoodOrderTwoMarkets() {
		state = CookState.Bored;
		
		numNeeded = 8;
		
		// Sets all inventory to threshold
		Restaurant6Food chicken = foods.get("Chicken");
		chicken.setAmount(1);
		Restaurant6Food steak = foods.get("Steak");
		steak.setAmount(1);
		Restaurant6Food salad = foods.get("Salad");
		salad.setAmount(1);
		Restaurant6Food pizza = foods.get("Pizza");
		pizza.setAmount(1);
	
		stateChanged();
	}
	
	// Message prompting cook to check inventory
	public void msgCheckInventory() {
		state = CookState.Bored;
		stateChanged();
	}

	public void msgEmptyStock() {
		// TODO Auto-generated method stub
		
	}
	
	// Message to receive order from waiter
	public void hereIsAnOrder(Restaurant6Order o) {
		cookOrders.add(new Restaurant6Order(o.getOrder(), o.getTableNum(), o.getWaiter()));
		print("Order " + o.getOrder() +" received");
		log.add(new LoggedEvent("Order " + o.getOrder() + " received"));
		stateChanged();
	}
	
	// Message from market saying order fulfillment incomplete
//	public void msgCannotFulfill(List<Restaurant6Restock> orders) {	
//		orderPlaced = false; 
//		
//		MarketOrder tempOrder = new MarketOrder(MarketOrder.MarketOrderState.Partial);
//		
//		// Copy all orders from orders list that market sent to own cook list
//		for (int i = 0; i < orders.size(); i++) {
//			tempOrder.items.add(new Food(orders.get(i).getOrderChoice(), orders.get(i).getAmount()));
//		}
//		
//		ordersToMarket.add(tempOrder);
//		
//		// Increment value of market index so can order from backup markets
//		++marketIndex;
//		
//		if (marketIndex >= 3) {
//			ordersToMarket.remove(tempOrder);
//			print("No more markets to order from.");
//			orderPlaced = false;
//		}
//		
//		stateChanged();
//	}
	
	// Message from market with order fulfillment	
	public void msgHereisYourFood(MarketTruck t, List<Food> order) {
		orderPlaced = false;
		
		MarketOrder tempOrder = new MarketOrder(MarketOrder.MarketOrderState.Fulfilled);
		tempOrder.truck = t;
		
		for (Food f : order) {
			tempOrder.items.add(new Food(f.choice, f.amount));
		}
		
		ordersToMarket.add(tempOrder);
		
		stateChanged();
	}
	
	// Message when GUI gets to the fridge
	public void msgAtFridge() {
		print("msgAtFridge called");
		atFridge.release();
		stateChanged();
	}
	
	// Message when GUI gets to the grill
	public void msgAtGrill() {
		atGrill.release();
		stateChanged();
	}
	
	// Message when GUI gets to the plating area
	public void msgAtPlatingArea() {
		atPlatingArea.release();
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/*Checks for the status of the orders  - if the list of cook's orders 
		 * is not empty and there are orders that have pending status, cook them.
		 * If the list of cook's orders is not empty and there are orders that are
		 * ready to be served, message the waiter to serve them.
		 */
		if (state == CookState.Bored) {
			checkInventory();
			return true;
		}
		
		if (state == CookState.Working) {
			if (!ordersToMarket.isEmpty()) {
//				for (MarketOrder r : ordersToMarket) {
//					if (r.state == MarketOrder.MarketOrderState.Partial && marketIndex < 3 && !orderPlaced) {
//						placeOrderWithNewMarket(r);
//						return true;
//					}
//				}
				for (MarketOrder r : ordersToMarket) {
					if (r.state == MarketOrder.MarketOrderState.Fulfilled) { // && !orderPlaced) {
						changeInventory(r);
						return true;
					}
				}
			}	
			
			if (checkingStand) {
				checkStand();
				return true;
			}
			
			if (!cookOrders.isEmpty()) {
				for (Restaurant6Order o : cookOrders) {
					if (o.getOrderStatus() == OrderState.Pending) {
						cookOrder(o);
						return true;
					}
				}
			}
		
			if (!cookOrders.isEmpty()) {
				for (Restaurant6Order o : cookOrders) {	
					if (o.getOrderStatus() == OrderState.Cooked) {
							tellWaiterOrderIsReady(o);
						return true;
					}
				}
			}
		}
			
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	// Check inventory and orders if low on inventory
	private void checkInventory() {
		state = CookState.Working;
		orderPlaced = false;
		print("Checking my inventory..");
		log.add(new LoggedEvent("Checking my inventory.."));
		
		MarketOrder tempOrder = new MarketOrder();
		
		// Check inventory for chicken. If below threshold, add to list of things you need to order
		Restaurant6Food chicken = foods.get("Chicken");
		if (chicken.getAmount() <= threshold) {
			// Add food to list of orders to send to market
			tempOrder.items.add(new Food("Chicken", numNeeded));
			print("Placed an order for chicken");
		}
		
		// Check inventory for steak. If below threshold, add to list of things you need to order
		Restaurant6Food steak = foods.get("Steak");
		if (steak.getAmount() <= threshold) {
			// Add food to list of orders to send to market
			tempOrder.items.add(new Food("Steak", numNeeded));
			print("Placed an order for steak");
		}
		
		// Check inventory for salad. If below threshold, add to list of things you need to order
		Restaurant6Food salad = foods.get("Salad");
		if (salad.getAmount() <= threshold) {
			// Add food to list of orders to send to market
			tempOrder.items.add(new Food("Salad", numNeeded));
			print("Placed an order for salad");
		}
		
		// Check inventory for pizza. If below threshold, add to list of things you need to order
		Restaurant6Food pizza = foods.get("Pizza");
		if (pizza.getAmount() <= threshold) {
			pizza.setAmountMissing(numNeeded);
			// Add food to list of orders to send to market
			tempOrder.items.add(new Food("Pizza", numNeeded));
			print("Placed an order for pizza");
		}
		
		if (!tempOrder.items.isEmpty()) {
			markets.get(0).MsgIwantFood(this, this.cashier, tempOrder.items, 6);
			// Tell the cashier what you ordered so they can verify
			cashier.msgOrderedFood(tempOrder.items);
			orderPlaced = true;
		}
	}
	
	// Changes the inventory of the cook once the market has been fulfilled.
	private void changeInventory(MarketOrder order) {
		orderPlaced = false;
		print("Receiving order shipment");
		
		// Loop through list inside MarketOrder object and change inventory by the amount of the order
		for (Food f : order.items) {
			Restaurant6Food food = foods.get(f.choice);
			int temp = f.amount + food.getAmount();
			food.setAmount(temp);
			print ("My cook inventory of " + food.getChoice() + " is now " + food.getAmount());
		}
		
		// Tell the market truck to go back
		order.truck.msgGoBack();
		ordersToMarket.remove(order);
	}
	
	// Places order with new market
//	private void placeOrderWithNewMarket(MarketOrder order) {
//		orderPlaced = true;
//		
//		print("Placing order with new market");
//		
//		if (marketIndex <= 3) {
//			markets.get(marketIndex).msgOrderFood(order.items);
//			ordersToMarket.remove(order);
//		}
//	}
	
	/*
	 * Checks the stand for any new orders. Will wait until order appears
	 */
	private void checkStand() {
		print("Checking stand..");
		log.add(new LoggedEvent("Checking stand.."));
		
		Restaurant6Order tempOrder = revolvingStand.remove();
		
		if (tempOrder != null) {
			print("Picked up order of " + tempOrder.getOrder());
			log.add(new LoggedEvent("Picked up order of " + tempOrder.getOrder()));
			
			cookOrders.add(tempOrder);
		}
		
		else {
			if (cookGui != null) {
				cookGui.DoGoToHome();
			}
		}
		
		standTimer.schedule(new TimerTask() {
			public void run() {
				msgCheckStand();
			}
		}, 1000); 
		
		checkingStand = false;
	}
	
	/* Cooks the order. Sets the order's status to cooking and runs the timer
	 * for the food to cook.
	 */
	private void cookOrder(final Restaurant6Order o) {	
		// If an order hasn't been placed, check inventory and place order if necessary
		if (!orderPlaced) {
			checkInventory();
		}
		
		Restaurant6Food food = foods.get(o.getOrder());
		
		if (food.getAmount() == 0) { // If there is no more food, let waiter know
			print("Out of food");
			o.getWaiter().outOfFood(o);
			cookOrders.remove(o);
		}
		
		// Otherwise, cook the food
		if (food.getAmount() > 0) { 
			if (cookGui != null) {
				cookGui.DoGoToFridge(o.getOrder());
		
				if (cookGui.getState() == Restaurant6CookGui.GuiState.DoingNothing || cookGui.getState() == Restaurant6CookGui.GuiState.WaitingForFoodToCook) {
					try {
						atFridge.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		
				}
				
				// Sets the gui's state to getting ingredients so can draw appropriate string
				cookGui.setState(Restaurant6CookGui.GuiState.GettingIngredients);
				
				cookGui.DoGoToGrill(o.getOrder());
				
				if (cookGui.getState() == Restaurant6CookGui.GuiState.GettingIngredients) {
					try {
						atGrill.acquire();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
		//		cookGui.setState(CookGui.GuiState.CookingFood);
				cookGui.setCookingGrillState(o.getOrder());
			}
			
			print("Cooking order..");
			log.add(new LoggedEvent("Cooking order.."));
			o.setOrderStatus(OrderState.Cooking);
			cookTimer.schedule(new TimerTask() {
				public void run() {
					print("Done cooking food!");
					o.setOrderStatus(OrderState.Cooked);
					stateChanged();
				}
			}, (food.getCookTime() * 1000)); 
			food.setAmount(food.getAmount() - 1);
			
			if (cookGui != null) { 
				cookGui.DoGoToHome();
				cookGui.setState(GuiState.WaitingForFoodToCook);
			}
		}
	}
	
	/* Removes the cooked food from the list of orders that the cook has.
	 * Lets the waiter know that the food is ready.
	 */
	private void tellWaiterOrderIsReady(Restaurant6Order o) {		
		if (cookGui != null) {
			cookGui.setState(Restaurant6CookGui.GuiState.GettingCookedFood);
		
			// Cook goes to the grill and picks up the cooked food
			cookGui.DoGoToGrill(o.getOrder());
			
			try {
				atGrill.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			cookGui.setState(Restaurant6CookGui.GuiState.GotCookedFood);
			
			cookGui.setEmptyGrillState(o.getOrder());
			
			// Cook drops the food off at the cooking area and then messages the waiter
			cookGui.DoGoToPlatingArea();
			
			try {
				atPlatingArea.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		o.getWaiter().orderIsReady(o);
		
		if (cookGui != null) {
			cookGui.setState(Restaurant6CookGui.GuiState.DoingNothing);
			cookGui.DoGoToHome();
		}
		
		cookOrders.remove(o);
	}

	// Returns role name
	public String getRoleName() {
		return "Restaurant 6 Cook";
	}

}

