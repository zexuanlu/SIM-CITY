package market.test.mock;

import java.util.List;

import market.CookAgent;
import market.interfaces.Cook;
import market.interfaces.MarketTruck;

public class MockTruck extends Mock implements MarketTruck{

	public EventLog log = new EventLog();
	
	public MockTruck(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}


	@Override
	public void msgPleaseDiliver(Cook c, List food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got it"));
	}

}
