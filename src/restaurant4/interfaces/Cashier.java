package restaurant4.interfaces;

/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public interface Cashier {
	/**
	 * @param food The food that the customer ordered
	 * @param c The customer who needs to pay
	 * @param w The waiter who is serving the customer
	 *
	 * Sent by the waiter asking for a customer's check
	 */
	public abstract void msgINeedCheck(String food, Customer c, Waiter w);

	/**
	 * @param c The customer who is paying
	 * @param money the customer's money to pay for food
	 *
	 * Sent by the customer to pay for his food
	 */
	public abstract void msgPayingForFood(Customer c, double money);
	
	
	public void msgPleasePay(Market m, double bill);
}