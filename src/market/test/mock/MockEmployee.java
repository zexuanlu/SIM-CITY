package market.test.mock;

import java.util.List;

import market.CookAgent;
import market.interfaces.Cook;
import market.interfaces.Customer;
import market.interfaces.Employee;
import market.interfaces.Truck;

public class MockEmployee extends Mock implements Employee{

	public EventLog log = new EventLog();
	
	public MockEmployee(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgCollectOrer(Customer customer, List food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
		
	}


	@Override
	public void msgCollectTheDilivery(Cook cook, List food, Truck truck) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}

}
