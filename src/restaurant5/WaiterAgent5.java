package restaurant5;
import restaurant5.WaiterBase5; 

/**
 * Restaurant Host Agent
 */
public class WaiterAgent5 extends WaiterBase5 {
	
	public WaiterAgent5(String _name) {
		super();	
		name = _name; 
		waiterState = wState.ready; 
	}
	// Actions

	protected void handleOrder(myCustomer c){
		withOrder.drainPermits();
		waiterGui.DoGoToCook();
		try {
			withOrder.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Waiter here is Order");
		c.s = CustomerState.cooking;
		//c.s = CustomerState.computingCheck; 
		myCook.msgHereIsOrder(this,c.choice,c.tablenum);
		waiterGui.DoLeaveCustomer();
		//stateChanged();
	}
	
	public void msgatStand(){
		
	}
	
}