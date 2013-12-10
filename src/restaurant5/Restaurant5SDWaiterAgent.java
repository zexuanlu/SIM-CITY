package restaurant5;
import person.PersonAgent; 

import java.util.concurrent.Semaphore;

import restaurant5.WaiterBase5; 
import restaurant5.Restaurant5CookAgent.Order; 
/**
 * Restaurant Host Agent
 */
public class Restaurant5SDWaiterAgent extends WaiterBase5 {
	protected Semaphore atStand = new Semaphore(0,true);
	public Restaurant5RevolvingStand revolvingstand; 
	
	public Restaurant5SDWaiterAgent(String _name, PersonAgent p) {
		super(_name, p);	
		name = _name; 
		waiterState = wState.ready; 
	}
	
	public void msgatStand(){
		atStand.release();
	}
	
	
	// Actions
	protected void handleOrder(myCustomer c){
		//SHARED DATA CRAP HERE
		atStand.drainPermits();
		waiterGui.DoGoToStand();
		
		try {
			atStand.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		c.s = CustomerState.cooking;
		revolvingstand.insertOrder(new CookOrder5(this,c.choice,c.tablenum));
		print("Waiter put order on Revolving Stand");

		waiterGui.DoLeaveCustomer();
	
	}
	
	//utilities 
	public void setStand(Restaurant5RevolvingStand rs){
		revolvingstand = rs; 
	}
	
	public String toString(){
		return name; 
	}

	public String getRoleName(){
		return "Restaurant 5 SDCashier";
	}
	
}