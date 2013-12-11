package restaurant4;

import agent.Role;
import restaurant4.gui.Restaurant4CookGui;
import restaurant4.shareddata.*;

import java.util.*;
import java.util.Map.Entry;

import market.Food;
import market.MarketCashierRole;
import restaurant4.interfaces.*;
import market.interfaces.MarketCashier;
import market.interfaces.MarketTruck;
import person.interfaces.Person;

/**
 * Restaurant Cook Agent
 * 
 * Receives cookOrders from a waiter, cooks them, and gives them to the waiter again
 */
public class Restaurant4CookRole extends Role implements Restaurant4Cook{
	private static final int baseCookingTime = 1000;
	private List<cookOrder> cookOrders = Collections.synchronizedList(new ArrayList<cookOrder>());
	//This contains the unfilled cookOrders from a market so that he may recookOrder them
	private List<List<Food>> marketcookOrders = Collections.synchronizedList(new ArrayList<List<Food>>());
	private List<Double> checkcookOrders = Collections.synchronizedList(new ArrayList<Double>());
	public MarketCashier mc;
	public Restaurant4Cashier rc;
	public MarketTruck truck;
	private String name;
	public Restaurant4RevolvingStand stand = new Restaurant4RevolvingStand();
	private boolean sendTruckBack = false;
	private boolean checkStand = false;
	public Restaurant4CookGui gui;
	private int GrillNum = 0;
	private int endOfDay = 0;
	private Map<String, MyFood> cookTimes = Collections.synchronizedMap(new HashMap<String, MyFood>());
	public List<Food> foodlist = Collections.synchronizedList(new ArrayList<Food>());
	boolean needTocookOrder = false;
	Timer timer = new Timer();

	public Restaurant4CookRole(String name, Person pa) {
		super(pa);
		this.name = name;
		cookTimes.put("Shrimp", new MyFood(2*baseCookingTime, "Shrimp", 10, 4));
		cookTimes.put("Scallops", new MyFood(3*baseCookingTime, "Scallops", 10, 4));
		cookTimes.put("Lobster", new MyFood(5*baseCookingTime, "Lobster", 5, 2));
		cookTimes.put("Crab", new MyFood(4*baseCookingTime, "Crab", 5, 2));
		timer.schedule(new TimerTask() {
			public void run() {
				checkStand = true;
				if(person != null)
					stateChanged();
			}
		},
			5000);
	}

	public String getName() {
		return name;
	}

	// Messages

	/** 
	 * Receives this message from a waiter when an cookOrder comes in
	 * 
	 * @param food the food item to be cooked
	 * @param w the waiter delivering the cookOrder
	 * @param table the table number that the cookOrder goes to
	 */
	public void msgMakeFood(String food, Restaurant4WaiterRole w, int table) {
		GrillNum++;
		cookOrders.add(new cookOrder(food, w, table, GrillNum));
		stateChanged();
	}

	public void msgGettingFood(String food, int table){
		synchronized(cookOrders){
			for(cookOrder o : cookOrders){
				if(o.type.equals(food) && o.table == table){
					o.s = state.finished;
					stateChanged();
					return;
				}
			}
		}
	}
	/**
	 * Recieves this message from a timer when an cookOrder is done
	 * 
	 * @param o the cookOrder that was completed
	 */
	public void msgFoodDone(cookOrder o){
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
		needTocookOrder = true;
		stateChanged();
	}
	
	/**
	 * Received from the truck when it is delivering food
	 * 
	 * @param truck the truck delivering the food
	 * @param food the list of food
	 */
	public void msgHereisYourFood(MarketTruck truck, List<Food> food){
		this.truck = truck;
		for(Entry<String, MyFood> entry : cookTimes.entrySet()){
			for(Food f : food){
				if(f.choice.equals(entry.getKey())){
					entry.getValue().amount = f.amount;
				}
			}
		}		
		sendTruckBack = true;
		stateChanged();
	}

	/**
	 * Received from the cashier when he needs a bill to be checked
	 * 
	 * @param price the price of the bill
	 */
	public void msgCheckBill(double price){
		checkcookOrders.add(price);
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//Sends the truck back to the market
		if(sendTruckBack){
			sendTruckBack();
			return true;
		}
		//If the cook needs to cookOrder
		if(needTocookOrder){
			cookOrderFoodThatIsLow();
			return true;
		}
		//Checks for any cookOrders that are finished cooking
		for(cookOrder o : cookOrders){
			if(o.s == state.done){
				plateIt(o);
				return true;
			}
		}
		if(checkStand){
			checkStand();
			return true;
		}
		//Checks for any cookOrders that haven't started cooking
		for(cookOrder o : cookOrders){
			if(o.s == state.pending){
				cookIt(o);
				return true;
			}
		}
		for(cookOrder o : cookOrders){
			if(o.s == state.finished){
				gui.removeFood(o.type, o.table);
				cookOrders.remove(o);
				return true;
			}
		}
		if(!checkcookOrders.isEmpty()){
			checkBill();
			return true;
		}
		if(endOfDay == 2){
			workDayOver();
			return true;
		}
		return false;
	}

