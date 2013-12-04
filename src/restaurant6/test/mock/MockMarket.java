package restaurant6.test.mock;

import java.util.List;

import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Invoice;
import restaurant6.Restaurant6Restock;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Market;

/**
 * Mock Market class to test market/cashier interaction
 * @author jenniezhou
 *
 */
public class MockMarket extends Mock implements Restaurant6Market {
	
	public EventLog log = new EventLog();
	int inventory;

	public MockMarket(String name) {
		super(name);
		inventory = 5;
	}
	
	public Restaurant6Cashier cashier;
	public Restaurant6Invoice invoice;

	/**
	 * Cook sends a message to the market ordering food
	 * @param orders
	 */
	public void msgOrderFood(List<Restaurant6Restock> orders) {
		log.add(new LoggedEvent("Received order from cook."));	
		for (Restaurant6Restock r : orders) {
			if (r.getAmount() > inventory) {
				log.add(new LoggedEvent("Cannot fulfill the order, but can fulfill " + inventory + r.getOrderChoice()));
			}
			else {
				log.add(new LoggedEvent("Can fulfill the order of " + r.getAmount() + r.getOrderChoice()));
			}
		}	
	}

	/**
	 * Cashier pays the market
	 */
	public void msgHereIsThePayment(double payment, Restaurant6Invoice invoice) {
		log.add(new LoggedEvent("Received payment of $" + payment + " from cashier."));
	}

}
