package restaurant4;

import agent.Role;

import java.util.*;

import person.interfaces.Person;
import restaurant4.interfaces.*;

/**
 * Restaurant Host Agent
 * 
 * Receives Customers, assigns them to Waiters, and keeps track of tables
 */
public class Restaurant4HostRole extends Role implements Restaurant4Host{
	static final int NTABLES = 3;//The number of tables
	//Used later to select the waiter
	private int waiterSelect = 0; 
	private int customerNum = 0;
	public List<MyCustomer> waitingCustomers
	= Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Collection<Table> tables;
	public List<MyWaiter> waiters
	= Collections.synchronizedList(new ArrayList<MyWaiter>());

	private String name;
	//Used to tell the customers if the restaurant is full
	private boolean informed = true;

	public Restaurant4HostRole(String name, Person pa) {
		super(pa);

		this.name = name;
		// Creates the tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void addWaiter(Restaurant4AbstractWaiter waiter){
		waiters.add(new MyWaiter(waiter));
		stateChanged();
	}

	public List<MyCustomer> getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	
	// Messages

	/**
	 * Receives this message when a customer wants to eat
	 * 
	 * @param cust the customer who wants to eat
	 */
	public void msgIWantFood(Restaurant4Customer cust) {
		waitingCustomers.add(new MyCustomer(cust));
		customerNum ++;
		informed = false;
		stateChanged();
	}

	/**
	 * Received when the customer leaves because the wait is too long
	 * 
	 * @param cust the customer who is leaving
	 */
	public void msgWaitTooLong(Restaurant4Customer cust) {
		waitingCustomers.remove(cust);
		stateChanged();
	}
	
	/**
	 * Received from a waiter who wants to go on break
	 * 
	 * @param waiter the waiter who wants to go on break
	 */
	public void msgIWantBreak(Restaurant4Waiter waiter){
		synchronized(waiters){
			for(MyWaiter w : waiters){
				if(w.waiter == waiter){
					w.s = state.wantBreak;
					stateChanged();
					return;
				}
			}
		}
	}
	
	/**
	 * Receives this message from a waiter when a customer has left
	 * 
	 * @param tableNum the number of the now free table
	 */
	public void msgTableFree(int tableNum) {
		synchronized(tables){
			for (Table table : tables) {
				if (table.tableNumber == tableNum) {
					print(table + " is Empty");
					table.setUnoccupied();
					stateChanged();
				}
			}
		}
	}
	
	/**
	 * Received from a waiter who is going off of break
	 * 
	 * @param waiter the waiter who is coming off of break
	 */
	public void msgEndingBreak(Restaurant4Waiter waiter){
		synchronized(waiters){
			for(MyWaiter w : waiters){
				if(w.waiter == waiter){
					w.s = state.working;
					stateChanged();
				}
			}
		}
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(waitingCustomers){
			for(MyCustomer customer : waitingCustomers){
				if(customer.s == custState.arrived){
					positionCustomer(customer);
					return true;
				}
			}
		}
		synchronized(waiters){
			for(MyWaiter w : waiters){
				if(w.s == state.wantBreak){
					checkBreak(w);
					return true;
				}
			}
		}
		/**
		 * Checks the tables, customers, and waiters to see if one of each is free. 
		 * If they are, assigns the customer and table to a waiter
		 */
		synchronized(tables){
			for (Table table : tables) {
				if (!table.isOccupied()) {
					synchronized(waitingCustomers){
						for(MyCustomer customer : waitingCustomers){
							if (customer.s == custState.positioned) {
								if(!waiters.isEmpty()){
									if(waiterSelect >= waiters.size()){
										waiterSelect = 0;
									}
									//This uses waiterSelect to cycle through the list.
									//When it gets to the end, it jumps back to 0
									while(waiters.get(waiterSelect).s == state.onBreak){
										waiterSelect++;						
										if(waiterSelect >= waiters.size()){
											waiterSelect = 0;
										}
									}
									seatCustomer(customer, table, (waiters.get(0)).waiter);//the action
									return true;
								}
							}
						}
					}
				}
			}
		}
		//Tells a customer that the restaurant is full
		if(!waitingCustomers.isEmpty() && !informed){
			restaurantFull(waitingCustomers.get(waitingCustomers.size()-1).customer);
			return true;
			
		}
		return false;
	}

	// Actions

	//Tells customer customer that the restaurant is full
	private void restaurantFull(Restaurant4Customer customer){
		customer.msgRestaurantFull();
		Do("The restaurant is full");
		informed = true;
	}
	/**
	 * Tells the waiter to seat the customer and changes the state of the customer and the table
	 * 
	 * @param customer the customer to be seated
	 * @param table the table to seat the customer at
	 * @param waiter the waiter assigned to the customer
	 */
	private void seatCustomer(MyCustomer customer, Table table, Restaurant4Waiter waiter) {
		//Increments this to insure that the list of waiters is cycled through
		waiterSelect++;
		table.setOccupant(customer.customer);
		waitingCustomers.remove(customer);
		waiter.msgSeatCustomer(customer.customer, table.tableNumber, this, "Customer " + customerNum);
	}

	private void positionCustomer(MyCustomer customer){
		if(customerNum > 14)
			customerNum = 1;
		
		((Restaurant4CustomerRole)customer.customer).getGui().DoGoToLocation("Customer " + customerNum);
		customer.s = custState.positioned;
	}
	
	/**
	 * Finds out if a waiter can go on break or not and lets him know
	 * 
	 * @param waiter the waiter to be checked
	 */
	private void checkBreak(MyWaiter waiter){
		int count = 0; 
		synchronized(waiters){
			for(MyWaiter w : waiters){
				if(w.s == state.onBreak)
					count++;
			}
		}
		if(count < (waiters.size()-1)){
			System.out.println("Letting " + waiter.waiter + " go on break");
			waiter.s = state.onBreak;
			waiter.waiter.msgCanGoOnBreak();
		}
		else{			
			System.out.println("Denying break for " + waiter.waiter);
			waiter.s = state.working;
			waiter.waiter.msgCannotGoOnBreak();
		}
	}
	// The animation DoXYZ() routines

	//utilities

	public String toString() {
		return "host " + getName();
	}
	
	/**
	 * The table class
	 * Holds a customer and a table number
	 */
	private class Table {
		Restaurant4Customer occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Restaurant4Customer cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	
	private class MyWaiter{
		Restaurant4Waiter waiter;
		state s;
		MyWaiter(Restaurant4Waiter waiter){
			this.waiter = waiter;
			s = state.working;
		}
	}
	enum state {working, onBreak, wantBreak}
	
	private class MyCustomer{
		Restaurant4Customer customer;
		custState s;
		MyCustomer(Restaurant4Customer customer){
			this.customer = customer;
			s = custState.arrived;
		}
	}
	enum custState {arrived, positioned, assigned}
	@Override
	public String getRoleName() {
		return "Restaurant 4 Host";
	}
}

