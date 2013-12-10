package restaurant5;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import restaurant5.gui.Restaurant5WaiterGui;
import restaurant5.interfaces.Waiter5;
import agent.Role; 
import person.PersonAgent; 

public abstract class WaiterBase5 extends Role implements Waiter5{
	
	protected String name;
	public PersonAgent myPerson; 
	protected Restaurant5HostAgent myHost; 
	public Restaurant5Cashier myCashier; 
	protected Restaurant5CookAgent myCook; 	
	
	public Restaurant5WaiterGui waiterGui = null; 
	
	protected Semaphore atCustomer = new Semaphore(0,true);
	protected Semaphore atTable = new Semaphore(0,true);
	protected Semaphore withOrder = new Semaphore(0,true);
	protected Semaphore atOrigin = new Semaphore(0,true);
	protected Semaphore atCashier = new Semaphore(0,true);
	protected Semaphore waitingForOrder = new Semaphore(0);

	protected enum wState {wantBreak, askedBreak, onBreak, offBreak, ready};
	protected wState waiterState; 
	
	protected List<myCustomer> customers = new ArrayList<myCustomer>();
	public enum CustomerState {waiting,seated,readytoOrder,asked, ordered, cooking,outofFood, servefood,  reordered, cooked, computingCheck,senttoCashier,sendCheck, sentCheck,gotfood, served,leaving,paid, flaked, done;}
	public class myCustomer{
		Restaurant5CustomerAgent c;
		int tablenum;
		CustomerState s;
		String choice;
		int check;
		Menu5 _menu; 
		
		myCustomer(Restaurant5CustomerAgent _c, int table, CustomerState _s) {
			c = _c;
			tablenum = table;
			s = _s;
			}
	     }
	
	public WaiterBase5(String name, PersonAgent p) {
		super(p);
		myPerson = p; 
	}
	
	public void msgDoneEating(Restaurant5CustomerAgent c){
		for (myCustomer mc:customers){
			if (mc.c == c && mc.s == CustomerState.served){ ////////CHECK HERE CAREFUL!!!!!!!!
				mc.s = CustomerState.computingCheck;
			}
		}
		stateChanged();
	}
	
	public void msgHereisCheck(Check5 myCheck){
		for (myCustomer mc:customers){
			if (mc.c == myCheck.c  && mc.s == CustomerState.senttoCashier){
				mc.check = myCheck.price; 
				mc.s = CustomerState.sendCheck; 
			}
		}	
		stateChanged();
	}
	
	public abstract void msgatStand();

	public void msgseatCustomer(Restaurant5CustomerAgent cust, int table) {
		boolean added = false;
		for (myCustomer mc: customers){
			if (mc.c == cust){
				mc.s = CustomerState.waiting;
				mc.tablenum = table; 
				mc.choice = null;
				added = true;
			}
		}
		if (!added){
			customers.add(new myCustomer(cust,table,CustomerState.waiting));
		}
		stateChanged();
	}
	
	public void msgoffBreak(){
		print ("Waiter off Break");
		waiterState = wState.offBreak; 
		stateChanged();	
	}
	
	public void goOnBreak(){
		waiterState = wState.wantBreak;
		stateChanged();
	}
	
	public void msggoOnBreak(){
		waiterState = wState.onBreak;
		//resGui.Break(true,this);
	}
	
	public void msgcantgoOnBreak(){
		print ("Waiter can't go on break");
	//	resGui.Break(false,this);
		waiterState = wState.ready; 
	}
	
	public void msgOutof(String choice, int table){
			for (myCustomer mc:customers){
				if (mc.choice == choice && mc.tablenum == table && mc.s == CustomerState.cooking){
					mc.s = CustomerState.outofFood;
				}
			}
			stateChanged();
	}

