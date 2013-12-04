package restaurant6;

import java.util.ArrayList; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import restaurant6.Restaurant6Invoice.InvoiceState;
import restaurant6.Restaurant6Order.OrderState;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Market;
import agent.Agent;

public class MarketAgent extends Agent implements Restaurant6Market{
	// Data
	private String name;
	int number;
	private Map<String, Integer> inventory = new HashMap<String, Integer>();
	private Map<String, Double> inventoryPrices = new HashMap<String, Double>();
	private Random marketGenerator = new Random();
	private Restaurant6CookRole cook;
	private Restaurant6Cashier cashier; 
	private Timer marketTimer = new Timer();
	private boolean canFulfillAll; // If the market can fulfill all of the cook's orders or not
	private boolean orderReceived;
	
	private final int chickenInventory = 5;
	private final int steakInventory = 5;
	private final int saladInventory = 5;
	private final int pizzaInventory = 5;
	
	// To generate random numbers of inventory for the market
	private int generateInventory() {
		int r = marketGenerator.nextInt(3);
		return r;
	}
	
	// Keeps track of the list of orders from the cook
	private List<Restaurant6Restock> ordersFromCook = Collections.synchronizedList(new ArrayList<Restaurant6Restock>());
	
	// Sends a list back to cook telling the cook what the market cannot fulfill
	private List<Restaurant6Restock> ordersToCook = Collections.synchronizedList(new ArrayList<Restaurant6Restock>());
	
	// To keep track of invoices sent to cashier
	private List<Restaurant6Invoice> invoices = Collections.synchronizedList(new ArrayList<Restaurant6Invoice>());
	
	public void setCashier(Restaurant6Cashier c) {
		cashier = c;
	}

	// Constructor for the market
	public MarketAgent(String n, int m) {
		super();
		name = n;
		number = m;
		
		orderReceived = false;
		
		inventory.put("Chicken", chickenInventory); //new Integer(generateInventory()));
		inventory.put("Steak", steakInventory); //new Integer(generateInventory()));
		inventory.put("Salad", saladInventory); //new Integer(generateInventory()));
		inventory.put("Pizza", pizzaInventory); //new Integer(generateInventory()));
		
		inventoryPrices.put("Chicken", 10.99);
		inventoryPrices.put("Steak", 15.99);
		inventoryPrices.put("Salad", 5.99);
		inventoryPrices.put("Pizza", 8.99);
	}
	
	// Gets the name of the market
	public String getName() {
		return name;
	}
	
	// Hack to access cook
	public void setCook(Restaurant6CookRole c) {
		this.cook = c;
	}
	
	// Messages
	// Message from the cook ordering food 
	public void msgOrderFood(List<Restaurant6Restock> orders) {	
		orderReceived = true;
		
		// Copy all orders from orders list that cook sent to own market list
		for (int i = 0; i < orders.size(); i++) {
			ordersFromCook.add(new Restaurant6Restock(orders.get(i).getOrderChoice(), orders.get(i).getAmount()));
		}
	
		stateChanged();
	}
	
	// Message from the cashier with payment
	public void msgHereIsThePayment(double amount, Restaurant6Invoice in) {
		for (Restaurant6Invoice invoice : invoices) {
			if (invoice == in) {
				if (invoice.getTotal() == amount) {
					// If the invoice's total is equal to the amount the cashier is paying, set state as paid
					invoice.setState(InvoiceState.Paid);
					invoice.setPaidAmount(amount);
				}
				else if (invoice.getTotal() > amount) {
					// If the invoice's total is greater than the amount the cashier is paying, set state as incomplete
					invoice.setState(InvoiceState.Incomplete);
					invoice.setPaidAmount(amount);
				}
			}
		}
		stateChanged();
	}
	
	// Scheduler
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if (orderReceived) {
			fulfillOrder(ordersFromCook);
			return true;
		}
		
		if (!invoices.isEmpty()) {
			for (Restaurant6Invoice in : invoices) {
				if (in.getState() == InvoiceState.Paid) {
					removeInvoice(in);
					return true;
				}
			}
			
			for (Restaurant6Invoice in : invoices) {
				if (in.getState() == InvoiceState.Incomplete) {
					tellCashierDoSomething(in);
					return true;
				}
			}
		}
		return false;
	}
	
	// Actions
	private void fulfillOrder(List<Restaurant6Restock> ordersFromCook) {
		orderReceived = false;
		
		// Loop through each restock item in the list and check inventory
		int temp = 0;
		final List<Restaurant6Restock> ordersCanFulfill = Collections.synchronizedList(new ArrayList<Restaurant6Restock>());
		
		for (Restaurant6Restock r : ordersFromCook) {
			/* If the amount of the order is less than or equal to the inventory amount,
			 * set the new inventory amount equal to the difference
			 */
			if (r.getAmount() <= inventory.get(r.getOrderChoice())) {
				print("I can fulfill the full order");
				temp = inventory.get(r.getOrderChoice()) - r.getAmount();
				inventory.put(r.getOrderChoice(), temp);
				ordersCanFulfill.add(new Restaurant6Restock(r.getOrderChoice(), r.getAmount()));
			}
			
			/* If the amount of the order is greater than the inventory amount but not equal to zero,
			 * set the new inventory amount to 0 and send a list back containing what it cannot fulfill
			 */
			else if (r.getAmount() > inventory.get(r.getOrderChoice())) {
				print("I cannot fulfill the full order, but I can fulfill " + inventory.get(r.getOrderChoice()) + " of " + r.getOrderChoice());
				temp = r.getAmount() - inventory.get(r.getOrderChoice());
				ordersCanFulfill.add(new Restaurant6Restock(r.getOrderChoice(), inventory.get(r.getOrderChoice())));
				inventory.put(r.getOrderChoice(), 0);
				ordersToCook.add(new Restaurant6Restock(r.getOrderChoice(), temp));
			}
		}
		
		if (ordersToCook.isEmpty()) {
			canFulfillAll = true;
		}
		
		if (!canFulfillAll) {
			cook.msgCannotFulfill(ordersToCook);
		}		
		
		double tempAmount = 0; // To hold the amount that the cashier will owe
		
		for (Restaurant6Restock r : ordersCanFulfill) { // Calculating the bill
			tempAmount += (r.getAmount() * inventoryPrices.get(r.getOrderChoice()));
		}
		
		Restaurant6Invoice tempInvoice = new Restaurant6Invoice(number, tempAmount);
		
		// Sends the list of orders for cashier to calculate
		cashier.msgInvoice(this, tempInvoice);

		// Adds to the list of invoices for market to keep track of
		invoices.add(tempInvoice);
		
		marketTimer.schedule(new TimerTask() {
			public void run() {
				print("Finished fulfilling the cook's request");
				// Send a message to the cook fulfilling order
				cook.orderFulfilled(ordersCanFulfill);
				stateChanged();
			}
		}, 50000);
		
		canFulfillAll = false; // Reinitialize the boolean 
		
	}
	
	private void removeInvoice(Restaurant6Invoice in) {
		print("Received cashier's payment of $" + in.getPaidAmount());
		invoices.remove(in);
	}
	
	private void tellCashierDoSomething(Restaurant6Invoice in) {
		invoices.remove(in);
	}

}
