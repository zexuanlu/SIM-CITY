package restaurant4;

import agent.Agent;
import restaurant4.gui.Restaurant4WaiterGui;
import restaurant4.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Waiter Agent
 * 
 * Takes the customer to the table, takes their order
 * Gives it to the cook, takes the food back to the customer
 * and lets the host know when the customer leaves
 */
public class Restaurant4WaiterRole extends Agent implements Waiter{
	static final int NTABLES = 3;
	public List<MyCustomer> customers
	= Collections.synchronizedList(new ArrayList<MyCustomer>());
	
	private Restaurant4HostRole host = null;
	private Restaurant4CookRole cook = null;
	private Restaurant4CashierRole cashier = null;

	private String name;
	
	private Restaurant4Menu m = new Restaurant4Menu();
	//Semaphores used to keep the agent from getting ahead of the gui
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCook = new Semaphore(0,true);
	private Semaphore atHost = new Semaphore(0, true);
	private Semaphore atCashier = new Semaphore(0, true);
	private Semaphore ordering = new Semaphore(0, true);
	private Semaphore atCust = new Semaphore(0, true);

	private breakState s;
	public Restaurant4WaiterGui waiterGui = null;

	public Restaurant4WaiterRole(String name, Restaurant4HostRole host) {
		super();
		this.host = host;
		this.name = name;
	}

	public void setCook(Restaurant4CookRole cook) {
		this.cook = cook;
	}
	public String getName() {
		return name;
	}

	public List<MyCustomer> getcustomers() {
		return customers;
	}

	enum breakState {working, wantBreak, willBreak, onBreak, askedBreak, endBreak, deniedBreak};
	// Messages

	/**
	 * Received from the host telling the waiter to seat a customer at a table
	 * 
	 * @param cust the customer to be seated
	 * @param table the table number to seat the customer at
	 * @param h the host agent
	 */
	public void msgSeatCustomer(Restaurant4CustomerRole cust, int table, Restaurant4HostRole h, int x, int y) {
		customers.add(new MyCustomer(cust, table, x, y));
		host = h;
		stateChanged();
	}

	/** 
	 * Received from the customer when he leaves the table
	 * 
	 * @param cust the customer leaving
	 */
	public void msgLeavingTable(Restaurant4CustomerRole cust) {
		for(MyCustomer customer : customers){
			if(customer.customer == cust){
				customer.s = state.left;
				stateChanged();
				return;
			}
		}
	}

	public void msgAtCust() {
		print("Arrived at the customer");
		atCust.release();
		stateChanged();
	}
	
	//Received from the gui when it arrives at the host
	public void msgAtHost() {
		print("Arrived at the host");
		atHost.release();
		stateChanged();
	}
	
	//Received from the gui when it arrives at the table
	public void msgAtTable() {
		print("Arrived at the table");
		atTable.release();
		stateChanged();
	}
	
	//Received from the gui when it arrives at the cook
	public void msgAtCook(){
		print("Arrived at the cook");
		atCook.release();
		stateChanged();
	}

