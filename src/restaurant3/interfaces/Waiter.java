package restaurant3.interfaces;

public interface Waiter {
	//Messages from host
	public void msgSeatCustomerAtTable(Customer c, int table);
	
	//Messages from customer
	public void msgReadyToOrder(Customer c);
	public void msgHereIsMyChoice(Customer c, String choice);
	public void msgDoneEatingCheckPls(Customer c);
	public void msgHereIsMyMoney(Customer c, double money);
	public void msgLeavingTable(Customer c);
	
	//Messages from cook
	public void msgOrderReady(int table, String choice);
	
	//Messages from cashier
	public void msgHereIsABill(int table, double bill);
	public void msgHereIsChangeReceipt(int table, double change);
	
	//Helper methods
	public String getName();
}
