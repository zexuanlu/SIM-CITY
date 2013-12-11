package restaurant2;

import restaurant2.gui.Restaurant2CookGui;
import restaurant2.interfaces.Restaurant2Customer;
import restaurant2.interfaces.Restaurant2Waiter;
import restaurant2.Restaurant2Order;
import utilities.restaurant.RestaurantCook;
import agent.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.Food;
import market.MarketCashierRole;
import market.interfaces.MarketTruck;
import person.interfaces.Person;

/**
 * Restaurant customer agent.
 */
public class Restaurant2CookRole extends Role implements RestaurantCook {
	public int offWorkMess = 0; 
	public boolean offWork; 
	
	Timer timer = new Timer();
	Timer clear = new Timer();
	Timer checkStand = new Timer();
	public MarketCashierRole marketCashier;
	public Restaurant2CashierRole cashier;
	public Restaurant2CookGui cookGui = null;
	Grill grill = new Grill();
	public enum OrderState {Uncooked, Cooking, Cooked};

	// agent correspondents
	public List<Restaurant2Waiter> waiters= new ArrayList<Restaurant2Waiter>();
	public List<Restaurant2Order> orders = Collections.synchronizedList(new ArrayList<Restaurant2Order>());
	public List<Restaurant2Order> readyOrders = new ArrayList<Restaurant2Order>();
	public List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	private Map<String, Integer> cookTimes = new HashMap<String, Integer>();
	private Map<MyMarket, Check> owed = new HashMap<MyMarket, Check>();
	private Food inventory = new Food(10,10,10,10,10,10);
	private List<market.Food> foodlist;
	public Restaurant2RevolvingStand revolver; //= new Restaurant2RevolvingStand();

	private Semaphore atGrill = new Semaphore(0, true);
	private Semaphore atCs = new Semaphore(0, true);

	private MyMarket mm1;
	private MyMarket mm2;
	private MyMarket mm3;
	private MyMarket market;
	private boolean needTocookOrder = false;
	public enum MarketState {Idle, OrderedFrom, NeedsPayment};