	//Received from the gui when it arrives at the cashier
	public void msgAtCashier(){
		print("Arrived at the cashier");
		atCashier.release();
		stateChanged();
	}
	/**
	 * Received from a customer when they are ready to order
	 * 
	 * @param c the customer who wants to order
	 */
	public void msgReadyToOrder(Restaurant4CustomerRole c){
		for(MyCustomer customer : customers){
			if(customer.customer == c){
				customer.s = state.readyToOrder;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Received from the customer, telling the waiter his order
	 * 
	 * @param c the customer who is ordering
	 * @param choice the customer's order
	 */
	public void msgHereIsOrder(Restaurant4CustomerRole c, String choice){
		print(c + " has ordered " + choice);
		for(MyCustomer customer : customers){
			if(customer.customer == c){
				customer.s = state.ordered;
				customer.choice = choice;
				ordering.release();
				stateChanged();
			}
		}
	}
	
	/**
	 * Received from the cook when an order is ready
	 * 
	 * @param table the table to take the order to
	 * @param choice the item that is ready
	 */
	public void msgOrderReady(int table, String choice, int grillNum){
		for(MyCustomer cust : customers){
			if(cust.table == table){
				cust.grillNum = grillNum;
				cust.s = state.foodReady;
				stateChanged();
			}
		}
	}
	
	//Received from the gui telling the waiter to try to go on break
	public void msgWantToBreak(){
		s = breakState.wantBreak;
		stateChanged();
	}
	
	//Received from the host telling the waiter that he can go on break
	public void msgCanGoOnBreak(){
		s = breakState.willBreak;
		stateChanged();
	}
	
	//Received from the host telling the waiter that he cannot go on break
	public void msgCannotGoOnBreak(){
		s = breakState.deniedBreak;
		stateChanged();
	}
	
	//Received from the gui when the waiter's break is over
	public void msgEndOfBreak(){
		s = breakState.endBreak;
		stateChanged();
	}
	
	/**
	 * Received from the cook when an item is out and the waiter requested it
	 * 
	 * @param food the item that is out
	 * @param table the table of the customer who ordered it
	 */
	public void msgOutOfItem(String food, int table){
		m.remove(food);
		Do("Menu contains " + m.options.size() + " choices");
		for(MyCustomer c : customers){
			if(c.table == table){
				c.s = state.reOrder;
				stateChanged();
			}
		}
	}
	
	/**
	 * Received from a customer when he is ready to pay
	 * 
	 * @param cust the customer who is ready to pay
	 */
	public void msgINeedCheck(Restaurant4CustomerRole cust){
		for(MyCustomer c : customers){
			if(c.customer == cust){
				c.s = state.doneEating;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Received from the cashier when the check is ready
	 * 
	 * @param cust the customer who requested the check
	 * @param price the price of the check (the check itself)
	 */
	public void msgHereIsCheck(Customer cust, double price){
		for(MyCustomer c : customers){
			if(c.customer == cust){
				c.price = price;
				c.s = state.checkReady;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		try{
			//Wants to go on break
			if(s == breakState.wantBreak){
				askAboutBreak();
				return true;
			}
			//Denied break
			if(s == breakState.deniedBreak){
				deniedBreak();
				return true;
			}
			//Break is over
			if(s == breakState.endBreak){
				endBreak();
				return true;
			}
			//If there is a customer who has left the restaurant
			for (MyCustomer customer : customers) {
				if(customer.s == state.left){
					emptyTable(customer);
					return true;
				}
			}
			//If there is a customer who's check is ready
			for(MyCustomer customer : customers){
				if(customer.s == state.checkReady){
					sendCheck(customer);
					return true;
				}
			}
			//If there is a customer who is done eating
			for(MyCustomer customer : customers) {
				if(customer.s == state.doneEating){
					requestCheck(customer);
					return true;
				}
			}
			
			//If a customer's food is ready
			for (MyCustomer customer : customers) {
				if(customer.s == state.foodReady){
					giveFood(customer);
					return true;
				}
			}
			//If there is a customer who needs to reorder
			for (MyCustomer customer : customers){
				if(customer.s == state.reOrder){
					reOrder(customer);
					return true;
				}
			}
			//If a customer has ordered
			for (MyCustomer customer : customers) {
				if(customer.s == state.ordered){
					giveOrderToCook(customer);
					return true;
				}
			}
			//If a customer is ready to order
			for (MyCustomer customer : customers) {
				if(customer.s == state.readyToOrder){
					takeOrder(customer);
					return true;
				}
			}
			//If there is a customer who is waiting
			for (MyCustomer customer : customers) {
				if(customer.s == state.waiting){
					seatCustomer(customer);
					return true;
				}
			}
			//If customers is empty, refresh the menu
			if(customers.isEmpty())
				m = new Restaurant4Menu();
			//if all customers are gone, go on break
			if(s == breakState.willBreak && customers.isEmpty()){
				goOnBreak();
				return false;
			}
		}
		catch(ConcurrentModificationException cme){
			return false;
		}
		return false;
	}

	// Actions

	/**
	 * Messages the customer to go to the seat, and messages the gui to go there as well
	 * 
	 * @param customer the customer to be seated
	 */
	private void seatCustomer(MyCustomer customer) {
		waiterGui.DoGoToCust(customer.x, customer.y);
		try{
			atCust.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		customer.customer.msgSitAtTable(customer.table, this, new Restaurant4Menu());
		DoSeatCustomer(customer.customer, customer.table);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.s = state.sitting;
		waiterGui.DoLeaveCustomer();
	}
	
	/**
	 * Goes to the customers table and tells him he is ready to take the order
	 * 
	 * @param customer the customer whose table he is going to
	 */
	private void takeOrder(MyCustomer customer){
		DoGoToTable(customer.table);
		try {
			atTable.acquire();
		} 
		catch (InterruptedException e) {			
			e.printStackTrace();
		}
		customer.customer.msgReadyForOrder();
		print("Customer is ordering");
		try{
			ordering.acquire();
		}
		catch (InterruptedException e) {
		
			e.printStackTrace();
		}
	}
	
	/**
	 * Gives the customer's order to the cook
	 * 
	 * @param customer the customer who has ordered
	 */
	private void giveOrderToCook(MyCustomer customer){
		waiterGui.DoGoToCook();
		print("Going to the Cook");
		try{
			atCook.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.s = state.sitting;
		cook.msgMakeFood(customer.choice, this, customer.table);
		waiterGui.DoGoHome();
	}
	
	/**
	 * Gets the food from the cook and gives it to the customer
	 * 
	 * @param customer the customer whose food is ready
	 */
	private void giveFood(MyCustomer customer){
		DoGetFoodFromCook(customer.grillNum); //Animation
		try{
			atCook.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cook.msgGettingFood(customer.choice, customer.table);
		DoGoToTable(customer.table);
		waiterGui.DoBringFoodToTable(customer.table, customer.choice);
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		customer.customer.msgHereIsFood(customer.choice);
		customer.s = state.sitting;
		waiterGui.DoLeaveCustomer();
	}
	
	/**
	 * Tells the host that a table is empty
	 * 
	 * @param customer the customer who has left
	 */
	private void emptyTable(MyCustomer customer){
		DoInformHost(customer.table); //Animation
		waiterGui.DoLeaveCustomer();
		host.msgTableFree(customer.table);
		customers.remove(customer);
	}

	//Asks the host if the waiter can go on break
	private void askAboutBreak(){
		System.out.println("I want to go on Break");
		s = breakState.askedBreak;
		host.msgIWantBreak(this);
	}
	
	//Goes on break and tells the gui to
	private void goOnBreak(){
		System.out.println("Going on Break");
		s = breakState.onBreak;
		waiterGui.goOnBreak();
	}
	
	//Ends the break and tells the host
	private void endBreak(){
		System.out.println("Ending break");
		s = breakState.working;
		host.msgEndingBreak(this);
	}
	
	//Tells the gui to switch the button back to available
	private void deniedBreak(){
		waiterGui.noBreak();
		s = breakState.working;
	}
	
	/**
	 * Goes and gets the reorder from the customer
	 * 
	 * @param c the customer who is reordering
	 */
	private void reOrder(MyCustomer c){
		waiterGui.DoGoToTable(c.table);
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		c.customer.msgReOrder(m);
	}
	
	/**
	 * Requests a check from the cashier
	 * 
	 * @param cust the customer who requested the check
	 */
	private void requestCheck(MyCustomer cust){
		DoGoToCashier();
		try{
			atCashier.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cust.s = state.sitting;
		cashier.msgINeedCheck(cust.choice, cust.customer, this);
	}
	
	/**
	 * Gives the check to a customer
	 * 
	 * @param cust the customer who requested the check
	 */
	private void sendCheck(MyCustomer cust){
		DoGoToTable(cust.table);
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cust.s = state.sitting;
		cust.customer.msgHereIsCheck(cust.price, cashier);
		waiterGui.DoLeaveCustomer();
	}
	// The animation DoXYZ() routines
	
	//Go to the table
	private void DoSeatCustomer(Restaurant4CustomerRole customer, int table) {
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table); 

	}

	private void DoGoToCashier(){
		Do("Getting check from Cashier");
		waiterGui.DoGoToCashier();
	}
	//Go to the cook
	private void DoGetFoodFromCook(int grillNum){
		print("Getting food from Cook");
		waiterGui.DoGoToGrill(grillNum);
	}
	
	//Go to the table
	private void DoGoToTable(int table){
		print("Going to table " + table);
		waiterGui.DoGoToTable(table);
		
	}
	
	private void DoInformHost(int table){
		print("Telling " + host + " that table " + table + " is free");
	}
	
	//utilities

	public void setCashier(Restaurant4CashierRole cash){
		cashier = cash;
	}
	public void setGui(Restaurant4WaiterGui gui) {
		waiterGui = gui;
	}

	public Restaurant4WaiterGui getGui() {
		return waiterGui;
	}
	
	public String toString() {
		return "waiter " + getName();
	}
	
	/**
	 * MyCustomer class
	 * 
	 * Contains a customer, a table number, a state, and a choice of food
	 */
	private class MyCustomer{
		Restaurant4CustomerRole customer;
		int table;
		state s;
		String choice;
		double price;
		int grillNum;
		int x;
		int y;
		MyCustomer(Restaurant4CustomerRole cust, int tableNum, int x, int y){
			customer = cust;
			table = tableNum;
			this.x = x;
			this.y = y;
			s = state.waiting;
			choice = "none";
		}
	}
	//The states of the customer
	enum state {waiting, sitting, readyToOrder, ordering, foodReady, doneEating, 
		ordered, left, checkReady, reOrder}
}

