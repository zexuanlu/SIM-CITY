package restaurant2;

import agent.Agent;
import agent.Role;
import restaurant2.gui.HostGui;
import restaurant2.interfaces.Customer;
import restaurant2.interfaces.Waiter;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant2HostRole extends Role {
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<Table> tables;
	private MyWaiter prevWaiter;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	private enum HostState {backHome, away};//use this to send a message to the customer notifying him to follow 
	private HostState state = HostState.backHome;
	private String name;
	private Semaphore atTable = new Semaphore(0,true);

	public HostGui hostGui = new HostGui(this);

	public Restaurant2HostRole(String name, Person p) {
		super(p);

		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection getTables() {
		return tables;
	}
	public void addWaiter(Waiter waiter){
		MyWaiter w = new MyWaiter(waiter);
		waiters.add(w);
	}
	// Messages
	public void msgAvailable(Waiter w)
	{
		for(int i=0; i < waiters.size(); i++)
		{
			MyWaiter mw = waiters.get(i);
			if(mw.getWaiter() == w)
			{
				mw.available = true;
			}
		}
		stateChanged();
	}
	public void msgBackHome()
	{
		if(state!=HostState.backHome)
		{
			state = HostState.backHome;
			System.out.println("home");
		}
	}
	public void msgIWantFood(Restaurant2CustomerRole cust) {
		MyCustomer mc = new MyCustomer(cust);
		waitingCustomers.add(mc);
		stateChanged();
	}
	public void msgTableFree(int tableNumber)
	{
		for(Table table: tables)
		{
			if(table.tableNumber == tableNumber)
			{
				table.setUnoccupied();
			}
		}
	}
	public void msgLeavingTable(Restaurant2CustomerRole cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) {
				print(cust + " leaving " + table);
				table.setUnoccupied();
				waitingCustomers.remove(cust);
				stateChanged();
			}
		}
	}
	public void msgWantToBreak(Waiter waiter)
	{
		if(waiters.size() > 1)
		{

			for(MyWaiter w: waiters)
			{
				if(w.getWaiter() == waiter)
				{
					print("sending "+waiter+" on break");
					w.sendOnBreak = true;
					//w.onBreak = true;
				}
			}
		}
		else
			print("only one waiter you may not break!");
		stateChanged();
	}
	public void msgBreakIsOver(Waiter w)
	{
		synchronized(waiters){
			for(MyWaiter mw : waiters)
			{
				if(mw.getWaiter() == w)
				{
					mw.onBreak = false;
				}
			}
		}
		stateChanged();
	}
	public void msgAtTable() {//from animation
		atTable.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(waiters){
			for(MyWaiter w : waiters)
			{
				if(w.sentOnBreak())
				{
					sendOnBreak(w);
					return true;
				}
			}
		}
		synchronized(waitingCustomers){
			while(!waitingCustomers.isEmpty())
			{
				for (Table table : tables) 
				{
					if (!table.isOccupied()) 
					{
						synchronized(waiters){
							for(MyWaiter waiter: waiters)
							{

								if(waiter.isActive() && waiter.isAvailable() && !waiter.isOnBreak()){
									seatCustomer(waitingCustomers.get(0), table, waiter);//the action
									prevWaiter = waiter;
									return true;//return true to the abstract agent to reinvoke the scheduler.
								}
							}
						}
					}	
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void notifyOfWaitTime()
	{
		for(MyCustomer mc : waitingCustomers)
		{
			mc.getCustomer().msgYouHaveToWait();
		}
	}
	private void seatCustomer(MyCustomer customer, Table table, MyWaiter waiter) {
		waiter.available = false;
		table.setOccupant(customer.getCustomer());
		print("waiter "+waiter.getWaiter()+" taking "+customer+" to "+table);
		waiter.getWaiter().msgSeatCustomer((Customer) customer.getCustomer(), table);//send a message to the selected waiter with the table and customer to seat at the table
		waitingCustomers.remove(customer);
	}
	private void sendOnBreak(MyWaiter w)
	{
		print("Sending "+w.getWaiter()+" on break");
		w.sendOnBreak = false;
		w.onBreak = true;
		print("someone is on break "+w.onBreak);
		w.available = false;
		w.getWaiter().msgGoOnBreak();
		//stateChanged();
	}
	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	private class MyCustomer{
		private Restaurant2CustomerRole customer;
		MyCustomer(Restaurant2CustomerRole customer)
		{
			this.customer = customer;
		}
		public Restaurant2CustomerRole getCustomer()
		{
			return customer;
		}
	}
	private class MyWaiter {
		private Waiter waiter;
		private String name;
		public boolean active = false;
		public boolean available = false;
		public boolean sendOnBreak = false;
		public boolean onBreak;

		MyWaiter(Waiter waiter)
		{
			this.waiter = waiter;
			//this.name = name;
			active = true;
			available = true;
			onBreak = false;
			sendOnBreak = false;
		}
		public Waiter getWaiter()
		{
			return waiter;
		}
		public boolean isActive(){
			return active;
		}
		public boolean isOnBreak(){
			return onBreak;
		}
		public boolean sentOnBreak()
		{
			return sendOnBreak;
		}
		public boolean isAvailable()
		{
			return available;
		}
	}
	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}
}

