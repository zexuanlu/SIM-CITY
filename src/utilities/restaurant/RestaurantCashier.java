package utilities.restaurant;

import market.interfaces.MarketCashier;

public interface RestaurantCashier {
	public abstract void msgPleasepaytheBill(MarketCashier c, double bills);

	public abstract void msgAddMoney(double amount);
}
