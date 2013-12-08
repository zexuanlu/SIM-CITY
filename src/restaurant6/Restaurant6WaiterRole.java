package restaurant6;

import restaurant6.Restaurant6Order.OrderState;
import restaurant6.interfaces.Restaurant6Waiter;

import java.util.*;

import person.interfaces.Person;

/**
 * Restaurant Waiter Agent
 */

public class Restaurant6WaiterRole extends Restaurant6AbstractWaiterRole implements Restaurant6Waiter {
	
	public Restaurant6WaiterRole(String name, Person p) {
		super(name, p);

		this.name = name;
		
		waiterState = MyWaiterState.Working;
	}

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

	// Gets person's role name
	public String getRoleName() {
		return "Restaurant 6 Waiter";
	}

}

