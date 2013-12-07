package market.interfaces;

import java.util.List;

import restaurant.RestaurantCook;
import restaurant1.Restaurant1CookRole;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(RestaurantCook c, List<Food> food, int x, int y);
	
	public void msgGoBack();
}
