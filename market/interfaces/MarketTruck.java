package market.interfaces;

import java.util.List;

import restaurant.Restaurant1CookRole;
import restaurant.interfaces.Cook;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(Cook c, List<Food> food, int x, int y);
	
	public void msgGoBack();
}
