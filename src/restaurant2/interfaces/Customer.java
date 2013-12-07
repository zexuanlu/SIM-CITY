package restaurant2.interfaces;

import restaurant2.Check;
import restaurant2.Menu;
import restaurant2.Restaurant2CustomerRole.AgentEvent;
import restaurant2.Restaurant2CustomerRole.AgentState;


public interface Customer {
	//EventLog log = new EventLog();
	
	public abstract void gotHungry();

	public abstract void msgSitAtTable(Waiter waiter, int tableNum, Menu menu);
	
	public abstract void msgYouHaveToWait();
	
	public abstract void msgAnimationFinishedGoToSeat();
	
	public abstract void msgAnimationFinishedLeaveRestaurant();
	
	public abstract void msgWhatWouldYouLike();
	
	public abstract void msgReOrder();
	
	public abstract void msgServed(String order);
	
	public abstract void msgHeresYourCheck(Check check);
	
	public abstract void msgAtCashier();
	
	public abstract void msgThanksForDining();

	public abstract void msgHeresYourCheck(int check);
	
	public abstract void msgRepay();

	public abstract String getName();
}