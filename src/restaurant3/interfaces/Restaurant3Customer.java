package restaurant3.interfaces;

import java.util.Map;

import utilities.restaurant.RestaurantCustomer;

public interface Restaurant3Customer extends RestaurantCustomer{
	
	//From GUI
	public void gotHungry();
	public void msgAtTableRelease();
	
	//From waiter
	public void msgFollowMeToTable(Restaurant3Waiter w, int table, Map<String, Double> menu);
	public void msgWhatWouldYouLike();
	public void msgHereIsYourFood(String choice);
	public void msgHereIsYourBill(double bill);
	public void msgHereIsChangeAndReceipt(double change); 
	
	//Helpers
	public String getName();
}
