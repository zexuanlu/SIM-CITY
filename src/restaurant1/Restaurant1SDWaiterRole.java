package restaurant1;

import restaurant1.Restaurant1AbstractWaiter.state;
import restaurant1.gui.WaiterGui;
import restaurant1.interfaces.Restaurant1Cashier;
import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Waiter;
import restaurant1.shareddata.*;
import person.interfaces.Person;

import java.util.*;
import java.util.concurrent.Semaphore;

public class Restaurant1SDWaiterRole extends Restaurant1AbstractWaiter implements Restaurant1Waiter {

	//note that tables is typed with Collection semantics.
		//Later we will see how it is implemented
		public int tablenum;
		private String name;
		private Semaphore atTable = new Semaphore(0,true);
		private Semaphore atCook = new Semaphore(0, true);
		boolean isBack = true;
		public WaiterGui waiterGui = null;
		private Restaurant1HostRole host = null;
		private Restaurant1CookRole cook= null;
		private Restaurant1Cashier cashier = null;
		private Restaurant1RevolvingStand revStand = null;
		List<mycustomer> customer = new ArrayList<mycustomer>();
		
		public Map<String, Double> menue = new HashMap<String, Double>();
		
		String names;
			
		enum waiterstate {none, working, applyingbreak};
		waiterstate ws = waiterstate.working;
		
		
		public Restaurant1SDWaiterRole(String name, Person pa){
			super(pa);
			this.name = name;
			roleName = "Rest1 SharedData Waiter";
			menue.put("Steak", 15.99);
			menue.put("Chicken", 10.99);
			menue.put("Salad", 5.99);
			menue.put("Pizza", 8.99);
		}
		
		public mycustomer findagent(Restaurant1Customer mc){
			mycustomer a = null;
			for(mycustomer m: customer){
				if(mc == m.c){
					a = m;
					return a;
				}
			}
			return a;
		}

		
		public mycustomer findtable(int table){
			mycustomer a = null;
			for(mycustomer m: customer){
				if(table == m.table){
					a = m;
					return a;
				}
			}
			return a;
		}

		public void sethost(Restaurant1HostRole host){
			this.host = host;
		}
		
		public void setcook(Restaurant1CookRole cook){
			this.cook = cook;
		}
		
		public void setCashier(Restaurant1Cashier cashier){
			this.cashier = cashier;
		}
		
		public void setNumber(int number){
			waiterGui.setOrigion(number);
		}
		
		public void setRevolvingStand(Restaurant1RevolvingStand rev){
			this.revStand = rev;
		}
		
		public String getMaitreDName() {
			return name;
		}

		public String getName() {
			return name;
		}

		public List getWaitingCustomers() {
			return customer;
		}

		
		// Messages

		public void msgIWantFood(Restaurant1Customer cust, int table, int loc) {
			customer.add(new mycustomer(cust, table, loc));
			stateChanged();

		}

		public void msgreadytoorder(Restaurant1Customer customer){
			mycustomer mc =findagent(customer);
			mc.s = state.readytoorder;
			stateChanged();
		}
		
		public void msgAnimationDoneAtTable(Restaurant1Customer customer){
			mycustomer mc =findagent(customer);
			mc.s = state.attable;
			stateChanged();
		}
		
		public void msgorderisready(Restaurant1Customer customer, String choice, int table){
			mycustomer mc =findagent(customer);
			mc.s = state.ordered;
			mc.table = table;
			mc.choice = choice;
			stateChanged();
		}
		
		public void msgorderiscooked(int table){
			mycustomer mc = findtable(table);
			mc.s = state.orderready;
			mc.table = table;
			stateChanged();
		}
		
		public void msgatCook(){
			atCook.release();
			stateChanged();
		}
		
		public void msgordertotable(Restaurant1Customer customer){
			mycustomer mc = findagent(customer);
			mc.s = state.bringattable;
			stateChanged();
		}
		
		
		public void msgLeavingTable(Restaurant1Customer c) {
			mycustomer mc = findagent(c);
			mc.s = state.done;
			stateChanged();
		}
		
		

		public void msgAtTable() {//from animation
			//print("msgAtTable() called");
			atTable.release();// = true;
			//stateChanged();
		}
		
		
		public void msgIsback(){
			isBack = true;
		}

		public void msgOutOfFood(int table){
			mycustomer mc = findtable(table);
			mc.s = state.outoffood;
			stateChanged();
		}
		
		public void IwantBreak(){
			ws = waiterstate.applyingbreak;
			stateChanged();
		}
		
		public void msgHereistheCheck(Restaurant1Customer c, double p){
			mycustomer mc = findagent(c);
			mc.s = state.checkingbill;
			stateChanged();
		}
		
