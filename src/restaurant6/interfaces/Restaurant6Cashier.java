package restaurant6.interfaces;

import java.util.List;

import market.Food;
import restaurant6.Restaurant6Check;
import restaurant6.Restaurant6Invoice;
import restaurant6.Restaurant6Restock;
import utilities.restaurant.RestaurantCashier;

public interface Restaurant6Cashier extends RestaurantCashier {
	/**
	 *	Asked by the waiter to compute the check
	 */
	public abstract void pleaseComputeCheck(String choice, Restaurant6Waiter w, Restaurant6Customer c); 
	
	/**
	 *	Told by the customer that the cashier would like to pay
	 */
	public abstract void iWouldLikeToPayPlease(double money, Restaurant6Customer c, Restaurant6Check chk);

	/**
	 * Sent by cook after ordered inventory
	 * @param items
	 */
	public abstract void msgOrderedFood(List<Food> items);

}
