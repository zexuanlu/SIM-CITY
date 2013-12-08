package restaurant2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;
import restaurant2.gui.Restaurant2WaiterGui;
import restaurant2.interfaces.Customer;
import restaurant2.interfaces.Waiter;
import agent.Agent;
import agent.Role;

public abstract class Restaurant2AbstractWaiterRole extends Role implements Waiter{
	static final int NTABLES = 4;

	Timer timer = new Timer();

	public List<MyCustomer> MyCustomers = new ArrayList<MyCustomer>();
	public List<Table> tables = new ArrayList<Table>();
	public Map<MyCustomer, String> toServe = new HashMap<MyCustomer, String>();
	public Map<MyCustomer, String> mcAlerts = new HashMap<MyCustomer, String>();

	public enum WaiterState {backHome, away, takingOrders, serving, Seating, goingOnBreak, onBreak, returnForNewOrder}; 
	//private enum WaiterEvent {none, seated, ordersTaken, served};
	//private WaiterEvent event = WaiterEvent.none;
	private WaiterState state = WaiterState.backHome;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	public Semaphore waitForOrder = new Semaphore(0, true);
	public Semaphore atCook = new Semaphore(0, true);
	public Semaphore serving = new Semaphore(0, true);
	public Semaphore onBreakSleep = new Semaphore(0, true);
	private boolean active = false;
	public Restaurant2WaiterGui waiterGui = null;
	protected Restaurant2HostRole host;
	protected Restaurant2CookRole cook;
	protected Restaurant2CashierRole cashier;
	public boolean onBreak = false;

	public Restaurant2AbstractWaiterRole (String name, Person p){
		super(p);
		this.name = name;
		active = true;
	}

	//Utilities
	public void setHost(Restaurant2HostRole h)
	{
		host = h;
	}
	public void setCook(Restaurant2CookRole c){
		cook = c;
	}
	public void setCashier(Restaurant2CashierRole cash)
	{
		cashier = cash;
	}
	public Restaurant2HostRole getHost()
	{
		return host;
	}
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	public boolean available(){
		if(state == WaiterState.backHome){
			return true;
		}
		else 
			return false;

	}
	public boolean isActive()
	{
		return active;
	}
	public Collection getTables() {
		return tables;
	}
	private boolean areThereMoreTasks()
	{
		if(MyCustomers.isEmpty())
		{
			return false;
		}
		else
			return true;
	}
	private void removeMyCustomer(Customer c)
	{
		for(int i=0; i < MyCustomers.size(); i++)
		{
			MyCustomer mc = MyCustomers.get(i);
			if(mc.getCustomer() == c)
			{
				MyCustomers.remove(mc);
			}
		}
	}
	public void setGui(Restaurant2WaiterGui gui) {
		waiterGui = gui;
	}

	public Restaurant2WaiterGui getGui() {
		return waiterGui;
	}
	
	// Messages
	public void msgBackHome()
	{
		if(state!=WaiterState.backHome)
		{
			state = WaiterState.backHome;
			print("home");
		}
		stateChanged();
	}

