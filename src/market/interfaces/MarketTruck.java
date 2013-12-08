package market.interfaces;

import java.util.List;

import restaurant1.Restaurant1CookRole;
import utilities.restaurant.RestaurantCook;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(RestaurantCook c, List<Food> food, int x, int y, int restaurantnum);
	
	public void msgGoBack();
}
