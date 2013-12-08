package restaurant1.interfaces;

import java.util.Map;

import utilities.restaurant.RestaurantCustomer;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Restaurant1Customer extends RestaurantCustomer{

public void msgAskforStatus();

public void msgSitAtTable(int a, Map<String, Double> s);

public void msgAnimationFinishedGoToSeat() ;

public void msgReorder(String choice);

public void msgwhatyouwant();

public void msgordercooked(Restaurant1Cashier ca);
	
	public void msgHereisYourBill(int bill);

	public void msgHereisYourChange(double change);

public void msgAnimationFinishedLeaveRestaurant() ;

}