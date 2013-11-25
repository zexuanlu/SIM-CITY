package market.test.mock;

import java.util.List;

import market.CashAgent;
import market.MarketCashierRole;
import market.CookAgent;
import market.Food;
import market.interfaces.MarketCashier;
import market.interfaces.Cook;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketTruck;

public class MockCashier extends Mock implements MarketCashier{

	public EventLog log = new EventLog();
	
	public MockCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereisOrder(MarketCustomer customer, List<Food> food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}

	@Override
	public void msgPayment(MarketCustomer customer, double m) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive "+m));
	}

	@Override
	public void msgHereisProduct(MarketCustomer customer, List<Food> order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got it"));
	}

	@Override
	public void msgGoToTable(MarketCustomer customer) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("You are at the table"));
	}

	@Override
	public void MsgIwantFood(Cook cook, CashAgent ca, List<Food> food, int num) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBillFromTheAir(CashAgent ca, double money) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void msgTruckBack(MarketTruck t) {
		// TODO Auto-generated method stub
		
	}

}
