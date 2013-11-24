package market.test.mock;

import java.util.List;

import market.CookAgent;
import market.interfaces.Cook;
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
	public void msgCollectOrer(MarketCustomer customer, List food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
		
	}


	@Override
	public void msgCollectTheDilivery(Cook cook, List food, MarketTruck truck) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}

}
