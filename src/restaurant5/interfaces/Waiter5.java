package restaurant5.interfaces;
import restaurant.RestaurantWaiter; 
import restaurant5.Check5; 
import restaurant5.CustomerAgent5;

public interface Waiter5 extends RestaurantWaiter {
	public abstract void msgHereisCheck(Check5 check);
	public abstract void msgDoneEating(CustomerAgent5 c);
	public abstract void msgseatCustomer(CustomerAgent5 cust, int table);
	public abstract void msgoffBreak();
//	public abstract void msgWanttoGoonBreak();
	public abstract void msggoOnBreak();
	public abstract void msgcantgoOnBreak();
	public abstract void msgOutof(String choice, int table);
	public abstract void msgReadytoOrder(CustomerAgent5 cust);
	public abstract void msghereisChoice(CustomerAgent5 c, String choice);
	public abstract void msgorderDone(String choice, int table);
	public abstract void msgDoneandLeaving(CustomerAgent5 c);
	public abstract String getName(); 
	public abstract int getPlace(CustomerAgent5 c); 
}
