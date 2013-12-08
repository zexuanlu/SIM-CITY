package restaurant6;

import agent.Role;

import java.text.DecimalFormat;
import java.util.*;

import person.interfaces.Person;
import market.Food;
import market.interfaces.MarketCashier;
import restaurant6.Restaurant6Check.CheckState;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Customer;
import restaurant6.interfaces.Restaurant6Market;
import restaurant6.interfaces.Restaurant6Waiter;
import restaurant6.test.mock.EventLog;
import restaurant6.test.mock.LoggedEvent;

/**
 * Restaurant Cook Agent
 */

public class Restaurant6CashierRole extends Role implements Restaurant6Cashier {
	
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Map of all food choices to price
	private Map<String, Double> prices = new HashMap<String, Double>();
	
	// List of all the checks that the cashier is keeping track of
	private List<Restaurant6Check> checks = Collections.synchronizedList(new ArrayList<Restaurant6Check>());
	
	// List of all the markets that need to be paid
	private List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	
	public Map<String, Double> getPrices() {
		return prices;
	}
	
	public List<Restaurant6Check> getChecks() {
		return checks;
	}
	
	public List<MyMarket> getMarkets() {
		return markets;
	}
	
	// Inner class for markets to hold invoices
	public static class MyMarket {
		public MarketCashier market;
		public double money;
		public enum MarketState {Paid, NeedToPay}
		public MarketState state;
		
		MyMarket(MarketCashier m, double mn) {
			market = m;
			money = mn;
			state = MarketState.NeedToPay;
		}
	}
	
	private String cashierName;
	private Restaurant6Customer customer;
	private Restaurant6Waiter waiter;	
	private Restaurant6Market market;
	private double restaurantMoney;
	
	Timer atBank = new Timer();
	
	// Returns restaurant money amount - for JUnit testing
	public double getRestaurantMoney() {
		return restaurantMoney;
	}
	
	// Sets restaurant money amount - for JUnit testing
	public void setRestaurantMoney(double m) {
		restaurantMoney = m;
	}

	// CashierAgent constructor
	public Restaurant6CashierRole(String name, Person p) {
		super(p);
		cashierName = name;
		// Creates map of food choices to food objects
		prices.put("Chicken", 10.99);
		prices.put("Steak", 15.99);
		prices.put("Salad", 5.99);
		prices.put("Pizza", 8.99);
		
		// Sets initial amount of restaurant's money
		restaurantMoney = 800;
	}
	
	// Returns the cashier's name
	public String getName() {
		return cashierName;
	}
	
	// Hack to establish connection between waiter and cashier
	public void setWaiter(Restaurant6Waiter w) {
		waiter = w;
	}
	
	// Hack to establish connection between customer and cashier
	public void setCustomer(Restaurant6Customer c) {
		customer = c;
	}
	
	// Gui message saying to set money to 0
	public void msgNoMoney() {
		restaurantMoney = 0;
	}
	
	// List of orders that the cook sent to the market
	private List<Food> marketOrders = Collections.synchronizedList(new ArrayList<Food>());

	// Messages	
	// Message from the cook detailing what was ordered from the market
	public void msgOrderedFood(List<Food> list) {
		// Copy all of the orders into market orders so there is a reference when market sends invoice
		for (Food f : list) {
			marketOrders.add(new Food(f.choice, f.amount));
		}
		stateChanged(); // probably not necessary
	}
	
	// Here there will be a message from the market detailing list of items that the cook ordered
	public void msgPleasepaytheBill(MarketCashier m, double mn) {
		// Checks the list of orders from the market against the reference list created earlier
		
		// If it matches, then pay the market
		
		markets.add(new MyMarket(m, mn));
		stateChanged();
	}

	
	// Message from the waiter asking the cashier to compute the check
	public void pleaseComputeCheck(String choice, Restaurant6Waiter w, Restaurant6Customer c) {
		print("Received order from waiter " + w.getName());
		log.add(new LoggedEvent("Received order from waiter " + w.getName()));
		checks.add(new Restaurant6Check(choice, prices.get(choice) + c.getDebt(), w, c));
		stateChanged();
	}
	
