package restaurant4;

import person.interfaces.Person;

/**
 * Restaurant Waiter Agent
 * 
 * Takes the customer to the table, takes their order
 * Gives it to the cook, takes the food back to the customer
 * and lets the host know when the customer leaves
 */
public class Restaurant4WaiterRole extends Restaurant4AbstractWaiter{

	public Restaurant4WaiterRole(String name, Person pa) {
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
		print("Going to the Cook");
		try{
			movement.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.s = state.sitting;
		cook.msgMakeFood(customer.choice, this, customer.table);
		gui.GoToLocation("Home");
	}

	@Override
	public String getRoleName() {
		return "Restaurant 4 Waiter";
	}
}

