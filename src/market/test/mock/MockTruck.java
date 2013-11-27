package market.test.mock;

import java.util.List;

import restaurant.Restaurant1CookRole;
import restaurant.interfaces.Cook;
import market.Food;
import market.interfaces.MarketTruck;

public class MockTruck extends Mock implements MarketTruck{

	public EventLog log = new EventLog();
	
	public MockTruck(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgGoBack() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void gotoPosition(Cook c, List<Food> food, int x,
			int y) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got it"));
	}

}
