package restaurant1.interfaces;

import market.interfaces.MarketCashier;


public interface Cashier {

	
	public abstract void msgCheckthePrice(Waiter w, Customer c, String choice);
	
	public abstract void msgPayment(Customer c, double paying);
	
	public abstract void msgPleasepaytheBill(MarketCashier c, double bills);
}
