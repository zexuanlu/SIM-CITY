package restaurant4;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant4.gui.Restaurant4WaiterGui;
import restaurant4.interfaces.*;
import agent.Role;
import person.interfaces.Person;

public abstract class Restaurant4AbstractWaiter extends Role implements Restaurant4Waiter {

	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	protected Restaurant4Host host = null;
	protected Restaurant4CookRole cook = null;
	protected Restaurant4CashierRole cashier = null;
	protected String name;
	protected Restaurant4Menu menu = new Restaurant4Menu();
	public Restaurant4WaiterGui gui;
	protected boolean endOfDay = false;
	protected breakState bs = breakState.none;
	protected Semaphore movement = new Semaphore(0, true);
	protected Semaphore ordering = new Semaphore(0, true);
	
	public Restaurant4AbstractWaiter(Person pa) {
		super(pa);
	}
	
	//Messages
	public void msgSeatCustomer(Restaurant4Customer cust, int table, Restaurant4Host h, String location) {
		MyCustomer temp = new MyCustomer(cust, table, location);
		temp.s = state.waiting;
		customers.add(temp);
		System.err.println("Here I Am");
		host = h;
		stateChanged();
	}
	
	public void msgLeavingTable(Restaurant4Customer cust) {
		for(MyCustomer customer : customers){
			if(customer.c == cust){
				customer.s = state.left;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgReadyToOrder(Restaurant4Customer cust){
		for(MyCustomer customer : customers){
			if(customer.c == cust){
				customer.s = state.readyToOrder;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgHereIsOrder(Restaurant4Customer cust, String choice){
		for(MyCustomer customer : customers){
			if(customer.c == cust){
				ordering.release();
				customer.s = state.ordered;
				customer.choice = choice;
				stateChanged();
			}
		}
	}
	
	public void msgOrderReady(int table, String choice, String location){
		for(MyCustomer cust : customers){
			if(cust.table == table){
				cust.location = location;
				cust.s = state.foodReady;
				stateChanged();
			}
		}
	}
	
	public void msgOutOfItem(String food, int table){
		menu.remove(food);
		for(MyCustomer c : customers){
			if(c.table == table){
				c.s = state.reOrder;
				stateChanged();
			}
		}
	}
	
	public void msgINeedCheck(Restaurant4Customer cust){
		for(MyCustomer customer : customers){
			if(customer.c == cust){
				customer.s = state.doneEating;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgHereIsCheck(Restaurant4Customer cust, double price){
		for(MyCustomer customer : customers){
			if(customer.c == cust){
				customer.price = price;
				customer.s = state.checkReady;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgWorkDayOver(){
		endOfDay = true;
		stateChanged();
	}
	
	//Received from the gui telling the waiter to try to go on break
	public void msgWantToBreak(){
		bs = breakState.wantBreak;
		stateChanged();
	}
	
	//Received from the host telling the waiter that he can go on break
	public void msgCanGoOnBreak(){
		bs = breakState.willBreak;
		stateChanged();
	}
	
	//Received from the host telling the waiter that he cannot go on break
	public void msgCannotGoOnBreak(){
		bs = breakState.deniedBreak;
		stateChanged();
	}
	
	//Received from the gui when the waiter's break is over
	public void msgEndOfBreak(){
		bs = breakState.endBreak;
		stateChanged();
	}
	
	public void msgAtDestination(){
		movement.release();
		stateChanged();
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		Do("Scheduler fun time with " + customers.size() + " customers");
		if(customers.size() != 0)
			Do("State is" + customers.get(0).s);
		try{
			//Wants to go on break
			if(bs == breakState.wantBreak){
				askAboutBreak();
				return true;
			}
			//Denied break
			if(bs == breakState.deniedBreak){
				deniedBreak();
				return true;
			}
			//Break is over
			if(bs == breakState.endBreak){
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
					Do("Seating customer");
					return true;
				}
			}
			//If customers is empty, refresh the menu
			if(customers.isEmpty())
				menu = new Restaurant4Menu();
			//if all customers are gone, go on break
			if(bs == breakState.willBreak && customers.isEmpty()){
				goOnBreak();
				return false;
			}
			if(customers.isEmpty() && endOfDay){
				workDayOver();
				return true;
			}
		}
		catch(ConcurrentModificationException cme){
			return false;
		}
		return false;
	}
	
	protected void giveOrderToCook(MyCustomer customer) {
		//Implemented in the lower classes
	}

	protected void seatCustomer(MyCustomer customer) {
		gui.GoToLocation(customer.location);
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		customer.c.msgSitAtTable(customer.table, this, new Restaurant4Menu());
		gui.GoToLocation("Table " + customer.table);
		movement.drainPermits();
		try {
			movement.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.s = state.sitting;
		gui.GoToLocation("Home");
	}
	
	protected void takeOrder(MyCustomer customer){
		gui.GoToLocation("Table " + customer.table);
		movement.drainPermits();
		try {
			movement.acquire();
		} 
		catch (InterruptedException e) {			
			e.printStackTrace();
		}
		customer.c.msgReadyForOrder();
		ordering.drainPermits();
		try{
			ordering.acquire();
		}
		catch (InterruptedException e) {
		
			e.printStackTrace();
		}
	}
	
	private void giveFood(MyCustomer customer){
		gui.GoToLocation(customer.location);
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cook.msgGettingFood(customer.choice, customer.table);
		gui.GoToLocation("Table " + customer.table);
		gui.carryFood(customer.choice);
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		gui.carryFood(false);
		customer.c.msgHereIsFood(customer.choice);
		customer.s = state.sitting;
		gui.GoToLocation("Home");
	}
	
	private void emptyTable(MyCustomer customer){
		gui.GoToLocation("Host"); //Animation
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		host.msgTableFree(customer.table);
		customers.remove(customer);
		gui.GoToLocation("Home");
	}
	
	//Asks the host if the waiter can go on break
	private void askAboutBreak(){
		bs = breakState.none;
		host.msgIWantBreak(this);
	}
	
	//Goes on break and tells the gui to
	private void goOnBreak(){
		bs = breakState.onBreak;
		gui.goOnBreak();
	}
	
	//Ends the break and tells the host
	private void endBreak(){
		bs = breakState.none;
		host.msgEndingBreak(this);
	}
	
	//Tells the gui to switch the button back to available
	private void deniedBreak(){
		gui.noBreak();
		bs = breakState.none;
	}
	
	private void reOrder(MyCustomer customer){
		gui.GoToLocation("Table " + customer.table);
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		customer.c.msgReOrder(menu);
	}
	
	private void requestCheck(MyCustomer cust){
		gui.GoToLocation("Cashier");
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		cust.s = state.sitting;
		cashier.msgINeedCheck(cust.choice, cust.c, this);
	}
	
	private void sendCheck(MyCustomer cust){
		gui.GoToLocation("Table " + cust.table);
		movement.drainPermits();
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cust.s = state.sitting;
		cust.c.msgHereIsCheck(cust.price, cashier);
		gui.GoToLocation("Home");
	}
	
	private void workDayOver(){
		cashier.msgWorkDayOver();
		cook.msgWorkDayOver();
		gui.GoToLocation("Home");
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		endOfDay = false;
		getPerson().msgGoOffWork(this, 0.00);
		gui.setPresent(false);
	}
	
	//Getters and Setters
	public Restaurant4Host getHost() {
		return host;
	}

	public void setHost(Restaurant4Host host) {
		this.host = host;
	}

	public Restaurant4CookRole getCook() {
		return cook;
	}

	public void setCook(Restaurant4CookRole cook) {
		this.cook = cook;
	}

	public Restaurant4CashierRole getCashier() {
		return cashier;
	}

	public void setCashier(Restaurant4CashierRole cashier) {
		this.cashier = cashier;
	}

	public Restaurant4WaiterGui getGui() {
		return gui;
	}


	public void setGui(Restaurant4WaiterGui gui) {
		this.gui = gui;
	}

	public static class MyCustomer {
		Restaurant4Customer c;
		int table;
		String location;
		String choice;
		double price;
		state s;
		
		MyCustomer(Restaurant4Customer c, int table, String location){
			this.location = location;
			this.c = c;
			this.table = table;
		}
	}
	protected enum state {waiting, sitting, readyToOrder, ordering, foodReady, doneEating, 
		ordered, left, checkReady, reOrder}
	
	protected enum breakState {none, wantBreak, willBreak, onBreak, endBreak, deniedBreak};
}	

