package market.test.mock;

import java.util.List;

import market.interfaces.Cook;

public class MockCook extends Mock implements Cook{

	public EventLog log = new EventLog();
	
	public MockCook(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereisYourFood(List order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Your order arraived"));
	}

}