	/**
	 * Constructor for CookAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the cookgui so the customer can send it messages
	 */
	public Restaurant2CookRole(String name, Person p){
		super(p);
		cookTimes.put("Steak", 1000);
		cookTimes.put("Hamburger", 500);
		cookTimes.put("Chicken", 5000);
		cookTimes.put("Ribs", 1500);
		cookTimes.put("Salad", 750);
		cookTimes.put("Pound Cake", 900);
		checkStand.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Do("THE LORD");
				stateChanged();
			}
		}, 0, 1000);
	}
	
	
	public void msgGoOffWork(){
		print ("REST COOK 2 msgGoOffWork");
		offWorkMess++; 
	//	if (offWorkMess == 2){
			offWork = true; 
			stateChanged(); 
	//	}
	}
	
	public void setMarket(MarketAgent a, MarketAgent b, MarketAgent c){
		market = new MyMarket(a, "market");
		/*mm1 = new MyMarket(a, "mm1");
		mm2 = new MyMarket(b, "mm2");
		mm3 = new MyMarket(c, "mm3");
		markets.add(mm1);
		markets.add(mm2);
		markets.add(mm3);*/
		/*checkStand.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				Do("THE LORD");
				stateChanged();
			}
		}, 0, 1000);*/

	}
	public void setGui(Restaurant2CookGui c){
		cookGui = c;
	}
	public void setRevolvingStand(Restaurant2RevolvingStand r){
		revolver = r;
	}
	// Messages
	public void msgOrderToCook(String orderString, Restaurant2Waiter waiter, Restaurant2CustomerRole customer) {

		System.out.println("Order for "+orderString+" for "+customer.getName());
		Restaurant2Order order = new Restaurant2Order(waiter, customer, orderString);
		orders.add(order);
		//waiters.add(waiter);
		stateChanged();
	}
	public void msgMarketOrderFilled(MarketAgent m, Map<String, Integer> order, Check price){
		for(Map.Entry<String, Integer> entry: order.entrySet()){
			inventory.add(entry.getKey(), entry.getValue());
			inventory.orderStatus.put(entry.getKey(), false);
		}
		synchronized(markets){
			for(MyMarket mm : markets){
				if(mm.getMarket() == m){
					mm.state = MarketState.NeedsPayment;
					owed.put(mm, price);
				}
			}
		}
		stateChanged();
	}
	public void msgOutOfFood(String order, MarketAgent m){
		print("The market couldnt fufill the order for "+order);
		synchronized(markets){
			for(MyMarket mm : markets){
				if(mm.getMarket() == m){
					mm.outOfStock.add(order);
				}
			}
		}
		stateChanged();
	}
	public void msgAtGrill(){
		print("At the grill");
		atGrill.release();
		stateChanged();
	}
	public void msgAtCS(){
		print("at the cook station");
		atCs.release();
		stateChanged();
	}
	public void msgBackHome(){
		print("at home");
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		checkInventory();//check inventory every time we iterate to make sure were stocked
		if(!revolver.isEmpty()){
			takeFromStand();
		}
		for(int i=0; i<orders.size(); i++){
			if(orders.get(i).state == OrderState.Uncooked){
				orders.get(i).state = OrderState.Cooking;
				Restaurant2Order order = orders.get(i);
				int num = inventory.getInventory(order.order);
				if(num > 0){
					int pos = grill.getNextFree();
					goToGrill(pos, order);
					return true;
				}
				else if(num == 0){
					order.waiter.msgOutOfFood(order.order, (Restaurant2Customer) order.customer);
					return true;
				}
			}
		}
		
		if (offWork){
			goOffWork();
			return true; 
		}
		return false;
	}

	// Actions
	
	private void goOffWork(){
		offWork = false; 
		offWorkMess = 0; 
		print("GO OFF WORK IN RESTAURANT 2");
		this.person.msgGoOffWork(this,0);
	}
	
	
	private void clearPosition(final int position){
		clear.schedule(new TimerTask(){
			public void run(){
				cookGui.changeStationLabel(position, " ");
			}
		},
		5000);
	}
	private void takeFromStand(){
		orders.add(revolver.remove());
	}
	private void cookFood(final int position, final Restaurant2Order custOrder) {

		int cookTime = cookTimes.get(custOrder.getOrder());//get the cook time from our map
		timer.schedule(new TimerTask() {

			public void run() {
				plateOrder(position, custOrder);
			}
		},
		cookTime);
	}

	private void plateOrder(int position, Restaurant2Order order){

		DoTakeToCookStation(position, order.order);
		try {
			atCs.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.changeStationLabel(position, order.getOrderAbbr(order.order));
		order.getWaiter().msgFoodReady(order.getOrder(), (Restaurant2Customer)order.getCustomer());
		readyOrders.remove(order);
		DoLeaveGrill();
		clearPosition(position);
		//cookGui.changeStationLabel(position, " ");
		//stateChanged();
	}

	private void  checkInventory(){

		if(!foodlist.isEmpty()){
			marketCashier.MsgIwantFood(this, cashier, foodlist, 2);
			foodlist.clear();
			needTocookOrder = false;
			return;
		}
		List<String> marketOrders = new ArrayList<String>();
		Map<String, Integer> itemsAndQuantityNeeded = new HashMap<String, Integer>();
		int numSteak = inventory.getInventory("Steak");
		int numHam = inventory.getInventory("Hamburger");
		int numSalad = inventory.getInventory("Salad");
		int numRibs = inventory.getInventory("Ribs");
		int numChicken =inventory.getInventory("Chicken");
		int numPC = inventory.getInventory("Pound Cake");

		if(numSteak <= 3 && !inventory.orderStatus.get("Steak"))
		{
			marketOrders.add("Steak");
			itemsAndQuantityNeeded.put("Steak", 10 - numSteak);
			inventory.orderedFoodItem("Steak", true);
		}
		if(numHam <= 3 && !inventory.orderStatus.get("Hamburger"))
		{
			marketOrders.add("Hamburger");
			itemsAndQuantityNeeded.put("Hamburger", 10-numHam);
			inventory.orderedFoodItem("Hamburger", true);
		}
		if(numSalad <= 3 && !inventory.orderStatus.get("Salad"))
		{
			marketOrders.add("Salad");
			itemsAndQuantityNeeded.put("Salad", 10-numSalad);
			inventory.orderedFoodItem("Salad", true);
		}
		if(numRibs <= 3 && !inventory.orderStatus.get("Ribs"))
		{
			marketOrders.add("Ribs");
			itemsAndQuantityNeeded.put("Ribs", 10-numRibs);
			inventory.orderedFoodItem("Ribs", true);
		}
		if(numPC <= 3 && !inventory.orderStatus.get("Pound Cake"))
		{
			marketOrders.add("Pound Cake");
			itemsAndQuantityNeeded.put("Pound Cake", 10-numPC);
			inventory.orderedFoodItem("Pound Cake", true);
		}
		if(numChicken <= 3 && !inventory.orderStatus.get("Chicken"))
		{
			marketOrders.add("Chicken");
			itemsAndQuantityNeeded.put("Chicken", 10-numChicken);
			inventory.orderedFoodItem("Chicken", true);
		}
		//now we will take marketOrders and order the items from the markets
		if(!itemsAndQuantityNeeded.isEmpty()){
			synchronized(markets){
				for(MyMarket m : markets){
					Map<String, Integer> inStockAtThisMarket = m.whatsInStock(itemsAndQuantityNeeded);
					if(!inStockAtThisMarket.isEmpty()){
						m.getMarket().msgRestockMe(inStockAtThisMarket);
						for(Map.Entry<String, Integer> entry: inStockAtThisMarket.entrySet()){
							if(itemsAndQuantityNeeded.containsKey(entry)){
								itemsAndQuantityNeeded.remove(entry);
							}
						}
						if(itemsAndQuantityNeeded.isEmpty()){
							break;
						}
					}
				}
			}
		}
	}
	private void goToGrill(int position, Restaurant2Order o){
		DoGoToGrill(position);
		cookGui.changeText(o.order);
		try {
			atGrill.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.changeGrillLabel(position, o.getOrderAbbr(o.order));
		grill.add(position, o);
		inventory.reduce(o.order);
		cookFood(position, o);
		DoLeaveGrill();
	}
	private void DoGoToGrill(int position){
		cookGui.DoGoToGrill(position);
	}
	private void DoLeaveGrill(){
		cookGui.DoLeaveGrill();
		cookGui.changeText(" ");

	}
	private void DoTakeToCookStation(int position, String order){
		cookGui.DoTakeToCookStation(position, order);
		cookGui.changeGrillLabel(position, " ");

	}
	private class MyWaiter {
		private Restaurant2Waiter waiter;

		MyWaiter(Restaurant2Waiter waiter)
		{
			this.waiter = waiter;
		}
		public Restaurant2Waiter getWaiter(){
			return waiter;
		}
	}
	class Grill{

		public Map<Integer, Restaurant2Order> grill = new HashMap<Integer, Restaurant2Order>();
		//public List<> add a list here that would contain the grill guis and place the order string on the grill
		Grill()
		{
			grill.put(1, null);
			grill.put(2, null);
			grill.put(3, null);
			grill.put(4, null);
		}
		void add(int position, Restaurant2Order o)
		{
			grill.put(position, o);
		}
		Restaurant2Order remove(int position)
		{
			Restaurant2Order temp = grill.get(position);
			grill.remove(temp);
			return temp;
		}
		int getNextFree(){
			for(Map.Entry<Integer, Restaurant2Order> entry: grill.entrySet())
			{
				if(entry.getValue() == null)
				{
					return entry.getKey();
				}
			}
			return -1;
		}
	}
	public static class MyMarket {

		private MarketAgent market;
		public List<String> outOfStock = Collections.synchronizedList(new ArrayList<String>());

		MarketState state;
		private boolean couldNotFillOrder;
		private String name;

		MyMarket(MarketAgent m, String name)
		{
			this.name = name;
			market = m;
			couldNotFillOrder = false;
			state = MarketState.Idle;
		}
		public MarketAgent getMarket()
		{
			return market;
		}
		public void setStatus(boolean tf)
		{
			couldNotFillOrder = tf;
		}
		public boolean getStatus()
		{
			return couldNotFillOrder;
		}
		public String getName()
		{
			return name;
		}
		public List<String> checkOutOfStock(List<String> items)
		{
			List<String> itemsInStock = new ArrayList<String>();

			for(String item : items)
			{
				if(!outOfStock.contains(item))
				{
					itemsInStock.add(item);
				}
			}
			return itemsInStock;
		}
		public Map<String, Integer> whatsInStock(Map<String, Integer> items)
		{
			Map<String, Integer> itemsInStock = new HashMap<String, Integer>();
			for(Map.Entry<String, Integer> entry: items.entrySet())
			{
				if(!outOfStock.contains(entry.getKey()))
				{
					itemsInStock.put(entry.getKey(), entry.getValue());
				}
			}
			return itemsInStock;
		}
		public void setOutOfStock(String item)
		{
			if(!outOfStock.contains(item))
			{
				outOfStock.add(item);
			}
		}
	}

	private class Food {
		public Map<String, Boolean> orderStatus = new HashMap<String, Boolean>();
		public Map<String, Integer> foodList = new HashMap<String, Integer>();

		Food(int s, int hb, int r, int sal, int chix, int cake)
		{	
			foodList.put("Steak", s);
			foodList.put("Hamburger", hb);
			foodList.put("Ribs", r);
			foodList.put("Salad", sal);
			foodList.put("Chicken", chix);
			foodList.put("Pound Cake", cake);

			orderStatus.put("Steak", false);
			orderStatus.put("Hamburger", false);
			orderStatus.put("Ribs", false);
			orderStatus.put("Salad", false);
			orderStatus.put("Chicken", false);
			orderStatus.put("Pound Cake", false);
		}
		void reduce(String foodType)
		{
			int currentQuantity = foodList.get(foodType);
			foodList.put(foodType, currentQuantity--);
		}
		void add(String foodType, int quantity)
		{
			int currentQuantity = foodList.get(foodType);
			foodList.put(foodType, (currentQuantity + quantity));
		}
		int getInventory(String foodType)
		{
			return foodList.get(foodType);
		}
		void orderedFoodItem(String foodType, boolean value)
		{
			orderStatus.put(foodType, value);
		}
	}
	/*private class Order {
		Waiter waiter;
		Restaurant2CustomerRole customer;
		String order;
		Map<String, String> Abbr = new HashMap<String, String>();
		OrderState state;
		Order (Waiter waiter2, Restaurant2CustomerRole c, String o)
		{
			Abbr.put("Steak", "ST");
			Abbr.put("Hamburger", "B");
			Abbr.put("Salad", "SL");
			Abbr.put("Chicken", "C");
			Abbr.put("Ribs", "R");
			Abbr.put("Pound Cake", "PC");

			waiter = waiter2;
			customer = c;
			order = o;

			state = OrderState.Uncooked;

		}
		Waiter getWaiter(){
			return waiter;
		}
		Restaurant2CustomerRole getCustomer(){
			return customer;
		}
		String getOrder(){
			return order;
		}
		String getOrderAbbr(String full)
		{
			return Abbr.get(full);
		}

	}*/

	// FIX
	@Override
	public String getRoleName() {
		return this.getName();
	}
	@Override
	public void msgHereisYourFood(MarketTruck t, List<market.Food> fList) {
		t.msgGoBack();
		for(market.Food f : fList){
			inventory.add(f.choice, f.amount);
		}
	}
	@Override
	public void msgEmptyStock() {
		foodlist = new ArrayList<market.Food>();
		foodlist.add(new market.Food("Steak", 5));
		foodlist.add(new market.Food("Hamburger", 5));
		foodlist.add(new market.Food("Ribs", 5));
		needTocookOrder = true;
		stateChanged();
	}


	public void setMarketCashier(MarketCashierRole r) {
		marketCashier = r;
	}
	
	public utilities.Gui getGui(){
		return cookGui; 
	}
}

