package market.test.mock;

import market.interfaces.Customer;

import java.util.*;

public class MockCustomer extends Mock implements Customer{

	public EventLog log = new EventLog();	
	
	public MockCustomer(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHello() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleasePay(int b) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got the bill "+ b));
	}

	@Override
	public void msgHereisYourChange(double change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgYourFoodReady() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Comming to the table"));
		
	}

	@Override
	public void msgHereisYourOrder(List order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive order"));
	}

}
