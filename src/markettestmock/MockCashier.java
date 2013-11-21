package markettestmock;

import java.util.List;

import market.CashAgent;
import market.CashierAgent;
import market.CookAgent;
import market.Food;
import marketinterface.Cashier;
import marketinterface.Cook;
import marketinterface.Customer;
import marketinterface.Truck;

public class MockCashier extends Mock implements Cashier{

	public EventLog log = new EventLog();
	
	public MockCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereisOrder(Customer customer, List<Food> food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}

	@Override
	public void msgPayment(Customer customer, double m) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive "+m));
	}

	@Override
	public void msgHereisProduct(Customer customer, List<Food> order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got it"));
	}

	@Override
	public void msgGoToTable(Customer customer) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("You are at the table"));
	}

	@Override
	public void MsgIwantFood(Cook cook, CashAgent ca, List<Food> food) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBillFromTheAir(CashAgent ca, double money) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void msgTruckBack(Truck t) {
		// TODO Auto-generated method stub
		
	}

}
