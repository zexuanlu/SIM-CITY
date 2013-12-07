package market.interfaces;

import java.util.List;

import market.*;
import utilities.restaurant.RestaurantCashier;
import utilities.restaurant.RestaurantCook;

public interface MarketCashier {

	public abstract void msgHereisOrder(MarketCustomer customer, List<Food> food);

	public abstract void msgPayment(MarketCustomer customer, double m);

	public abstract void msgHereisProduct(MarketCustomer customer, List<Food> order);

	public abstract void msgGoToTable(MarketCustomer customer);
	// end of in market scenario

	public abstract void MsgIwantFood(RestaurantCook cook, RestaurantCashier ca, List<Food> food, int number);

	public abstract void msgBillFromTheAir(double money);
	
	public void msgTruckBack(MarketTruck t);
	
	public void msgDevliveryFail(MarketTruck t, RestaurantCook cook, List<Food> food);
	
}
