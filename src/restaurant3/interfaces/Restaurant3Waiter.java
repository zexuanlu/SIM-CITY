package restaurant3.interfaces;

import utilities.restaurant.RestaurantWaiter;

public interface Restaurant3Waiter extends RestaurantWaiter{
	//Messages from host
	public void msgSeatCustomerAtTable(Restaurant3Customer c, int table);
	
	//Messages from customer
	public void msgReadyToOrder(Restaurant3Customer c);
	public void msgHereIsMyChoice(Restaurant3Customer c, String choice);
	public void msgDoneEatingCheckPls(Restaurant3Customer c);
	public void msgHereIsMyMoney(Restaurant3Customer c, double money);
	public void msgLeavingTable(Restaurant3Customer c);
	
	//Messages from cook
	public void msgOrderReady(int table, String choice);
	
	//Messages from cashier
	public void msgHereIsABill(int table, double bill);
	public void msgHereIsChangeReceipt(int table, double change);
	
	//Helper methods
	public String getName();

	//Messages from GUI
	public void msgAtTableRelease();
	public void msgAtCookRelease();
}
