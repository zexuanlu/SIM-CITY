package restaurant6.interfaces;

import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6FoodMenu;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Restaurant6Customer {

	/**
	 * @param total change (if any) due to the customer
	 *
	 * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
	 */
	public abstract void hereIsYourChange(double total);


	/**
	 * @param remaining_cost how much money is owed
	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
	 */
	public abstract void youHaveDebt(double remaining_cost);

	/**
	 * Returns the debt amount for the customer
	 * @return
	 */
	public abstract Double getDebt();

	/**
	 * Sent by the waiter giving the customer the check
	 * @param check
	 */
	public abstract void hereIsYourCheck(Restaurant6Check check);

	/**
	 * Sent by the waiter when delivering food to the customer
	 * @param order
	 */
	public abstract void hereIsYourFood(String order);

	/**
	 * Sent by the waiter when the waiter has gotten to the front and is ready to seat the customer
	 * @param tableNum
	 * @param foodMenu
	 * @param waiterAgent
	 */
	public abstract void followMe(int tableNum, Restaurant6FoodMenu foodMenu,
			Restaurant6Waiter waiterAgent);

	/**
	 * Sent by the waiter asking what the customer wants to order
	 */
	public abstract void whatWouldYouLike();

	/**
	 * Sent by the waiter when the restaurant runs out of the customer's order
	 * @param menuWithoutChoice
	 */
	public abstract void pleaseOrderSomethingNew(String menuWithoutChoice);

}