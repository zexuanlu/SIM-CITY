package restaurant3.interfaces;

import market.interfaces.MarketCashier;

import utilities.restaurant.RestaurantCashier;

public interface Restaurant3Cashier extends RestaurantCashier {
	//Messages from waiter
	public void msgPrepareBill(Restaurant3Waiter w, int table, String choice);
	public void msgHereIsMoney(Restaurant3Waiter w, int table, double money);
	public void msgGoOffWork();
	//Messages from Market
	public void msgPleasepaytheBill(MarketCashier c, double pAmt);
	
	//Helper Methods
	public String getName();
}
