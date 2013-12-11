package restaurant3;

import agent.Role;
import person.PersonAgent;
import restaurant1.shareddata.Order;
import restaurant1.shareddata.Restaurant1RevolvingStand;
import restaurant3.interfaces.Restaurant3Waiter;
import restaurant3.interfaces.Restaurant3Cook;
import restaurant3.Restaurant3CashierRole;
import restaurant3.gui.Restaurant3CookGui;
import market.Food;
import market.MarketCashierRole;
import market.interfaces.MarketTruck;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

public class Restaurant3CookRole extends Role implements Restaurant3Cook{
	//MEMBER DATA
	String name;
	public boolean offWork; 
	int marketNum = 1;
	boolean startTimer = true;
	boolean restock = false;
	public Restaurant3RevolvingStand revStand = new Restaurant3RevolvingStand();
	
	//Agent references
	public Restaurant3CashierRole cashier;
	MarketCashierRole marketCashier;
	MarketTruck truck;
	
	//Boolean for truck
	boolean sendTruckBack = false;
	
	//GUI references
	public Restaurant3CookGui cookGui;
	
	public void msgGoOffWork(){
		offWork = true;
		stateChanged();
	}
	
	//Private class for food information
	private class MyFood{
		String type;
		int cookTime;
		int amount;
		int max;
		int numToOrder;
		
		MyFood(String t, int cT, int amt, int mx){
			type = t;
			cookTime = cT*1000;
			amount = amt;
			max = mx;
		}
	}
	
	//Order list
	List<Restaurant3Order> orders = new ArrayList<Restaurant3Order>();
	
	//Food inventory
	Map<String, MyFood> foodInventory = new HashMap<String, MyFood>();
	
	//Restock list
	public List<Food> restockList = Collections.synchronizedList(new ArrayList<Food>());
	
	//Semaphore for animation
	private Semaphore atFr = new Semaphore(0, true);
	
	//Timer for cooking
	Timer cookTimer = new Timer();
	
	//Timer to check revolving stand
	Timer checkTimer = new Timer();
	
	//CONSTRUCTOR *****************************************
	public Restaurant3CookRole(String name, PersonAgent pa) {
		super(pa);
		this.name = name;
		roleName = "Restaurant 3 Cook";
		
		//Initialize inventory
		foodInventory.put("Steak", new MyFood("Steak", 4, 10, 10));
		foodInventory.put("Pizza", new MyFood("Pizza", 3, 10, 10));
		foodInventory.put("Chicken", new MyFood("Chicken", 2, 10, 10));
		foodInventory.put("Salad", new MyFood("Salad", 1, 10, 10));
	}
	
	//HELPER METHODS **************************************
	public String getName(){
		return name;
	}
	
	public Restaurant3RevolvingStand getRevStand(){
		return this.revStand;
	}
	
	public String getRoleName(){
		return roleName;
	}
	
	public void setGui(Restaurant3CookGui ckg){
		cookGui = ckg;
	}
	
	public void setCashier(Restaurant3CashierRole c){
		cashier = c;
	}
	
	public void setMarketCashier(MarketCashierRole c){
		this.marketCashier = c;
	}

	//MESSAGES ********************************************
	@Override
	public void msgNewOrder(Restaurant3Waiter w, int table, String choice) {
		print(name + ": received new order from waiter " + w.getName());
		orders.add(new Restaurant3Order(w, choice, table));
		stateChanged();
	}
	
	public void msgHereisYourFood(MarketTruck t, List<Food> fList){
		print(name + " received stock from market " + marketNum);
		this.truck = t;
		for(Food f : fList){
			foodInventory.get(f.choice).amount += f.amount;
			print("Stock: " + f.choice + " = " + foodInventory.get(f.choice).amount);
		}
		sendTruckBack = true;
		stateChanged();
	}
	
