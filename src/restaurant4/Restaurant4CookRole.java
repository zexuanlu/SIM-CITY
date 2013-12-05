package restaurant4;

import agent.Role;
import restaurant4.gui.Restaurant4CookGui;

import java.util.*;

import person.interfaces.Person;

/**
 * Restaurant Cook Agent
 * 
 * Receives orders from a waiter, cooks them, and gives them to the waiter again
 */
public class Restaurant4CookRole extends Role {
	private static final int baseCookingTime = 1000;
	private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	//This contains the unfilled orders from a market so that he may reorder them
	private List<HashMap<String,Integer>> marketOrders = Collections.synchronizedList(new ArrayList<HashMap<String,Integer>>());
	private String name;
	private Restaurant4CookGui gui;
	private int GrillNum = 0;
	private Map<String, Food> cookTimes = Collections.synchronizedMap(new HashMap<String, Food>());
	//Used to cycle through the markets
	int currentMarket = 0;
	//Used to find out if all markets are out of something
	int orderCount = 0;
	boolean needToOrder = false;
	Timer timer = new Timer();

	public Restaurant4CookRole(String name, Person pa) {
		super(pa);

		this.name = name;
		cookTimes.put("Steak", new Food(5*baseCookingTime, "Steak"));
		cookTimes.put("Chicken", new Food(4*baseCookingTime, "Chicken"));
		cookTimes.put("Salad", new Food(baseCookingTime, "Salad"));
		cookTimes.put("Pizza", new Food(2*baseCookingTime, "Pizza"));
	}

	public String getName() {
		return name;
	}

	// Messages

	/** 
	 * Receives this message from a waiter when an order comes in
	 * 
	 * @param food the food item to be cooked
	 * @param w the waiter delivering the order
	 * @param table the table number that the order goes to
	 */
	public void msgMakeFood(String food, Restaurant4WaiterRole w, int table) {
		GrillNum++;
		orders.add(new Order(food, w, table, GrillNum));
		print("Cook received order");
		stateChanged();
	}

	public void msgGettingFood(String food, int table){
		synchronized(orders){
			for(Order o : orders){
				if(o.type.equals(food) && o.table == table){
					o.s = state.finished;
					stateChanged();
					return;
				}
			}
		}
	}
	/**
	 * Recieves this message from a timer when an order is done
	 * 
	 * @param o the order that was completed
	 */
	public void msgFoodDone(Order o){
		o.s = state.done;
		stateChanged();
	}
	
	/**
	 * A hack for emptying the stock of any item
	 * 
	 * @param name a variable used for the hack
	 */
	public void msgEmptyFood(String name){
		switch(name){
			case "outOfSteak": cookTimes.get("Steak").amount = 0; break;
			case "outOfChicken": cookTimes.get("Chicken").amount = 0; break;
			case "outOfPizza": cookTimes.get("Pizza").amount = 0; break;
			case "outOfSalad": cookTimes.get("Salad").amount = 0; break;
			case "outOfAll": 
				synchronized(cookTimes.entrySet()){
					for(Map.Entry<String, Food> entry : cookTimes.entrySet()){
						Food f = entry.getValue();
						f.amount = 0;
					}
				}
				break;
			default : break;
		}
		needToOrder = true;
		stateChanged();
	}
	
	/**
	 * Received from a market when they cannot fufill an order
	 * 
	 * @param reOrder the part of the order that is unfufilled
	 * @param m the market that couldn't fufill the order
	 */
	//FIX!
//	public void msgOutOfFood(Map<String,Integer> reOrder, MarketAgent m){
//		currentMarket++;
//		orderCount++;
//		if(currentMarket == markets.size()){
//			currentMarket = 0;
//		}
//		if(orderCount == markets.size()){
//			Do("All markets out of some items");
//			return;
//		}
//		HashMap<String, Integer> ReOrder = new HashMap<String, Integer>(reOrder);
//		marketOrders.add(ReOrder);
//		stateChanged();
//		System.out.println("Notified by Market that they are out of some items");
//	}
	
	/**
	 * A message from the market delivering the food
	 * 
	 * @param delivery the map containing the delivery
	 */
	public void msgFoodDelivered(Map<String,Integer> delivery){
		synchronized(cookTimes.entrySet()){
			for(Map.Entry<String, Food> entry : cookTimes.entrySet()){
				Food f = entry.getValue();
				if(delivery.containsKey(f.type)){
					
					f.amount += delivery.get(f.type);
					f.s = orderState.none;
					Do("Now we have " + f.amount + " " + f.type);
					orderCount = 0;
					stateChanged();
				}
			}
		}
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//If the cook needs to reorder something
		//FIX
//		if(marketOrders.size() != 0){
//			reOrderFoodThatIsLow(marketOrders.get(0));
//			marketOrders.remove(0);
//			return true;
//		}
		//If the cook needs to order (used for the hack)
//		if(needToOrder){
//			orderFoodThatIsLow();
//			return true;
//		}
		//Checks for any orders that are finished cooking
		for(Order o : orders){
			if(o.s == state.done){
				plateIt(o);
				return true;
			}
		}
		//Checks for any orders that haven't started cooking
		for(Order o : orders){
			if(o.s == state.pending){
				cookIt(o);
				return true;
			}
		}
		for(Order o : orders){
			if(o.s == state.finished){
				gui.removeFood(o.type, o.table);
				orders.remove(o);
				return true;
			}
		}
		return false;
	}

