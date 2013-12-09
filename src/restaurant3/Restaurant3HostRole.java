package restaurant3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import agent.Agent;
import restaurant3.interfaces.Customer;
import restaurant3.interfaces.Waiter;

public class Restaurant3HostRole extends Agent {
	//MEMBER DATA
	public static final int NTABLES = 3;
	private String name;
	private int nextWaiter = 0;
	private Semaphore atTable = new Semaphore(0,true);
	
	//Private class to keep track of tables at restaurant
	private class Table{
		boolean isOccupied = false;
		int tableNum;
		
		Table(int tNum){
			tableNum = tNum;
		}
		
		boolean isOccupied() {
			return isOccupied;
		}

		public String toString() {
			return ("table " + tableNum);
		}
	}
	
	
	//List of customers at restaurant
	public List<Customer> waitingCustomers
	= Collections.synchronizedList(new ArrayList<Customer>());
	//List of waiters
	public List<Waiter> waiters
	= Collections.synchronizedList(new ArrayList<Waiter>());
	//All tables at restaurant
	public Collection<Table> tables
	= Collections.synchronizedList(new ArrayList<Table>(NTABLES));;
	
	
	//CONSTRUCTOR ******************************
	public Restaurant3HostRole(String name) {
		super();
		this.name = name;
		
		//Create tables
		for (int i = 1; i <= NTABLES; i++) {
			tables.add(new Table(i));
		}
	}
	
	//HELPER METHODS ****************************
	public String getName(){
		return name;
	}
	
	public void addWaiter(Waiter w){
		waiters.add(w);
		stateChanged();
	}
	
	//MESSAGES *********************************
	public void msgIWantFood(Customer c){
		waitingCustomers.add(c);
		print(name + ": customer " + c.getName() + " added");
		stateChanged();
	}
	
	public void msgCustLeavingTable(int table){
		synchronized(tables){
			for(Table t : tables) {
				if(t.tableNum == table) {
					t.isOccupied = false;
					print("Table number " + t.tableNum + "is free");
				}
			}
		}
		stateChanged();
	}

	//SCHEDULER ***************************************
	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(!waitingCustomers.isEmpty() && !waiters.isEmpty()){
			print(name + ": about to seat a customer");
			synchronized(tables){
				for(Table t : tables){
					if(!t.isOccupied){
						SeatCustomer(waitingCustomers.get(0), t, waiters.get(nextWaiter));
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//ACTIONS **************************************
	public void SeatCustomer(Customer c, Table table, Waiter w){
		print(name + ": requesting waiter " + w.getName() + " to seat customer " + c.getName());
		w.msgSeatCustomerAtTable(c, table.tableNum);
		table.isOccupied = true;
		waitingCustomers.remove(c);
		nextWaiter = (nextWaiter+1)%waiters.size();
		stateChanged();
	}
}
