package restaurant4;

import agent.Role;
import restaurant4.gui.Restaurant4CookGui;

import java.util.*;

import market.Food;
import restaurant4.interfaces.*;
import market.interfaces.MarketCashier;
import market.interfaces.MarketTruck;
import person.interfaces.Person;

/**
 * Restaurant Cook Agent
 * 
 * Receives orders from a waiter, cooks them, and gives them to the waiter again
 */
public class Restaurant4CookRole extends Role implements Restaurant4Cook{
	private static final int baseCookingTime = 1000;
	private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	//This contains the unfilled orders from a market so that he may reorder them
	private List<List<Food>> marketOrders = Collections.synchronizedList(new ArrayList<List<Food>>());
	private List<Double> checkOrders = Collections.synchronizedList(new ArrayList<Double>());
	public MarketCashier mc;
	public Restaurant4Cashier rc;
	private String name;
	public Restaurant4CookGui gui;
	private int GrillNum = 0;
	private Map<String, MyFood> cookTimes = Collections.synchronizedMap(new HashMap<String, MyFood>());
	public List<Food> foodlist = Collections.synchronizedList(new ArrayList<Food>());
	boolean needToOrder = false;
	Timer timer = new Timer();

	public Restaurant4CookRole(String name, Person pa) {
		super(pa);
		this.name = name;
		cookTimes.put("Shrimp", new MyFood(2*baseCookingTime, "Shrimp", 10, 4));
		cookTimes.put("Scallops", new MyFood(3*baseCookingTime, "Scallops", 10, 4));
		cookTimes.put("Lobster", new MyFood(5*baseCookingTime, "Lobster", 5, 2));
		cookTimes.put("Crab", new MyFood(4*baseCookingTime, "Crab", 5, 2));
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
	 */
	public void msgEmptyStock(){
		foodlist = new ArrayList<Food>();
		for (String key : cookTimes.keySet()){
			MyFood f = cookTimes.get(key);
			 f.amount = 0;
			 foodlist.add(new Food(f.type, f.capacity-f.amount));
		}
		needToOrder = true;
		stateChanged();
	}
	
	public void msgHereisYourFood(MarketTruck truck, List<Food> food){
		
	}

	public void msgCheckBill(double price){
		checkOrders.add(price);
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//If the cook needs to order
		if(needToOrder){
			orderFoodThatIsLow();
			return true;
		}
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
		if(!checkOrders.isEmpty()){
			checkBill();
			return true;
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
		if(GrillNum > 3)
			GrillNum = 1;
		DoCooking(o);
		MyFood f = cookTimes.get(o.type);
		f.amount --;
		if(f.s == orderState.notOrdered && f.amount <= f.low){
			foodlist.add(new Food(f.type, f.capacity-f.amount));
			f.s = orderState.ordered;
			orderFoodThatIsLow();
		}
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
	private void orderFoodThatIsLow(){
		needToOrder = false;
		marketOrders.add(foodlist);
		mc.MsgIwantFood(this, rc, foodlist, 4);	
	}
	
	private void checkBill(){
		double price = checkOrders.get(0);
		double compare = 0;
		List<Food> marketList = marketOrders.get(0);
		for(int i = 0; i < marketList.size(); i++){
			switch(marketList.get(i).choice){
				case "Shrimp": compare+=8.99; break;
				case "Scallops": compare+=7.99; break;
				case "Lobster": compare+=14.99; break;
				case "Crab": compare+=13.99; break;
			}
		}
		if(compare == price)
			rc.msgBillChecked(price);
		else
			rc.msgBillChecked(price);
	}
	
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
	private class MyFood {
		int time;
		String type;
		int amount;
		int capacity;
		int low;
		orderState s;
		MyFood(int time, String type, int capacity, int low){
			this.time = time;
			this.type = type;
			this.amount = capacity;
			this.capacity = capacity;
			this.low = low;
			s = orderState.notOrdered;
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
