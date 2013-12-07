package restaurant;

import java.util.List;

import market.Food;
import market.interfaces.*;

public interface RestaurantCook {
	public abstract void msgHereisYourFood(MarketTruck t, List<Food> fList);
	public abstract void msgEmptyStock();
}
