package restaurant5;
import java.util.concurrent.Semaphore;
import restaurant5.WaiterBase5; 
import restaurant5.CookAgent5.Order; 
/**
 * Restaurant Host Agent
 */
public class SDWaiterAgent5 extends WaiterBase5 {
	protected Semaphore atStand = new Semaphore(0,true);
	public RevolvingStand5 revolvingstand; 
	
	public SDWaiterAgent5(String _name) {
		super();	
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
	public void setStand(RevolvingStand5 rs){
		revolvingstand = rs; 
	}
	
}