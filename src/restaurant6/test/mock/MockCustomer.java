package restaurant6.test.mock;


import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6FoodMenu;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.interfaces.Restaurant6Customer;
import restaurant6.interfaces.Restaurant6Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Restaurant6Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Restaurant6Cashier cashier;
	
	public EventLog log = new EventLog();
	
	double debt;

	public MockCustomer(String name) {
		super(name);
		
		debt = 0;
	}

	/**
	 * Cashier gives customer change after customer has paid
	 */
	public void hereIsYourChange(double total) {
		log.add(new LoggedEvent("Received change from cashier. Change: "+ total));
	}

	/**
	 * Customer doesn't have enough money to pay, so the cashier tells the customer 
	 * that they need to pay next time
	 */
	public void youHaveDebt(double dbt) {
		debt = dbt;
		log.add(new LoggedEvent("Received debt amount from cashier. Debt: "+ dbt));
	}

	/**
	 * Sent by the waiter giving customer the check
	 */
	public void hereIsYourCheck(Restaurant6Check check) {
	}

	/**
	 * Gets the customer's debt amount
	 */
	public Double getDebt() {
		return debt;
	}
	
	/**
	 * Waiter gives customer food
	 */
	public void hereIsYourFood(String order) {
	}
	
	/**
	 * Waiter seats customer
	 */
	public void followMe(int tableNum, Restaurant6FoodMenu foodMenu, Restaurant6Waiter waiterAgent) {
	}

	/**
	 * Waiter takes customer's order
	 */
	public void whatWouldYouLike() {
	}

	/**
	 * Waiter takes customer's order after restaurant has run out of food
	 */
	public void pleaseOrderSomethingNew(String menuWithoutChoice) {
	}

}
