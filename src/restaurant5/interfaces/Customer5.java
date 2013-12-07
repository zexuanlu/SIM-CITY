package restaurant5.interfaces;

import restaurant.RestaurantCustomer; 
import restaurant5.Menu5;
import restaurant5.gui.CustomerGui5; 
import restaurant5.gui.FoodGui5;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer5 extends RestaurantCustomer {
	/**
	 * @param total The cost according to the cashier
	 *
	 * Sent by the cashier prompting the customer's money after the customer has approached the cashier.
	 */	
	public abstract void msgChange(int Cash); 
	public abstract void msgSendCheck(int Check); 
	public abstract void msgGotHungry(); 
	public void msgRestaurantFull(); 
	public void msgOutofChoice(Menu5 m); 
	public void msgAnimationFinishedLeaveRestaurant(); 
	public void msgAnimationFinishedGoToSeat(); 
	public void msgfollowMe(Waiter5 w, Menu5 m);
	public void msgwhatdoyouWant(Waiter5 w); 
	public void msgHeresYourOrder(Waiter5 w, String choice);
	public void msgfinishedFood();	
}