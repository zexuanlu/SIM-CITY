package market.interfaces;

import java.util.List;

import market.CookAgent;
import market.Food;

public interface MarketTruck {
	
	public void gotoPosition(Cook c, List<Food> food, int x, int y);
	
	public void msgGoBack();
}
