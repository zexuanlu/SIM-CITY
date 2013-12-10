package restaurant4;

import agent.Role;
import restaurant4.interfaces.*;
import restaurant4.test.mock.EventLog;

import java.util.*;

import market.interfaces.MarketCashier;
import person.interfaces.Person;

/**
 * Restaurant Cashier Agent
 * 
 * Receives check requests from a waiter, and payment requests from customers and fills them
 */
public class Restaurant4CashierRole extends Role implements Restaurant4Cashier{
	//A list of the various Checks, either requested or being paid
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	private String name;
	private int endOfDay = 0;
	//Lets the Cashier check the prices of items
	private Map<String, Double> foodPrices = Collections.synchronizedMap(new HashMap<String, Double>());
	public EventLog log;

	public Restaurant4CashierRole(String name, Person pa) {
		super(pa);
		this.name = name;
		log = new EventLog();
		foodPrices.put("Shrimp", 8.99);
		foodPrices.put("Scallops", 7.99);
		foodPrices.put("Lobster", 14.99);
		foodPrices.put("Crab", 13.99);
	}

	public String getName() {
		return name;
	}

	// Messages

	/**
	 * Receives this message from a waiter when a customer is ready for a check
	 * 
	 * @param choice the food the customer ordered
	 * @param customer the customer who ordered the food
	 * @param waiter the waiter who submitted the check
	 */
	public void msgINeedCheck(String choice, Restaurant4Customer customer, Restaurant4Waiter waiter){
		synchronized(checks){
			for(Check c : checks){
				if(c.cust == customer){
					c.s = state.requested;
					stateChanged();
					return;
				}
			}
		}
		checks.add(new Check(waiter, customer, choice));
		stateChanged();
	}

	/**
	 * Receives this message from a customer when the customer is paying
	 * 
	 * @param customer the customer who is paying
	 * @param money the amount of money the customer is using
	 */
	public void msgPayingForFood(Restaurant4Customer customer, double money){
		synchronized(checks){
			for(Check c : checks){
				if(c.cust == customer){
					c.money = money;
					c.s = state.paid;
					stateChanged();
					return;
				}
			}
		}
	}
	
	public void msgPleasepaytheBill(MarketCashier mc, double price){
		bills.add(new Bill(mc, price));
		stateChanged();
	}
	
	public void msgBillChecked(double price){
		for(Bill b : bills){
			if(b.amount == price){
				b.bs = billState.checked;
				stateChanged();
				return;
			}
		}
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//If there is a check that has been paid, confirm the payment
		for(Check c : checks){
			if(c.s == state.paid){
				confirmPayment(c);
				return true;
			}
		}
		//If there is a check that has been requested, send it
		for(Check c : checks){
			if(c.s == state.requested){
				prepAndSendCheck(c);
				return true;
			}
		}
		for(Bill bill : bills){
			if(bill.bs == billState.checked){
				payBill(bill);
				return true;
			}
		}
		for(Bill bill : bills){
			if(bill.bs == billState.received){
				checkBill(bill);
				return true;
			}
		}
		if(endOfDay == 2){
			workDayOver();
			return true;
		}
		return false;
	}

	// Actions
	/**
	 * Prepares and sends the check to the waiter who requested it
	 * 
	 * @param c The check to be prepared
	 */
	private void prepAndSendCheck(Check c){
		c.price += foodPrices.get(c.type);
		c.waiter.msgHereIsCheck(c.cust, c.price);
		c.s = state.owed;
	}
	
	/**
	 * Fills a check and gives the customer back his change
	 * 
	 * @param c the check to be filled
	 */
	private void confirmPayment(Check c){
		c.money -= c.price;
		c.price = 0;
		c.cust.msgHereIsChange(c.money);
		c.money = 0;
		c.s = state.done;
	}
	
	private void checkBill(Bill b){
		b.bs = billState.checked;
	}

	private void payBill(Bill b){
		b.mc.msgBillFromTheAir(b.amount);
	}
	
	private void workDayOver(){
		endOfDay = 0;
		getPerson().msgGoOffWork(this, 0.00);
	}
	//utilities
	
	public class Bill {
		public MarketCashier mc;
		public double amount;
		billState bs;
		Bill(MarketCashier m, double bill){
			amount = bill;
			mc = m;
			bs = billState.received;
		}
	}
	
	enum billState {received, checked, none}
	
	/**
	 * The check class
	 * 
	 * Contains a waiter, a food-type, a price, a customer, an amount spent, and a state
	 */
	public class Check {
		public Restaurant4Waiter waiter;
		public String type;
		//The price of the food
		public double price;
		public Restaurant4Customer cust;
		public state s;
		//The money that the customer spent (before change is given)
		public double money;
		Check(Restaurant4Waiter w, Restaurant4Customer c, String t){
			waiter = w;
			type = t;
			cust = c;
			price = 0;
			s = state.requested;
		}
	}
	public enum state {requested, owed, paid, done}
	@Override
	public String getRoleName() {
		return "Restaurant 4 Cashier";
	}

	public void msgWorkDayOver() {
		endOfDay++;
		stateChanged();
	}
}
