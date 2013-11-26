package market.interfaces;

import java.util.List;

import restaurant.Restaurant1CookRole;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(Restaurant1CookRole c, List<Food> food, int x, int y);
	
	public void msgGoBack();
}