	public void msgReadytoOrder(Restaurant5CustomerAgent cust) {
		for (myCustomer mc:customers){
			if (mc.c == cust){
				mc.s = CustomerState.readytoOrder;
			}
		}
		stateChanged();
	}
	
	
	public void msghereisChoice(Restaurant5CustomerAgent c, String choice){
		for (myCustomer mc:customers){
			if (mc.c == c){
				mc.choice = choice;
				waitingForOrder.release();
				mc.s = CustomerState.ordered;
			}
		}
		stateChanged();
	}
	

	
	public void msgorderDone(String choice, int table){		

		for (myCustomer mc:customers){
			if (mc.choice == choice && mc.tablenum == table && mc.s == CustomerState.cooking){
				mc.s = CustomerState.cooked;
			}
		}
		stateChanged();
	}

	public void msgDoneandLeaving(Restaurant5CustomerAgent c){
		waiterGui.IconOff(c);
		for (myCustomer mc:customers){
			if (mc.c == c){
				mc.s = CustomerState.leaving;
			}
		}
		stateChanged();
	}
	
	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		if (atTable.availablePermits() == 0){
			atTable.release();// = true;
		}
		//this should call a Semaphore but what does it do?
		//stateChanged();
	}
	
	public void msgatCustomer(){
		if (atCustomer.availablePermits()==0){
			atCustomer.release();
		}
	}
	
	public void msgatCashier(){
		if (atCashier.availablePermits() == 0) {  
			atCashier.release();
		}
	}
	
	public void msgatCook(){
		if (withOrder.availablePermits() == 0) {  
			withOrder.release();
		}
	}
	
	public void msgatOrigin(){
		//print("msgatorigin() called");
		if (atOrigin.availablePermits() == 0){
			atOrigin.release();
		}
	}
	
	
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		try{		
			for (myCustomer mc:customers){
				if (mc.s == CustomerState.gotfood){
					pickupfood(mc);
					return true;
				}
			}
			
			
			for (myCustomer mc:customers){
				if (mc.s == CustomerState.servefood){
					servefood(mc);
					return true;
				}
			}
			
			
		if (waiterState == wState.offBreak){
			goOffBreak();
			return true;
		}
		
		if (waiterState == wState.wantBreak){
			askforBreak();
			return true; 
		}
		
		
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.leaving){
				clearTable(mc);
				return true;
			}
		}
		
//		if (waiterGui.getXPos() == -20 && waiterGui.getYPos() == -20){
				for (myCustomer mc:customers){
					if (mc.s == CustomerState.waiting){
						sitCustomer(mc);
						return true; 
					}
				}
			//}
