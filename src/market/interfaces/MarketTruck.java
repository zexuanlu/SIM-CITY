package market.interfaces;

import java.util.List;

import utilities.restaurant.RestaurantCook;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(RestaurantCook c, List<Food> food,  int restaurantnum);
	
	public void msgGoBack();
}
