package restaurant2;

import restaurant2.gui.Restaurant2CustomerGui;
import restaurant2.interfaces.Restaurant2Cashier;
import restaurant2.interfaces.Restaurant2Customer;
import restaurant2.interfaces.Restaurant2Market;
import restaurant2.interfaces.Restaurant2Waiter;
import restaurant2.Restaurant2CookRole.MarketState;
import utilities.restaurant.RestaurantCashier;
import agent.Agent;
import agent.Role;
import bank.BankDatabaseAgent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.interfaces.MarketCashier;
import person.interfaces.Person;

/**
 * Restaurant customer agent.
 */
public class Restaurant2CashierRole extends Role implements Restaurant2Cashier{
	public boolean offWork; 
	public int offWorkMess; 
	
	// agent correspondents
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());	
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	private Map<String, Integer> prices = new HashMap<String, Integer>();

	public enum MarketState {Unpaid, Paid, Idle};
	public enum BillState {Pending, Ready, Paid, Complete, Waiting, IOU}; 
	public double wallet;
	public int accountNumber;
	public BankDatabaseAgent bank;
	Semaphore getMoney = new Semaphore(0,true);

	/**
	 * Constructor for Customer class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public Restaurant2CashierRole(String name, Person p){
		super(p);
		prices.put("Steak", 20);
		prices.put("Hamburger", 8);
		prices.put("Ribs", 12);
		prices.put("Chicken", 10);
		prices.put("Salad", 5);
		prices.put("Pound Cake", 2);

		wallet = 800;
	}
	
	public void msgGoOffWork(){
		offWorkMess++; 
	//	if (offWorkMess == 2){
			offWork = true; 
			stateChanged(); 
	//	}
	}

	// Messages
	public void msgComputeCheck(String orderString, Restaurant2Waiter waiter, Restaurant2Customer customer){
		Check check = new Check(orderString);
		print("This check equates to "+check.getCheck());
		MyWaiter mw = new MyWaiter(waiter, check, customer);
		MyCustomer mc = new MyCustomer(customer, check);
		waiters.add(mw);
		customers.add(mc);
		Bill bill = new Bill(customer, waiter, check);
		bills.add(bill);
		stateChanged();
	}
	public void msgCheckFromMarket(Restaurant2Market m, Check check){
		MyMarket mm = new MyMarket(m, check);
		markets.add(mm);
		Bill bill = new Bill(m, check);
		bills.add(bill);

		stateChanged();
	}
	public void msgPayment(Restaurant2Customer c, int check){
		for(MyCustomer mc : customers){
			if(mc.getCustomer() == c){
				print(c+" has paid for his/her meal");
				mc.setPayed(true);
				wallet += check;
			}
		}	
		for(Bill b : bills){
			if(b.getCustomer() == c){
				b.setState(BillState.Paid);
			}
		}
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {

		if(wallet < 500.00){
			getMoneyFromBank();
			return true;
		}
		synchronized(bills){
			for(Bill b : bills){
				if(b.state == BillState.Ready){
					payMarket(b);
					return true;
				}
				else if(b.state == BillState.Pending){
					passCheckToWaiter(b);
					return true;
				}
				else if(b.state == BillState.Paid){
					thankCustomer(b);
					return true;
				}
				else if(b.state == BillState.IOU){
					Repay(b);
					return true;
				}
			}
			
			if (offWork){
				goOffWork();
				return true; 
			}
		}
		return false;
	}

	// Actions
	
	
	private void goOffWork(){
		offWork = false; 
		offWorkMess = 0; 
		this.person.msgGoOffWork(this,0);
	}

	private void thankCustomer(Bill b){
		if(b.customer.getName() != "flake lord"){
			b.getCustomer().msgThanksForDining();
			b.setState(BillState.Complete);
		}
		else{
			b.getCustomer().msgRepay();
			b.setState(BillState.Complete);
		}
	}
	private void payMarket(Bill b){
		if(wallet >= b.getCheck().getCheck()){
			if(b.checkBill()){
				b.getMarket().msgHeresPayment(this, b.getCheck().getCheck());
				b.setState(BillState.Complete);
				wallet -= b.getCheck().getCheck();
			}
		}
		else{
			double remaining = b.getCheck().getCheck() - wallet;
			b.getMarket().msgHeresPayment(this, (int)-remaining);
			b.setremaining(remaining);
			print("Remaining: "+b.getCheck().getCheck());
			//wallet -= b.getCheck().getCheck();
			b.setState(BillState.IOU);
		}

	}
	private void passCheckToWaiter(Bill b){
		b.getWaiter().msgHeresTheCheck(b.getCustomer(), b.getCheck());
		b.setState(BillState.Waiting);
	}
	private void Repay(Bill b){
		double repay = b.remaining;
		if(wallet >= 0){
			if(wallet < repay){
				b.getMarket().msgHeresLatePayment(this, (int)wallet);
				b.remaining -= wallet;
				wallet = 0;
			}
			else if(wallet > repay){
				b.getMarket().msgHeresLatePayment(this, (int)repay);
				wallet -= b.remaining;
				b.remaining = 0;
			}
			else if(wallet == repay){
				b.getMarket().msgHeresLatePayment(this, (int)wallet);
				wallet = 0;
				b.remaining = 0;
			}

			print("r: "+b.remaining+ "and wallet: "+wallet);
		}
		if(b.remaining == 0){
			b.setState(BillState.Complete);
		}
	}

	private void passCheckToWaiter(MyWaiter w){
		print("The check for "+w.getCustomer()+" is for the amount "+w.getCheck());
		w.getWaiter().msgHeresTheCheck(w.getCustomer(), w.getCheck());
		w.setHasCheck(true);
	}
	private void thankCustomer(MyCustomer c){
		//print("Thanks for dining with us today "+c.getCustomer().getName()+"!");
		c.getCustomer().msgThanksForDining();
		synchronized(waiters){
			for(MyWaiter w : waiters)
			{
				if(w.getCustomer() == c)
				{
					waiters.remove(w);
				}
			}
		}
		customers.remove(c);
	}
	private void payMarket(MyMarket mm){
		int payment = mm.check.getCheck();
		mm.getMarket().msgHeresPayment(this, payment);
		wallet -= payment;
		markets.remove(mm);
	}
	public class Bill{
		public Restaurant2Customer customer;
		public Restaurant2Waiter waiter;
		public Restaurant2Market market;
		public Check check;
		public BillState state;
		public double remaining;
		Bill(Restaurant2Customer c, Restaurant2Waiter w, Check chk){
			customer = c;
			waiter = w;
			check = chk;

			state = BillState.Pending;
		}
		Bill(Restaurant2Market m, Check chk){
			market = m;
			check = chk;

			state = BillState.Ready;
		}
		void setState(BillState bs){
			state = bs;
		}
		Restaurant2Waiter getWaiter(){
			return waiter;
		}
		public boolean checkBill(){
			//ggg
			return true;
		}
		public Restaurant2Customer getCustomer(){
			return customer;
		}
		public Restaurant2Market getMarket(){
			return market;
		}
		public Check getCheck(){
			return check;
		}
		public void setremaining(double remaining2){
			remaining = remaining2;
		}
	}
	public class MyMarket {

		private Restaurant2Market market;
		public MarketState state;
		public Check check;

		MyMarket(Restaurant2Market m, Check c){
			market = m;
			state = MarketState.Unpaid;
			check = c;
		}
		public Restaurant2Market getMarket(){
			return market;
		}

	}
	public class MyWaiter {

		private Restaurant2Waiter waiter;
		private Restaurant2Customer customer;
		private Check check;
		private boolean hasCheck;

		MyWaiter(Restaurant2Waiter waiter, Check check, Restaurant2Customer customer)
		{
			System.out.println(check.getCheck());
			this.waiter = waiter;
			this.check = check;
			this.customer = customer;
			hasCheck = false;
		}
		public Restaurant2Waiter getWaiter(){
			return waiter;
		}
		public Restaurant2Customer getCustomer(){
			return customer;
		}
		public Check getCheck(){
			return check;
		}
		public boolean hasCheck(){
			return this.hasCheck;
		}
		public void setHasCheck(boolean tf)
		{
			this.hasCheck = tf;
		}
	}
	public class MyCustomer {

		private Restaurant2Customer customer;
		private Check checkToPay;
		private boolean hasPayed;

		MyCustomer (Restaurant2Customer customer, Check ctp){
			this.customer = customer;
			this.checkToPay = ctp;
			hasPayed = false;
		}
		public Restaurant2Customer getCustomer(){
			return customer;
		}
		public Check getCheck(){
			return checkToPay;
		}
		public boolean hasPayed(){
			return hasPayed;
		}
		public void setPayed(boolean tf){
			hasPayed = tf;
		}
	}
	
	//FIX
	@Override
	public void msgPayment(Restaurant2Customer c, Check check) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgPleasepaytheBill(MarketCashier c, double bills) {
		// TODO Auto-generated method stub
		
	}
	
	public utilities.Gui getGui(){
		return null; 
	}
	private void getMoneyFromBank(){
		bank.msgWithdrawMoney(this, (1000.00-wallet), accountNumber);
		try{
			getMoney.acquire();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}
	
	public void msgAddMoney(double amount) {
		wallet += amount;
		getMoney.release();
	}
}

