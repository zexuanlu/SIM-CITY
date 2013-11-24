package market.interfaces;

import java.util.List;

import market.CookAgent;
import market.Food;

public interface MarketTruck {
	
	public void msgPleaseDiliver(Cook c, List<Food> food);
}
