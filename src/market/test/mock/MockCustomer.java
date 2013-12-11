package market.test.mock;

import market.Food;
import market.interfaces.MarketCustomer;

import java.util.*;

public class MockCustomer extends Mock implements MarketCustomer{

	public EventLog log = new EventLog();	
	
	public MockCustomer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}



	@Override
	public void msgPleasePay(int b) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got the bill "+ b));
	}

	@Override
	public void msgHereisYourChange(double change, int num) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgYourFoodReady() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Comming to the table"));
		
	}


	@Override
	public void msgHello(double m, List<Food> f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereisYourOrder(List<Food> order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive order"));
	}



	@Override
	public void msgDoneLeaving() {
		// TODO Auto-generated method stub
		
	}

}
