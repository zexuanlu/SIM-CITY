package restaurant1.test.mock;

import market.interfaces.MarketCashier;
import restaurant1.interfaces.Cashier;
import restaurant1.interfaces.Customer;
import restaurant1.interfaces.Waiter;

public class MockRestaurantCashier extends Mock implements Cashier{

	public MockRestaurantCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgCheckthePrice(Waiter w, Customer c, String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayment(Customer c, double paying) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleasepaytheBill(MarketCashier c, double bills) {
		// TODO Auto-generated method stub
		
	}

}
