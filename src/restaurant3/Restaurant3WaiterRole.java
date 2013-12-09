package restaurant3;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.PersonAgent;
import agent.Agent;
import restaurant3.Restaurant3HostRole;
import restaurant3.interfaces.*;
import restaurant3.gui.Restaurant3AnimationPanel;
import restaurant3.gui.Restaurant3CustomerGui;
import restaurant3.gui.Restaurant3WaiterGui;

public class Restaurant3WaiterRole extends Restaurant3AbstractWaiter implements Restaurant3Waiter {
	//MEMBER DATA
	String name;
	private int nTables = Restaurant3HostRole.NTABLES; //REMOVE THIS IF NOT USED
	
	//References to agents
	Restaurant3HostRole host;
	Restaurant3Cook cook;
	Restaurant3Cashier cashier;
	
	//References to GUI stuff
	Restaurant3WaiterGui waiterGui;

	//List to hold customers
	List<MyCustomer> myCustomers
	= Collections.synchronizedList(new ArrayList<MyCustomer>());
	
	//Map for Menu
	Map<String, Double> menu = new HashMap<String, Double>();
	
	//Timer for table cleanup
	Timer timer = new Timer();
	
	//Semaphores for animations
	private Semaphore atTable = new Semaphore(0, true);
	private Semaphore atCook = new Semaphore(0, true);
	
	//CONSTRUCTOR ************************************
	public Restaurant3WaiterRole(String name, PersonAgent pa) {
		super(pa);
		this.name = name;
		roleName = "Restaurant 3 Waiter";
		
		//Initialize menu
		menu.put("Steak", Restaurant3CashierRole.steak);
		menu.put("Pizza", Restaurant3CashierRole.pizza);
		menu.put("Chicken", Restaurant3CashierRole.chicken);
		menu.put("Salad", Restaurant3CashierRole.salad);
		
	}

	//HELPER METHODS ************************
	public String getName(){
		return name;
	}
	
	public String getRoleName(){
		return roleName;
	}
	
	public void setHost(Restaurant3HostRole h){
		this.host = h;
	}
	
	public void setCook(Restaurant3CookRole ck){
		this.cook = ck;
	}
	
	public void setCashier(Restaurant3CashierRole c){
		this.cashier = c;
	}
	
	public void setGui(Restaurant3WaiterGui wg){
		this.waiterGui = wg;
	}

	@Override
	public void msgSeatCustomerAtTable(Restaurant3Customer c, int table) {
		print(name + ": received request to seat customer " + c.getName());
		myCustomers.add(new MyCustomer(c, table));
		stateChanged();
	}

