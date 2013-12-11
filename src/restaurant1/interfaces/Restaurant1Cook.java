package restaurant1.interfaces;

import java.util.List;

import utilities.restaurant.RestaurantCook;
import market.Food;
import market.interfaces.*;

public interface Restaurant1Cook extends RestaurantCook{
	public abstract void msghereisorder(Restaurant1Waiter w, String choice, int table);
	public abstract void msgHereisYourFood(MarketTruck t, List<Food> fList);
	public abstract void msgEmptyStock();
	public abstract void msgGoOffWork(); 

}
