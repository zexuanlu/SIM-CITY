package restaurant6.test.mock;

import java.util.List;

import market.Food;
import market.interfaces.MarketTruck;
import restaurant6.interfaces.Restaurant6Cook;

public class MockCook extends Mock implements Restaurant6Cook{
	
	public EventLog log = new EventLog();
	
	public MockCook(String name) {
		super(name);
	}

	/**
	 * Cook gets message from the market with delivery of food
	 */
	public void msgHereisYourFood(MarketTruck t, List<Food> fList) {
		log.add(new LoggedEvent("Received food from Market Truck"));
	}

	public void msgEmptyStock() {
		
	}
}