//setting computing check at the end of hereischoice message being sent hopefully that works
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.computingCheck){
				computeCheck(mc);
				return true;
			}
		}
			
		
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.sendCheck){
				sendCheck(mc);
				return true; 
			}
		}
				
				
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.readytoOrder){
				takeOrder(mc);
				return true;
			}
		}

		for (myCustomer mc:customers){
			if (mc.s == CustomerState.outofFood){
				asktoReorder(mc);
			}
		}
			
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.cooked){
				giveOrder(mc);
				return true;
			}
		}
	
		for (myCustomer mc:customers){
			if (mc.s == CustomerState.ordered){
				handleOrder(mc);
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
	
	
	
	
	
	
	
	
	
	
	
	
	//Action
	protected void goOffBreak(){
		waiterState = wState.ready; 
		myHost.msgOffBreak(this);
	}

	protected void computeCheck(myCustomer mc){
		//animation dogivetocashier(c);
		mc.s = CustomerState.senttoCashier; 
		atCashier.drainPermits();
		waiterGui.DoGoToCashier();
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print ("Waiter compute bill");
		myCashier.msgcomputeBill(this, mc.c,mc.choice);
	}
	
	protected void sendCheck(myCustomer mc) {
		mc.s = CustomerState.sentCheck; 
		atTable.drainPermits();
		waiterGui.DoBringToTable(mc.c, mc.tablenum); //animation
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Waiter send Check");
		(mc.c).msgSendCheck(mc.check);
	}
	
	protected void asktoReorder(myCustomer mc){
		
		mc._menu.remove(mc.choice);
		mc.s = CustomerState.reordered; 
		atTable.drainPermits();
			waiterGui.DoBringToTable(mc.c, mc.tablenum); //animation
			print ("Waiter Out of Choice");
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		mc.c.msgOutofChoice(mc._menu);
	}

	protected void clearTable(myCustomer mc){
		print("Waiter table free" + mc.tablenum);
		atOrigin.drainPermits(); 
		waiterGui.DoLeaveCustomer();
		try {
			atOrigin.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mc.s = CustomerState.done; 
		myHost.msgTableFree(this, mc.tablenum);
		
	}
	
	protected void pickupfood(myCustomer mc){
		mc.s = CustomerState.servefood;
		myCook.msgpickedupfood(this, mc.choice, mc.tablenum);
		stateChanged();
	}
	
	protected void servefood(myCustomer c){
		c.s = CustomerState.served; 
		waiterGui.IconOn(c.c,c.choice);
		waiterGui.DoBringToTable(c.c, c.tablenum); //animation
		
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Waiter Here's Your Order");
		c.c.msgHeresYourOrder(this,c.choice);
		waiterGui.DoLeaveCustomer();
		
	}
	
	
	protected void askforBreak(){
		print ("Waiter can I go on Break?");
		waiterState = wState.askedBreak; 
		myHost.msgCanIGoOnBreak(this);
	}
	
	protected void sitCustomer(myCustomer c){
	//	atOrigin.drainPermits();
		atCustomer.drainPermits();
		Menu5 mm = new Menu5();
		c._menu = mm;

			waiterGui.DoGotoCustomer(c.c);
			try {
				atCustomer.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//}
		waiterGui.DoBringToTable(c.c, c.tablenum); //animation
		c.s = CustomerState.seated;
		print("Waiter follow me");
		c.c.msgfollowMe(this, c._menu); 
		atTable.drainPermits();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//stateChanged();
	}
	
	protected void giveOrder(myCustomer c){
		atTable.drainPermits();
		withOrder.drainPermits();
		waiterGui.DoGoToCook();
		try{
			withOrder.acquire();
		}catch (InterruptedException e){
			e.printStackTrace();
		}
		print("giveorder here");
		c.s = CustomerState.gotfood; ///////USED TO BE SERVED
		stateChanged();
	}
	
	protected void takeOrder(myCustomer c){
		print("Waiter what do you want?");
		c.s = CustomerState.asked;
	    waiterGui.DoBringToTable(c.c, c.tablenum); 
	    atTable.drainPermits();
		try {
				atTable.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
		c.c.msgwhatdoyouWant(this);
		//stateChanged();
	}

	protected abstract void handleOrder(myCustomer c);
	
	
	//utilities

	public void setGui(Restaurant5WaiterGui gui) {
		waiterGui = gui;
	}

	public Restaurant5WaiterGui getGui() {
		return waiterGui;
	}
	
	public String onBreak(){
		
		if (waiterState == wState.ready || waiterState == wState.offBreak){
			return ("OffBreak");
		}
		else if (waiterState == wState.askedBreak || waiterState == wState.wantBreak){
			return ("AskedBreak");
		}
		else {
			return ("OnBreak");
		}
	}

	public String getName(){
		return name;
	}
	
	public int getPlace(Restaurant5CustomerAgent c){
		for (myCustomer mc:customers){
			if (mc.c == c){
				return mc.tablenum;
			}
		}
		return 0;	
	}

	
	public void setCashier(Restaurant5Cashier c){
		myCashier = c; 
	}
	
	
	public void setCook(Restaurant5CookAgent cook){
		myCook = cook;
	}
	
	public String toString(){
		return name; 
	}

	public abstract String getRoleName();
	
	public void setHost(Restaurant5HostAgent host){
		myHost = host; 
	}
	
	public Restaurant5HostAgent getHost(){
		return myHost; 
	}


	
}