	@Override
	public void msgEmptyStock() {
		print(name + ": need to restock");
		for(Entry<String, MyFood> e : foodInventory.entrySet()){
			e.getValue().amount = 3;
		}
		restock = true;
	}

	public void msgAtFrRelease(){
		atFr.release();
	}
	
	//SCHEDULER *****************************************
	@Override
	public boolean pickAndExecuteAnAction() {
		if(startTimer){
			startCheckTimer();
			return true;
		}
		//Send truck back
		if(sendTruckBack == true) {
			truckBack();
			return true;
		}
		//Check if we need to restock
		if(restock){
			for(Entry<String, MyFood> f : foodInventory.entrySet()){
				checkRestock(f);
			}
			return true;
		}
		//Check if there is an order on the revolving stand
		if(!revStand.isEmpty()){
			takeOrderFromStand();
			return true;
		}
		//Check if food needs to be restocked
		if(!restockList.isEmpty()){
			restockFood();
			return true;
		}
		//Check if an order is pending
		for(Restaurant3Order o : orders){
			if(o.state == Restaurant3Order.oState.pending){
				cookOrder(o);
				return true;
			}
		}	
		//Check if an order is cooked
		for(Restaurant3Order o : orders){
			if(o.state == Restaurant3Order.oState.cooked){
				sendOrderToTable(o);
				return true;
			}
		}
		
		if (offWork){
			goOffWork();
			return true; 
		}
		return false;
	}

	
	
	public void goOffWork(){
		offWork = false; 
		this.person.msgGoOffWork(this, 0);
	}
	//ACTIONS *************************************
	public void startCheckTimer(){
		checkTimer.scheduleAtFixedRate(new TimerTask(){
			public void run(){
				stateChanged();
			}
		}, 0, 500);
		startTimer = false;
	}
	
	public void takeOrderFromStand(){
		Restaurant3Order o = null;
		o = revStand.removeOrder();
		System.out.println(o.choice);
		if(o != null){
			orders.add(o);
		}
	}
	
	public void cookOrder(Restaurant3Order o){
		print(name + " cooking order for table " + o.tableNum);
		//Check if food needs to be restocked
		for(Entry<String, MyFood> f : foodInventory.entrySet()){
			if(f.getKey().equals(o.choice)){
				f.getValue().amount--;
				checkRestock(f);
			}
		}
		o.state = Restaurant3Order.oState.cooking;
		DoCooking(o);
	}
	
	public void sendOrderToTable(Restaurant3Order o){
		print(name + ": order for table " + o.tableNum + " cooked. Sending out.");
		DoSendOrder();
		orders.remove(o);
		o.wtr.msgOrderReady(o.tableNum, o.choice);
	}
	
	public void restockFood(){
		print(name + ": restocking food");
		marketCashier.MsgIwantFood(this, cashier, restockList, marketNum);
		restockList.clear();
	}
	
	public void truckBack(){
		print(name + ": sending market truck back");
		truck.msgGoBack();
		sendTruckBack = false;
	}
	
	public void checkRestock(Entry<String, MyFood> f){
		if(f.getValue().amount <= 3){
			print(name + ": need to restock " + f.getKey());
			restockList.add(new Food(f.getKey(), (f.getValue().max - f.getValue().amount)));
		}
	}
	
	//DO METHODS ****************************************
	public void DoCooking(final Restaurant3Order o){
		cookGui.DoGoToFridge();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		//NON NORM CODE HERE
		cookGui.DoGoToGrill();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}

		cookTimer.schedule(new TimerTask(){
			public void run(){
				o.state = Restaurant3Order.oState.cooked;
				stateChanged();
			}
		}, foodInventory.get(o.choice).cookTime);
	}
	
	public void DoSendOrder(){
		cookGui.DoGoToOrderStand();	//GUI CODE
		atFr.drainPermits();
		try{
			atFr.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public utilities.Gui getGui(){
		return cookGui;
	}
}
