package restaurant4;

import person.interfaces.Person;
import restaurant4.shareddata.Order;
import restaurant4.shareddata.Restaurant4RevolvingStand;

/**
 * Restaurant Waiter Agent
 * 
 * Takes the customer to the table, takes their order
 * Gives it to the cook, takes the food back to the customer
 * and lets the host know when the customer leaves
 */
public class Restaurant4SDWaiterRole extends Restaurant4AbstractWaiter{

	public Restaurant4RevolvingStand stand = null;
	public Restaurant4SDWaiterRole(String name, Person pa) {
		super(pa);
		this.name = name;
	}
	// Actions
	/**
	 * Gives the customer's order to the cook
	 * 
	 * @param customer the customer who has ordered
	 */
	protected void giveOrderToCook(MyCustomer customer){
		gui.GoToLocation("Cook");
		try{
			movement.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		stand.insertOrder(new Order(this, customer.choice, customer.table));
		customer.s = state.sitting;
		gui.GoToLocation("Home");
	}

	@Override
	public String getRoleName() {
		return "Restaurant 4 SD Waiter";
	}
}

