package restaurant4.interfaces;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Waiter {
	/**
	 * @param check The amount the customer owes
	 * @param c The customer who owes that amount
	 *
	 * Sent by the cashier giving the waiter the check for a specific customer
	 */
	public abstract void msgHereIsCheck(Customer c, double check);

}