	// Actions
	/**
	 * Sets a timer for the cookOrder, and starts cooking it
	 * 
	 * @param o the cookOrder to be cooked
	 */
	private void cookIt(cookOrder o){
		if(cookTimes.get(o.type).amount == 0){
			o.waiter.msgOutOfItem(o.type, o.table);
			cookOrders.remove(o);
			return;
		}
		if(GrillNum > 3)
			GrillNum = 1;
		DoCooking(o);
		MyFood f = cookTimes.get(o.type);
		f.amount --;
		if(f.s == cookOrderState.notcookOrdered && f.amount <= f.low){
			foodlist.add(new Food(f.type, f.capacity-f.amount));
			f.s = cookOrderState.cookOrdered;
			cookOrderFoodThatIsLow();
		}
		o.s = state.cooking;
		timer.schedule(new doneCooking(o), cookTimes.get(o.type).time);
	}

	/**
	 * Plates the cookOrder, and messages the waiter who cookOrdered it
	 * 
	 * @param o the cookOrder to be plated
	 */
	private void plateIt(cookOrder o){
		DoPlating(o);
		o.s = state.sent;
		o.waiter.msgOrderReady(o.table, o.type, "Grill " + o.grillNum);
	}
	
	/**
	 * Cycles through the inventory, and cookOrders any items that haven't been cookOrdered that are low
	 */
	private void cookOrderFoodThatIsLow(){
		needTocookOrder = false;
		marketcookOrders.add(foodlist);
		mc.MsgIwantFood(this, rc, foodlist, 4);	
	}
	
	private void checkStand(){
		checkStand = false;
		Do("Checking stand");
		Order o = stand.removeOrder();
		if(o != null){
			cookOrder order = new cookOrder(o);
			cookOrders.add(order);
			Do("Adding Item");
		}
		
		timer.schedule(new TimerTask() {
			public void run() {
				checkStand = true;
				if(person != null)
					stateChanged();
			}
		},
			5000);
	}
	
	private void sendTruckBack(){
		truck.msgGoBack();
		sendTruckBack = false;
	}
	
	private void checkBill(){
		double price = checkcookOrders.get(0);
		double compare = 0;
		List<Food> marketList = marketcookOrders.get(0);
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
	
	private void workDayOver(){
		endOfDay = 0;
		getPerson().msgGoOffWork(this, 0.00);
		gui.setPresent(false);
	}
	
	// The animation DoXYZ() routines
	
	private void DoCooking(cookOrder o){
		print("Cooking "+ o.type);
		gui.DoCookFood(o.type, o.grillNum, o.table);
	}

	private void DoPlating(cookOrder o){
		print("Plating " + o.type);
		gui.DoPrepFood(o.type, o.table);
	}
	
	//utilities
	
	public void setGui(Restaurant4CookGui cg){
		gui = cg;
	}
	/**
	 * The cookOrder class
	 * 
	 * Contains a waiter, a type, a table and a current state
	 */
	private class cookOrder {
		Restaurant4AbstractWaiter waiter;
		String type;
		int table;
		int grillNum;
		state s;
		cookOrder(String choice, Restaurant4AbstractWaiter w, int tableNum, int grillNum){
			waiter = w;
			type = choice;
			table = tableNum;
			this.grillNum = grillNum;
			s = state.pending;
		}
		cookOrder(Order o){
			this.type = o.choice;
			this.waiter = o.w;
			GrillNum++;
			this.grillNum = GrillNum;
			this.table = o.table;
			s = state.pending;			
		}
	}
	//The states for the cookOrders
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
		cookOrderState s;
		MyFood(int time, String type, int capacity, int low){
			this.time = time;
			this.type = type;
			this.amount = capacity;
			this.capacity = capacity;
			this.low = low;
			s = cookOrderState.notcookOrdered;
		}
	}
	enum cookOrderState {cookOrdered, notcookOrdered, none}
	//This class is used so that the cookOrder can be temporarily held by the timer
	//May be unnecessary but is used currently and works
	private class doneCooking extends TimerTask {
		private final cookOrder cookOrder;
		doneCooking(cookOrder o){
			cookOrder = o;
		}
		public void run(){
			msgFoodDone(cookOrder);
		}
	}
	@Override
	public String getRoleName() {
		return "Restaurant 4 Cook";
	}

	public void setMarketCashier(MarketCashierRole r) {
		mc = r;
	}

	public void msgWorkDayOver() {
		endOfDay++;
		stateChanged();
	}
	
	public utilities.Gui getGui(){
		return this.gui; 
	}
}
