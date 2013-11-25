package market.interfaces;

import java.util.List;

import market.*;
import restaurant.Restaurant1CookRole;
import restaurant.interfaces.Cashier;

public interface MarketCashier {

	public abstract void msgHereisOrder(MarketCustomer customer, List<Food> food);

	public abstract void msgPayment(MarketCustomer customer, double m);

	public abstract void msgHereisProduct(MarketCustomer customer, List<Food> order);

	public abstract void msgGoToTable(MarketCustomer customer);
	// end of in market scenario

	public abstract void MsgIwantFood(Restaurant1CookRole cook, CashAgent ca, List<Food> food, int number);

	public abstract void msgBillFromTheAir(Cashier ca, double money);
	
	public void msgTruckBack(MarketTruck t);
	
}