	// Actions
	/**
	 * Sets a timer for the order, and starts cooking it
	 * 
	 * @param o the order to be cooked
	 */
	private void cookIt(Order o){
		if(cookTimes.get(o.type).amount == 0){
			o.waiter.msgOutOfItem(o.type, o.table);
			orders.remove(o);
			return;
		}
		if(GrillNum > 10)
			GrillNum = 1;
		DoCooking(o);
		cookTimes.get(o.type).amount--;
//		orderFoodThatIsLow();
		o.s = state.cooking;
		timer.schedule(new doneCooking(o), cookTimes.get(o.type).time);
	}

	/**
	 * Plates the order, and messages the waiter who ordered it
	 * 
	 * @param o the order to be plated
	 */
	private void plateIt(Order o){
		DoPlating(o);
		o.s = state.sent;
		o.waiter.msgOrderReady(o.table, o.type, "Grill " + o.grillNum);
	}
	
	/**
	 * Cycles through the inventory, and orders any items that haven't been ordered that are low
	 */
	//FIX
//	private void orderFoodThatIsLow(){
//		needToOrder = false;
//		Map<String, Integer> marketOrder = new HashMap<String, Integer>();
//		synchronized(cookTimes.entrySet()){
//			for(Map.Entry<String, Food> entry : cookTimes.entrySet()){
//				Food f = entry.getValue();
//				if(f.amount < f.low && f.s != orderState.ordered){
//					f.s = orderState.ordered;
//					marketOrder.put(f.type, (f.capacity-f.amount));
//				}
//			}
//		}
		//FIX
//		if(marketOrder.size() != 0){
//			Do("Sending Order to " + markets.get(currentMarket));
//			markets.get(currentMarket).msgINeedFood(marketOrder, this);
//		}
//	}
	
	/**
	 * Uses the map from the market to order from a different market
	 * 
	 * @param marketOrder the map used to reorder
	 */
	//FIX
//	private void reOrderFoodThatIsLow(Map<String, Integer> marketOrder){
//		Do("Sending Order to " + markets.get(currentMarket));
//		markets.get(currentMarket).msgINeedFood(marketOrder, this);		
//	}
	
	// The animation DoXYZ() routines
	
	private void DoCooking(Order o){
		print("Cooking "+ o.type);
		gui.DoCookFood(o.type, o.grillNum, o.table);
	}

	private void DoPlating(Order o){
		print("Plating " + o.type);
		gui.DoPrepFood(o.type, o.table);
	}
	
	//utilities

	//FIX
//	public void addMarket(MarketAgent m){
//		markets.add(m);
//		orderFoodThatIsLow();
//	}
	
	public void setGui(Restaurant4CookGui cg){
		gui = cg;
	}
	/**
	 * The order class
	 * 
	 * Contains a waiter, a type, a table and a current state
	 */
	private class Order {
		Restaurant4WaiterRole waiter;
		String type;
		int table;
		int grillNum;
		state s;
		Order(String choice, Restaurant4WaiterRole w, int tableNum, int grillNum){
			waiter = w;
			type = choice;
			table = tableNum;
			this.grillNum = grillNum;
			s = state.pending;
		}
	}
	//The states for the orders
	enum state {pending, cooking, done, sent, finished}
	
	/**
	 * The food class
	 * 
	 * Contains a time of cooking and will later contain inventory
	 */
	private class Food {
		int time;
		String type;
		int amount;
		int capacity;
		int low;
		orderState s;
		Food(int time, String type){
			this.time = time;
			this.type = type;
			amount = 4;
			capacity = 5;
			low = 2;
			s = orderState.none;
		}
	}
	enum orderState {ordered, notOrdered, none}
	//This class is used so that the Order can be temporarily held by the timer
	//May be unnecessary but is used currently and works
	private class doneCooking extends TimerTask {
		private final Order order;
		doneCooking(Order o){
			order = o;
		}
		public void run(){
			msgFoodDone(order);
		}
	}
	@Override
	public String getRoleName() {
		return "Restaurant 4 Cook";
	}
}