	public void msgLeavingTable(Customer cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				removeMyCustomer(cust);
			}
		}
		stateChanged();
	}
	public void msgLeavingTableNoOrder(Customer cust) {
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == cust)
			{
				mc.changeState("No order");
			}
		}
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
			}
		}
		stateChanged();
	}
	public void msgAtTable() {//from animation
		print("msgAtTable() called");
		atTable.release();
		stateChanged();
	}
	public void msgAtCook()
	{
		print("At the cook");
		atCook.release();
		if(state == WaiterState.serving)
		{
			serving.release();
		}
		waiterGui.DoLeaveCustomer();
		stateChanged();
	}
	public void msgReadyToOrder(Customer customer, int table)
	{
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == customer)
			{
				mc.changeState("readyToOrder");
			}
		}
		stateChanged();
	}
	public void msgOrder(Customer customer, String order)
	{
		//System.out.println("Order from "+customer.getName()+" for "+order);
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == customer)
			{
				mc.setOrder(order);
				mc.changeState("waitingForFood");
				waiterGui.changeText(order);
			}
		}
		waitForOrder.release();
		stateChanged();
	}
	public void msgFoodReady(String order, Customer customer)
	{
		System.out.println("picked up and order for "+order);
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == customer)
			{
				mc.changeState("toBeServed");
				toServe.put(mc, order);
			}
		}
		stateChanged();
	}
	public void msgOutOfFood(String foodType, Customer customer)
	{
		print("Please tell "+customer+" that we are out of "+foodType);
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == customer)
			{
				mc.changeState("reOrder");
			}
		}
		stateChanged();
	}
	public void msgSeatCustomer(Customer customer, Table table)
	{
		MyCustomer mc = new MyCustomer(customer, table.tableNumber);
		//print("going to table"+table.tableNumber+" with "+customer.getName());
		tables.add(table);
		MyCustomers.add(mc);
		stateChanged();
	}
	public void msgGoOnBreak()
	{
		onBreak = true;
		stateChanged();
	}
	public void msgHeresTheCheck(Customer c, Check check)
	{
		print("here's the check for "+c+" for $"+check.getCheck());
		for(MyCustomer mc : MyCustomers)
		{
			if(mc.getCustomer() == c)
			{
				mc.setCheck(check.getCheck());
			}
		}
		stateChanged();
	}

	public void returnFromBreak()
	{
		onBreakSleep.release();
		state = WaiterState.backHome;
		stateChanged();
	}

	//actions
	private void seatCustomer(MyCustomer customer, int table) {
		state = WaiterState.away;//change the state
		customer.changeState("beingSeated");
		Menu menu = new Menu();
		customer.getCustomer().msgSitAtTable(this, table, menu);
		DoSeatCustomer((Restaurant2CustomerRole) customer.getCustomer(), table);
		print("Seating " + customer + " at " + table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		customer.changeState("seated");

		waiterGui.DoLeaveCustomer();
	}
	private void leaveForBreak()
	{
		print("going on my break");
		onBreak = false;
		state = WaiterState.onBreak;
		waiterGui.changeText("Break Time");
		comeBackFromBreak();
		waiterGui.DoLeaveCustomer();
	}
	private void comeBackFromBreak()
	{
		int breakTime = 10000;
		timer.schedule(new TimerTask() {

			public void run() {
				back();
			}
		},
		breakTime);
	}
	private void back()
	{
		print("on my way back from break");
		host.msgBreakIsOver(this);
		waiterGui.changeText(" ");
	}
	private void getCheck(MyCustomer mc)
	{
		print("I'd like the check for "+mc.getCustomer());
		cashier.msgComputeCheck(mc.getCachedOrder(), this, mc.getCustomer());
		mc.changeState("waitingForCheck");
	}
	private void deliverCheck(MyCustomer mc)
	{
		print("Here is your check "+mc.getCustomer());
		mc.getCustomer().msgHeresYourCheck(mc.getCheck());
		mc.changeState("checkDelivered");
	}
	private void takeOrder(MyCustomer customer)
	{
		DoTakeOrder((Restaurant2CustomerRole) customer.getCustomer(), customer.tableAt());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		customer.getCustomer().msgWhatWouldYouLike();
		try {
			waitForOrder.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		state = WaiterState.away;
		print("order taken!");

	}
	private void returnForNewOrder(MyCustomer customer)
	{
		DoTakeFoodToCustomer(customer.tableAt());
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.getCustomer().msgReOrder();
		customer.changeState("none");

	}
	private void ordersToCook(){};
	
	private void serveCustomers(MyCustomer customer)
	{
		state = WaiterState.serving;
		if(toServe.containsKey(customer))
		{
			print("serving "+customer.getCustomer());
			waiterGui.DoTakeToCook();
			try {
				serving.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DoTakeFoodToCustomer(customer.tableAt());
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			customer.getCustomer().msgServed(toServe.get(customer));
			toServe.remove(customer);
			customer.changeState("served");
			waiterGui.changeText(" ");
			waiterGui.DoLeaveCustomer();
		}

		state = WaiterState.away;
	}
	//utilities 

	// The animation routines
	private void DoSeatCustomer(Restaurant2CustomerRole customer,int table) {

		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table); 
	}

	private void DoTakeOrder(Restaurant2CustomerRole customer, int table){
		print("Heading over to table "+table+" to take "+ customer.getName()+"'s order");
		waiterGui.DoGoTakeOrder(customer, table);
	}
	protected void DoTakeOrdersToCook()
	{
		print("Taking orders to le cook");
		waiterGui.DoTakeToCook();
	}
	private void DoTakeFoodToCustomer(int table){

		waiterGui.DoTakeFoodToCustomer(table);
		print("Taking orders to tables");
	}


	class MyCustomer {

		private Customer customer;
		private int tableAt;
		private boolean reOrder;
		private int check;
		private String order = "none";
		private String cachedOrder;//a hack for now
		private String state = "waitingToBeSeated";

		MyCustomer(Customer customer, int tableAt)
		{
			this.customer = customer;
			this.tableAt = tableAt;
		}
		public Customer getCustomer(){
			return customer;
		}
		public int tableAt(){
			return tableAt;
		}
		public String getOrder()
		{
			return order;
		}
		public String getCachedOrder()
		{
			return cachedOrder;
		}
		public void setOrder(String order)
		{
			this.order = order;
			if(!order.equals("none"))
			{
				this.cachedOrder = order;
			}
		}
		public void changeState(String nextState)
		{
			state = nextState;
		}
		public String getState()
		{
			return this.state;
		}
		public void needToReOrder(boolean t)
		{
			reOrder = t;
		}
		public boolean reOrder()
		{
			return this.reOrder;
		}
		public void setCheck(int c)
		{
			System.out.println("Setting the check for "+c);
			this.check = c;
		}
		public int getCheck()
		{
			return check;
		}
	}

}