		/**
		 * Scheduler.  Determine what action is called for, and do it.
		 */
		public boolean pickAndExecuteAnAction() {
			/* Think of this next rule as:
	            Does there exist a table and customer,
	            so that table is unoccupied and customer is waiting.
	            If so seat him at the table.
			 */	
			try{
			for(mycustomer customers: customer){
				if(customers.s == state.waiting){
				if (isBack == true) {
						isBack = false;
						seatCustomer(customers, customers.table);//the action
						return true;//return true to the abstract agent to reinvoke the scheduler.
					
				}
			
				}
			}
			
			for(mycustomer customers: customer){
				if(customers.s == state.checkingbill){
					DoGiveBill(customers);
					return true;
				}
			}
			
			for(mycustomer customers: customer){
				if(customers.s == state.readytoorder){
					Dotakeorder(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.attable){
					Doaskfororder(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.ordered){
					Dotakeordertocook(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.orderready){
					Dogotocook(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.outoffood){
					Doredorder(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.bringattable){
					Docalltoeat(customers);
					return true;
				}
			}
			for(mycustomer customers: customer){
				if(customers.s == state.done){
					Dofreetable(customers);
					return true;
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
		
		

		private void seatCustomer(mycustomer customer, int table) {
			customer.s = state.seated;
			tablenum = table;
			Seating(customer.c);
			//waiterGui.Dotakecustomer(customer.c);
			
		}
		
		public void Seating(Restaurant1Customer c){
			mycustomer mc = findagent(c);
			waiterGui.DoGotoCHomePosition(mc.location);
			atTable.drainPermits();
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			DoSeatCustomer(mc, tablenum);
			mc.c.msgSitAtTable(tablenum, menue);
			atTable.drainPermits();
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			waiterGui.DoLeaveCustomer();
		}
		
		// The animation DoXYZ() routines
		private void DoSeatCustomer(mycustomer customer, int table) {
			//Notice how we print "customer" directly. It's toString method will do it.
			//Same with "table"
			print("Seating " + customer.c + " at " + table);
			waiterGui.DoBringToTable(tablenum); 
			//waiterGui.DoLeaveCustomer();
		}
		
		public void Dotakeorder(mycustomer customer){
			//Dogototable(customer.table);
			customer.s = state.askedtoorder;
			Do("Coming!");
			waiterGui.DoGoToTakeOrder(customer.c, customer.table);
			atTable.drainPermits();
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msgAnimationDoneAtTable(customer.c);

		}
		
		public void Doaskfororder(mycustomer customer) {
			customer.s = state.askedtoorder;
			Do("What do you like?");
			customer.c.msgwhatyouwant();
		}
		
		public void Dotakeordertocook(mycustomer customer){
			customer.s = state.gotocook;
			waiterGui.Dogotocook();
			atTable.drainPermits();
			try {
				atCook.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			revStand.insertOrder(new Order(this, customer.choice, customer.table));	
			cook.msgAddedOrderToRevolvingStand();
			System.err.println("Added order to revolving stand");
			//waiterGui.DoGotoCHomePosition(tablenum);
			waiterGui.DoLeaveCustomer();
		}

		public void Dogotocook(mycustomer customer){
			waiterGui.Dogotocook();
			
			atCook.drainPermits();
			
			try {
				atCook.acquire();
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			waiterGui.hidefood();
			waiterGui.animationBringFood(customer.choice);
			customer.s = state.available;
			
			waiterGui.DoBackToTable(customer.c ,customer.table);
			atTable.drainPermits();
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			msgordertotable(customer.c);
	        waiterGui.DoLeaveCustomer();
		}
		
		public void Docalltoeat(mycustomer customer){
			waiterGui.bringFoodDone();
			customer.s = state.starteating;
			customer.c.msgordercooked(cashier);
			Do("Please compute the check");
			cashier.msgCheckthePrice(this, customer.c, customer.choice);
		}
		
		public void Dofreetable(mycustomer cus){
			waiterGui.DoLeaveCustomer();
			host.msgLeavingTable(this ,cus.c);
			customer.remove(cus);
			//host ageent free table
		}
		//utilities

		public void Doredorder(mycustomer customer){
			customer.s = state.askedtoorder;
			customer.c.msgReorder(customer.choice);
		}
		
		public void DoGiveBill(mycustomer customer){
			Do("Here is your check");
			customer.s = state.bringbill;
			
		}
		
		
		public void setGui(WaiterGui gui) {
			waiterGui = gui;
		}

		public WaiterGui getGui() {
			return waiterGui;
		}


		public void add(Restaurant1WaiterRole waiter) {
			// TODO Auto-generated method stub
			
		}		

		public String getRoleName(){
			return roleName;
		}
}
