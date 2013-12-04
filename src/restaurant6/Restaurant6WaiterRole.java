package restaurant6;

import restaurant6.Restaurant6Order.OrderState;
import restaurant6.interfaces.Restaurant6Waiter;

import java.util.*;

/**
 * Restaurant Waiter Agent
 */

public class Restaurant6WaiterRole extends Restaurant6AbstractWaiterRole implements Restaurant6Waiter {
	
	public Restaurant6WaiterRole(String name) {
		super(name);

		this.name = name;
		
		waiterState = MyWaiterState.Working;
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */


	// Actions
	// Goes to cook to put in order
	protected void goToCook(MyCustomer customer) {		
		waiterGui.DoGoToCook(); // GUI goes to cook
		
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		customer.setState(MyCustomer.CustState.WaitingForFood);
		cook.hereIsAnOrder(customer.order); // Messages cook the customer's order
		waiterGui.DoGoToHomePosition(); // GUI leaves cook
	}

}

