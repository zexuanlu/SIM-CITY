package restaurant1.test.mock;

import java.util.List;

import market.Food;
import market.interfaces.MarketTruck;
import restaurant4.interfaces.Restaurant4Waiter;
import utilities.restaurant.RestaurantCook;

public class MockRestaurantCook extends Mock implements RestaurantCook{

	public MockRestaurantCook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereisYourFood(MarketTruck t, List<Food> fList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msghereisorder(Restaurant4Waiter w, String choice,
			int table) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgEmptyStock() {
		// TODO Auto-generated method stub
		
	}

}