	// Message from the customer requesting to pay
	public void iWouldLikeToPayPlease(double money, Restaurant6Customer c, Restaurant6Check chk) {
		for (Restaurant6Check check : checks) {
			if (check == chk) {
				check.setCheckState(CheckState.customerIsPaying);
				check.setCustomerPaymentAmount(money);
				break;
			}
		}
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/*Checks if the checks list is empty. If the check's state has yet 
		 * to be computed, calculate the amount that the customer owes. 
		 * If the customer is paying for the check right now, then get the money
		 * and return the change (if any) to the customer. 
		 */
		if (!checks.isEmpty()) {
			for (Restaurant6Check c : checks) {
				if (c.getCheckState() == CheckState.toBeComputed) {
					computeCheck(c); 
					return true;
				}
			}

			for (Restaurant6Check c : checks) {
				if (c.getCheckState() == CheckState.customerIsPaying) {
					getPayment(c);
					return true;
				}
			}
		}
		
		if (!markets.isEmpty()) {
			payMarket(markets.get(0));
			return true;
		}
			
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	// Computes the check after receiving order from the waiter
	private void computeCheck(Restaurant6Check c) {
		print("Computing check..");
		
		log.add(new LoggedEvent("Computing check.."));
		
		// Send a message to the waiter giving the check 
		c.getWaiter().msgHereIsTheCheck(c);
		
		// Set state of check to toBePaid
		c.setCheckState(CheckState.givenToWaiter);
	}
	
	// Gets the payment and gives the customer change, if there is any
	private void getPayment(Restaurant6Check c) {
		DecimalFormat df = new DecimalFormat("##.##");
		// Customer pays the exact amount
		if (c.getCustomerPaymentAmount() == c.getBillAmount()) {
			c.setChange(0);
			c.getCustomer().hereIsYourChange(c.getChange());
			c.getCustomer().youHaveDebt(0);
			restaurantMoney += c.getCustomerPaymentAmount();
			print("Your change is $0.00. Have a nice day!");
			
			log.add(new LoggedEvent("Your change is $0.00. Have a nice day!"));
			}
		// Customer doesn't have enough money to pay
		else if (c.getCustomerPaymentAmount() < c.getBillAmount()) {
			c.getCustomer().hereIsYourChange(0);
			c.getCustomer().youHaveDebt(c.getBillAmount() - c.getCustomerPaymentAmount());
			restaurantMoney += c.getCustomerPaymentAmount();
			print("Your payment is not enough. You have to pay next time.");
			
			log.add(new LoggedEvent("Your payment is not enough. You have to pay next time."));
		}
		// Customer pays more than the bill amount and requires change
		else {
			c.setChange(c.getCustomerPaymentAmount() - c.getBillAmount());
			c.getCustomer().hereIsYourChange(c.getChange());
			c.getCustomer().youHaveDebt(0);
			restaurantMoney += c.getBillAmount();
			
			print("Your change is $" + df.format(c.getChange()) + ". Have a nice day!");
			
			log.add(new LoggedEvent("Your change is $" + df.format(c.getChange()) + ". Have a nice day!"));
		}
		print("My money is now $" + df.format(restaurantMoney));
		
		log.add(new LoggedEvent("My money is now $" + df.format(restaurantMoney)));
		
		checks.remove(c);
	}
	
	// Pays the market
	private void payMarket(MyMarket m) {
		DecimalFormat df = new DecimalFormat("###.##");
		
		// Cashier has enough money to pay for the cook's order
		m.market.msgBillFromTheAir(m.money);
		print("Can pay the market in full. Paid $" + m.money);
		log.add(new LoggedEvent("Can pay the market in full. Paid $" + m.money));
		
		restaurantMoney -= m.money;
		
		log.add(new LoggedEvent("My money is now $" + df.format(restaurantMoney)));
		print("My money is now $" + df.format(restaurantMoney));
		markets.remove(m);		
	}

	// Returns the name of the role
	public String getRoleName() {
		return "Restaurant 6 Cashier";
	}
}

