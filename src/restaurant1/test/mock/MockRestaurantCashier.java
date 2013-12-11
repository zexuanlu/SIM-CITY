package restaurant1.test.mock;

import java.util.List;

import market.Food;
import market.interfaces.MarketCashier;
import restaurant1.interfaces.Restaurant1Cashier;
import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Waiter;

public class MockRestaurantCashier extends Mock implements Restaurant1Cashier{

	public MockRestaurantCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgCheckthePrice(Restaurant1Waiter w, Restaurant1Customer c, String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayment(Restaurant1Customer c, double paying) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleasepaytheBill(MarketCashier c, double bills) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAddMoney(double amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoOffWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgYouCanPayNow(MarketCashier c, List<Food> food) {
		// TODO Auto-generated method stub
		
	}

}
