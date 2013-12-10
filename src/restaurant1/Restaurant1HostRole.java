package restaurant1;

import agent.Role;
import person.interfaces.Person;

import java.util.*;

import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Host;
import utilities.restaurant.*;
import restaurant1.interfaces.Restaurant1Waiter;


/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the Restaurant1HostRole. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant1HostRole extends Role implements Restaurant1Host{
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<mycustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<mycustomer>());
	public Collection<Table> tables;
	public List<mywaiter> waiter = Collections.synchronizedList(new ArrayList<mywaiter>());
	public Restaurant1Waiter wa;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	public int tablenum;
	private String name;;
	private int counter = 0;

	public Restaurant1HostRole(String name, Person pa) {
		super(pa);
		roleName = "Rest1 Host";
		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}
	enum state {working, applyingforwork, onbreak};

	public class mywaiter{
		Restaurant1Waiter waiter;
		state s = state.working;
		mywaiter(Restaurant1Waiter waiter){
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

	public mywaiter findwaiter(Restaurant1Waiter w){
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



	public void msgaddwaiter(Restaurant1Waiter w){
		waiter.add(new mywaiter(w));
	}

	public void msgIWantFood(Restaurant1CustomerRole cust, int loc) {
		waitingCustomers.add(new mycustomer(cust, loc));
		stateChanged();

	}

	public void msgLeavingTable(Restaurant1Waiter w, Restaurant1Customer c) {
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

	public void msgApplyingforbreak(Restaurant1Waiter w){
		mywaiter mw = findwaiter(w);
		mw.s = state.applyingforwork;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
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
<<<<<<< HEAD
		customer.setwaiter(waiter.get(1).waiter);
		print("Assigning waiter " + waiter.get(1).waiter.getName());
		waiter.get(1).waiter.msgIWantFood(customer, tablenum, mc.location);
=======
		customer.setwaiter(waiter.get(s+1).waiter);
		print("Assigning waiter " + waiter.get(s).waiter.getName());
		waiter.get(s+1).waiter.msgIWantFood(customer, tablenum, mc.location);
>>>>>>> 897170101698cf976aef589347ede34d6d892508
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
	
	public String getRoleName(){
		return roleName;
	}
}

