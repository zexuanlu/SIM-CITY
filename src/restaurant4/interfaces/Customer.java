package restaurant4.interfaces;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	
	/**
	 * @param change The change from the cashier
	 *
	 * Sent by the cashier giving the customer his change after he receives the payment
	 */
	public abstract void msgHereIsChange(double change);

}