	@Override
	public void msgReadyToOrder(Restaurant3Customer c) {
		print(name + ": customer " + c.getName() + " is ready to order");
		synchronized(myCustomers){
			for(MyCustomer cmr : myCustomers){
				if(cmr.cust == c){
					cmr.event = wEvent.custReadyToOrder;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgHereIsMyChoice(Restaurant3Customer c, String choice) {
		print(name + ": " + c.getName() + " is placing order");
		synchronized(myCustomers){
			for(MyCustomer cmr : myCustomers){
				if(cmr.cust == c){
					cmr.event = wEvent.custDecided;
					cmr.choice = choice;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgDoneEatingCheckPls(Restaurant3Customer c) {
		print(name + ": customer " + c.getName() + " requested check");
		synchronized (myCustomers) {
			for(MyCustomer cmr : myCustomers){
				if(cmr.cust == c){
					cmr.event = wEvent.custDoneEating;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgHereIsMyMoney(Restaurant3Customer c, double money) {
		print(name + ": received payment from customer " + c.getName());
		synchronized(myCustomers){
			for(MyCustomer cmr : myCustomers){
				if(cmr.cust == c){
					cmr.payment = money;
					cmr.event = wEvent.custPaid;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgLeavingTable(Restaurant3Customer c) {
		print(name + ": customer leaving - " + c.getName());
		synchronized(myCustomers){
			for(MyCustomer cmr : myCustomers){
				if(cmr.cust == c){
					cmr.event = wEvent.custLeaving;
					print("step completed");
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgOrderReady(int table, String choice) {
		synchronized(myCustomers){
			for(MyCustomer c : myCustomers){
				if(c.tableNum == table && c.choice.equals(choice)){
					print(name + " order is ready for customer " + c.cust.getName());
					c.event = wEvent.custOrderReady;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgHereIsABill(int table, double bill) {
		synchronized(myCustomers){
			for(MyCustomer c : myCustomers){
				if(c.tableNum == table){
					print(name + ": received bill for customer " + c.cust.getName());
					c.bill = bill;
					c.event = wEvent.custBillReady;
				}
			}
		}
		stateChanged();
	}

	@Override
	public void msgHereIsChangeReceipt(int table, double change) {
		synchronized(myCustomers){
			for(MyCustomer c : myCustomers){
				if(c.tableNum == table){
					print(name + ": received receipt for customer " + c.cust.getName());
					c.change = change;
					c.event = wEvent.custReceiptReady;
				}
			}
		}
		stateChanged();
	}
	
	public void msgAtTableRelease(){
		atTable.release();	//Maybe call stateChanged?
	}
	
	public void msgAtCookRelease(){
		atCook.release();
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		
		if(!myCustomers.isEmpty()){
			synchronized(myCustomers){
				//Check if any customer needs to be seated
				for(MyCustomer c : myCustomers){
					if(c.state == cState.idle && c.event == wEvent.custNeedSeat){
						seatCustomer(c);
						return true;
					}
				}
				
				//Check if cust is ready to order
				for(MyCustomer c : myCustomers){
					if(c.state == cState.beingSeated && c.event == wEvent.custReadyToOrder){
						takeOrder(c);
						return true;
					}
				}
				
				//Check if customer has decided
				for(MyCustomer c : myCustomers){
					if(c.state == cState.seated && c.event == wEvent.custDecided){
						placeOrder(c);
						return true;
					}
				}
				
				//Check if an order is ready
				for(MyCustomer c : myCustomers){
					if(c.state == cState.orderPlaced && c.event == wEvent.custOrderReady){
						pickUpOrder(c);
						return true;
					}
				}
				
				//Check if a customer is done eating
				for(MyCustomer c : myCustomers){
					if(c.state == cState.orderDelivered && c.event == wEvent.custDoneEating){
						getCheck(c);
						return true;
					}
				}
				
				//Check if a customer is awaiting a bill
				for(MyCustomer c : myCustomers){
					if(c.state == cState.awaitingBill && c.event == wEvent.custBillReady){
						requestPayment(c);
						return true;
					}
				}
				
				//Check if a customer has handed over money
				for(MyCustomer c : myCustomers){
					if(c.state == cState.billDelivered && c.event == wEvent.custPaid){
						giveMoneyToCashier(c);
						return true;
					}
				}
				
				//Check if a customer's receipt is ready
				for(MyCustomer c : myCustomers){
					if(c.state == cState.awaitingReceipt && c.event == wEvent.custReceiptReady){
						deliverReceiptAndChange(c);
						return true;
					}
				}
				
				//Check if a customer is leaving
				for(MyCustomer c : myCustomers){
					if(c.state == cState.receiptDelivered && c.event == wEvent.custLeaving){
						clearTable(c);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	//ACTIONS ***************************************
	public void seatCustomer(MyCustomer c){
		print(name + ": seating customer " + c.cust.getName() + " at table " + c.tableNum);
		c.state = cState.beingSeated;
		waiterGui.DoGoToHomePosition();	//GUI CODE
		atTable.drainPermits();
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		c.cust.msgFollowMeToTable(this, c.tableNum, menu);
		waiterGui.DoTakeCustomerToTable(c.tableNum);	//GUI CODE
		atTable.drainPermits();
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
	}
	
	public void takeOrder(MyCustomer c){
		print(name + ": taking order from table " + c.tableNum);
		c.state = cState.seated;
		waiterGui.DoGoToTable(c.tableNum);	//GUI CODE
		atTable.drainPermits();
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		c.cust.msgWhatWouldYouLike();
	}
	
	public void placeOrder(MyCustomer c){
		print(name + ": placing order for customer " + c.cust.getName());
		c.state = cState.orderPlaced;
		waiterGui.DoGoToCook();	//GUI CODE
		atCook.drainPermits();
		try{
			atCook.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		cook.msgNewOrder(this, c.tableNum, c.choice);
		waiterGui.DoGoToHomePosition();	//GUI CODE
	}
	
	public void pickUpOrder(MyCustomer c){
		print(name + ": picking up order for customer " + c.cust.getName());
		waiterGui.DoGoToCook();	//GUI CODE
		atCook.drainPermits(); //Force animation to complete
		try{
			atCook.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		waiterGui.carryOrder(c.choice);
		print(name + ": delivering order for customer " + c.cust.getName());
		waiterGui.DoTakeFoodToCustomer(c.tableNum);	//GUI CODE
		atTable.drainPermits();
		try{
			atTable.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
		waiterGui.deliverOrder();
		c.state = cState.orderDelivered;
		c.cust.msgHereIsYourFood(c.choice);
		waiterGui.DoGoToHomePosition();
	}
	
	public void getCheck(MyCustomer c){
		print(name + ": requesting check for customer " + c.cust.getName());
		c.state = cState.awaitingBill;
		cashier.msgPrepareBill(this, c.tableNum, c.choice);
	}
	
	public void requestPayment(MyCustomer c){
		print(name + ": requesting payment from customer " + c.cust.getName());
		c.state = cState.billDelivered;
		c.cust.msgHereIsYourBill(c.bill);
	}
	
	public void giveMoneyToCashier(MyCustomer c){
		print(name + ": sending payment from customer " + c.cust.getName() + " to cashier");
		c.state = cState.awaitingReceipt;
		cashier.msgHereIsMoney(this, c.tableNum, c.payment);
	}
	
	public void deliverReceiptAndChange(MyCustomer c){
		print(name + ": delivering receipt to customer " + c.cust.getName());
		c.state = cState.receiptDelivered;
		c.cust.msgHereIsChangeAndReceipt(c.change);
	}
	
	public void clearTable(MyCustomer c){
		print(name + ": clearing table " + c.tableNum);
		myCustomers.remove(c);
		final int t = c.tableNum;
		timer.schedule(new TimerTask(){
			public void run(){
				host.msgCustLeavingTable(t);
			}
		}, 1000);
	}
	
}
