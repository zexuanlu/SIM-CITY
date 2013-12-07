package market.test.mock;

import java.util.List;

import restaurant.RestaurantCook;
import restaurant1.Restaurant1CookRole;
import market.Food;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketEmployee;
import market.interfaces.MarketTruck;

public class MockEmployee extends Mock implements MarketEmployee{

	public EventLog log = new EventLog();
	
	public MockEmployee(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void msgCollectOrer(MarketCustomer customer, List<Food> food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
		
	}

	@Override
	public void msgCollectTheDilivery(RestaurantCook cook,
			List<Food> food, MarketTruck truck, int number) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}


	@Override
	public void msgWorkDayOver() {
		// TODO Auto-generated method stub
		
	}

}
