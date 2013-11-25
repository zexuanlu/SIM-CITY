package restaurant.interfaces;

import java.util.Map;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {

public void gotHungry();

public void msgAskforStatus();

public void msgSitAtTable(int a, Map<String, Double> s);

public void msgAnimationFinishedGoToSeat() ;

public void msgReorder(String choice);

public void msgwhatyouwant();

public void msgordercooked();
	
	public void msgHereisYourBill(int bill);

	public void msgHereisYourChange(double change);

public void msgAnimationFinishedLeaveRestaurant() ;

}