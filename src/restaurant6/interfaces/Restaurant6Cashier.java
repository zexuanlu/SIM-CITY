package restaurant6.interfaces;

import java.util.List;

import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Invoice;
import restaurant6.Restaurant6Restock;

public interface Restaurant6Cashier {
	/**
	 *	Asked by the waiter to compute the check
	 */
	public abstract void pleaseComputeCheck(String choice, Restaurant6Waiter w, Restaurant6Customer c); 
	
	/**
	 *	Told by the customer that the cashier would like to pay
	 */
	public abstract void iWouldLikeToPayPlease(double money, Restaurant6Customer c, Restaurant6Check chk);

	/**
	 * Sent by the market after cook has ordered more inventory
	 */
	public abstract void msgInvoice(Restaurant6Market marketAgent, Restaurant6Invoice tempInvoice);

	/**
	 * Sent by cook after ordered inventory
	 * @param items
	 */
	public abstract void msgOrderedFood(List<Restaurant6Restock> items);

}
