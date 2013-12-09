package restaurant3.interfaces;

public interface Cook {
	//Messages from GUI
	public void msgAtFrRelease();
	
	//Messages from waiter
	public void msgNewOrder(Waiter w, int table, String choice);
	
	//Helper methods
	public String getName();
}
