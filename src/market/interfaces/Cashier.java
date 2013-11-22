package market.interfaces;

import java.util.List;

import market.*;

public interface Cashier {

	public abstract void msgHereisOrder(Customer customer, List<Food> food);

	public abstract void msgPayment(Customer customer, double m);

	public abstract void msgHereisProduct(Customer customer, List<Food> order);

	public abstract void msgGoToTable(Customer customer);
	// end of in market scenario

	public abstract void MsgIwantFood(Cook cook, CashAgent ca, List<Food> food);

	public abstract void msgBillFromTheAir(CashAgent ca, double money);
	
	public void msgTruckBack(Truck t);
	
}
