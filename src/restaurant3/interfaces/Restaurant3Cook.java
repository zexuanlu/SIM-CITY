package restaurant3.interfaces;

import java.util.List;

import market.Food;
import market.interfaces.MarketTruck;

import utilities.restaurant.RestaurantCook;

public interface Restaurant3Cook extends RestaurantCook {
	//Messages from GUI
	public void msgAtFrRelease();
	public void msgGoOffWork();
	//Messages from waiter
	public void msgNewOrder(Restaurant3Waiter w, int table, String choice);
	
	//Messages from sd waiter
	public void msgAddedOrderToRevolvingStand();
	
	//Messages from market components
	public void msgHereisYourFood(MarketTruck t, List<Food> fList);
	
	//Helper methods
	public String getName();
}
