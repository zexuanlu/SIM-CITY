package restaurant5.interfaces;
 
import restaurant5.Check5; 
import restaurant5.Restaurant5CustomerAgent;
import utilities.restaurant.RestaurantWaiter;

public interface Waiter5 extends RestaurantWaiter {
	public abstract void msgHereisCheck(Check5 check);
	public abstract void msgDoneEating(Restaurant5CustomerAgent c);
	public abstract void msgseatCustomer(Restaurant5CustomerAgent cust, int table);
	public abstract void msgoffBreak();
//	public abstract void msgWanttoGoonBreak();
	public abstract void msggoOnBreak();
	public abstract void msgGoOffWork();
	public abstract void msgcantgoOnBreak();
	public abstract void msgOutof(String choice, int table);
	public abstract void msgReadytoOrder(Restaurant5CustomerAgent cust);
	public abstract void msghereisChoice(Restaurant5CustomerAgent c, String choice);
	public abstract void msgorderDone(String choice, int table);
	public abstract void msgDoneandLeaving(Restaurant5CustomerAgent c);
	public abstract String getName(); 
	public abstract int getPlace(Restaurant5CustomerAgent c); 
}
