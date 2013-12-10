package restaurant4.interfaces;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Restaurant4Waiter {
	/**
	 * @param check The amount the customer owes
	 * @param c The customer who owes that amount
	 *
	 * Sent by the cashier giving the waiter the check for a specific customer
	 */
	public abstract void msgHereIsCheck(Restaurant4Customer c, double check);

	public abstract void msgReadyToOrder(Restaurant4Customer customer);

	public abstract void msgHereIsOrder(Restaurant4Customer customer, String choice);

	public abstract void msgINeedCheck(Restaurant4Customer customer);

	public abstract void msgLeavingTable(Restaurant4Customer customer);

	public abstract void msgCannotGoOnBreak();

	public abstract void msgCanGoOnBreak();

	public abstract void msgSeatCustomer(Restaurant4Customer customer, int tableNumber, Restaurant4Host host,String string);

	public abstract void msgWorkDayOver();

}