package restaurant5;
import person.PersonAgent; 
import restaurant5.WaiterBase5; 

/**
 * Restaurant Host Agent
 */
public class Restaurant5WaiterAgent extends WaiterBase5 {
	
	public Restaurant5WaiterAgent(String _name, PersonAgent p) {
		super(_name, p);	
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
	
	public String toString(){
		return name; 
	}

	public String getRoleName(){
		return "Restaurant 5 Waiter";
	}
	
}