package restaurant3.interfaces;

public interface Cashier {
	//Messages from waiter
	public void msgPrepareBill(Waiter w, int table, String choice);
	public void msgHereIsMoney(Waiter w, int table, double money);
	
	//Helper Methods
	public String getName();
}
