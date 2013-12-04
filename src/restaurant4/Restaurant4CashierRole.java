package restaurant4;

import agent.Agent;
import restaurant4.interfaces.*;

import java.util.*;

/**
 * Restaurant Cashier Agent
 * 
 * Receives check requests from a waiter, and payment requests from customers and fills them
 */
public class Restaurant4CashierRole extends Agent implements Cashier{
	
	//A list of the various Checks, either requested or being paid
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	private String name;
	private double money = 200.00;
	//Lets the Cashier check the prices of items
	private Map<String, Double> foodPrices = Collections.synchronizedMap(new HashMap<String, Double>());

	public Restaurant4CashierRole(String name) {
		super();
		this.name = name;
		foodPrices.put("Steak", 15.99);
		foodPrices.put("Chicken", 10.99);
		foodPrices.put("Salad", 5.99);
		foodPrices.put("Pizza", 8.99);
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
	public void msgINeedCheck(String choice, Customer customer, Waiter waiter){
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
	public void msgPayingForFood(Customer customer, double money){
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
	
	public void msgPleasePay(Market m, double bill){
		bills.add(new Bill(m, bill));
		stateChanged();
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
		
		if(bills.size() != 0){
			payBill(bills.get(0));
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

	private void payBill(Bill b){
		if(money > b.amount){
			b.market.msgPayingBill(b.amount);
			money -= b.amount;
			bills.remove(b);
		}
		else{
			b.market.msgPayingBill(money);
			b.amount -= money;
			money = 0;
			String contract = new String("I, " + name + ", promise to work for you for " + (b.amount/10.0) + " hours to pay off the debt");
			b.market.msgCannotPay(contract);
			bills.remove(b);
		}
	}
	//utilities
	
	public class Bill {
		public Market market;
		public double amount;
		Bill(Market m, double bill){
			amount = bill;
			market = m;
		}
	}
	
	/**
	 * The check class
	 * 
	 * Contains a waiter, a food-type, a price, a customer, an amount spent, and a state
	 */
	public class Check {
		public Waiter waiter;
		public String type;
		//The price of the food
		public double price;
		public Customer cust;
		public state s;
		//The money that the customer spent (before change is given)
		public double money;
		Check(Waiter w, Customer c, String t){
			waiter = w;
			type = t;
			cust = c;
			price = 0;
			s = state.requested;
		}
	}
	public enum state {requested, owed, paid, done}
}
