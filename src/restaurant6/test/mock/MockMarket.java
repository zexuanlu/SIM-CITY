package restaurant6.test.mock;

import java.util.List;

import person.Restaurant;
import market.Food;
import market.interfaces.MarketCashier;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketTruck;
import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Invoice;
import restaurant6.Restaurant6Restock;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Market;
import utilities.restaurant.RestaurantCashier;
import utilities.restaurant.RestaurantCook;

/**
 * Mock Market class to test market/cashier interaction
 * @author jenniezhou
 *
 */
public class MockMarket extends Mock implements MarketCashier {
	
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
	public void msgBillFromTheAir(double money) {
		log.add(new LoggedEvent("Received payment of $" + money + " from cashier."));
	}

	public void msgHereisOrder(MarketCustomer customer, List<Food> food) {
		
	}

	public void msgPayment(MarketCustomer customer, double m) {
		
	}

	public void msgHereisProduct(MarketCustomer customer, List<Food> order) {
		
	}

	public void msgGoToTable(MarketCustomer customer) {
		
	}

	public void MsgIwantFood(RestaurantCook cook, RestaurantCashier ca, List<Food> food, int number) {
		log.add(new LoggedEvent("Received order from cook."));	
		for (Food r : food) {
				log.add(new LoggedEvent("Can fulfill the order of " + r.amount + r.choice));
		}	
	}

	public void msgTruckBack(MarketTruck t) {
		
	}

	public void msgDevliveryFail(MarketTruck t, RestaurantCook cook, List<Food> food, Restaurant r, int restnum) {
		
	}

}
