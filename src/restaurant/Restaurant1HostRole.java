package restaurant;

import agent.Agent;

import restaurant.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import restaurant.interfaces.Customer;


/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the Restaurant1HostRole. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant1HostRole extends Agent {
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<mycustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<mycustomer>());
	public Collection<Table> tables;
	public List<mywaiter> waiter = Collections.synchronizedList(new ArrayList<mywaiter>());
	public Restaurant1WaiterRole wa = new Restaurant1WaiterRole("Jim");
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public int tablenum;
	private String name;;
	private int counter = 0;

	public Restaurant1HostRole(String name) {
		super();

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}
	enum state {working, applyingforwork, onbreak};

	public class mywaiter{
		Restaurant1WaiterRole waiter;
		state s = state.working;
		mywaiter(Restaurant1WaiterRole waiter){
			this.waiter = waiter;
		}
	}

	public class mycustomer{
		Restaurant1CustomerRole c;
		int location;
		Cstate s = Cstate.waiting;
		mycustomer(Restaurant1CustomerRole c, int location){
			this.c = c;
			this.location = location;
		}
	}

	enum Cstate {waiting, deciding, staying, leaving, eating};

	public mywaiter findwaiter(Restaurant1WaiterRole w){
		mywaiter a = null;
		for(mywaiter m: waiter){
			if(w == m.waiter){
				a = m;
				return a;
			}
		}
		return a;
	}

	public mycustomer findcustomer(Restaurant1CustomerRole c){
		mycustomer a = null;
		for(mycustomer mc: waitingCustomers){
			if(c == mc.c){
				a = mc;
				return a;
			}
		}
		return a;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}



	public void msgaddwaiter(Restaurant1WaiterRole w){
		waiter.add(new mywaiter(w));
	}

	public void msgIWantFood(Restaurant1CustomerRole cust, int loc) {
		waitingCustomers.add(new mycustomer(cust, loc));
		stateChanged();

	}

	public void msgLeavingTable(Waiter w, Customer c) {
		for (Table table : tables) {
			if (table.getOccupant() == c) {
				print(c + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgDecidestatus(boolean a, Restaurant1CustomerRole c){
		mycustomer mc = findcustomer(c);
		if(a){
			mc.s = Cstate.leaving;
			waitingCustomers.remove(mc);
		}
		else{
			mc.s = Cstate.staying;
		}
		stateChanged();
	}

	public void msgIsback(){
		stateChanged();
	}

	public void msgApplyingforbreak(Restaurant1WaiterRole w){
		mywaiter mw = findwaiter(w);
		mw.s = state.applyingforwork;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		try{
		boolean hasemptytable = false;

		synchronized(waiter){
			for(mywaiter waiters: waiter){
				if(waiters.s == state.applyingforwork){
					Dobreakwaiter(waiters);
					return true;
				}
			}
		}

		if (!waiter.isEmpty()){
			for (Table table : tables) {
				if (!table.isOccupied() ) {
					hasemptytable = true;
					if (!waitingCustomers.isEmpty()) {
						//System.out.println("Host"+waiters.waiter.getName());
						seatCustomer(waitingCustomers.get(0).c, table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					}
				}
			}
		}

		if(!hasemptytable){
			hasemptytable = true;
			synchronized(waitingCustomers){
				for(mycustomer customer: waitingCustomers){
					if(customer.s == Cstate.waiting){
						DoAskforstatus(customer);
						return true;
					}
				}
			}
		}


		}
		catch(ConcurrentModificationException e){
			return false;
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions




	private void seatCustomer(Restaurant1CustomerRole customer, Table table) {
		mycustomer mc = findcustomer(customer);
		mc.s = Cstate.eating;
		tablenum = table.tableNumber;
		boolean c = true;
		while(c){					
			if(waiter.get(counter).s == state.onbreak){
				if(counter< waiter.size() - 1){
					counter++;
				}
				else{
					counter = 0;
				}
			}
			else{
				c = false;
			}
		}
		int s = counter;
		if(counter< waiter.size() - 1){
			counter++;
		}
		else{
			counter = 0;
		}
		customer.setwaiter(waiter.get(s).waiter);
		waiter.get(s).waiter.msgIWantFood(customer, tablenum, mc.location);
		table.setOccupant(customer);
		Do("host seat customer");
		waitingCustomers.remove(mc);
	}

	private void DoAskforstatus(mycustomer customer){
		customer.s = Cstate.deciding;
		customer.c.msgAskforStatus();
	}

	private void Dobreakwaiter(mywaiter w){
		if(waiter.size() == 1){
			Do("No, way");
			w.s = state.working;
		}
		else {
			Do("You can break");
			w.s = state.onbreak;
		}
	}
	// The animation DoXYZ() routines
	//utilities


	private class Table {
		Restaurant1CustomerRole occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(Restaurant1CustomerRole cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		Restaurant1CustomerRole getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}



	}
}

