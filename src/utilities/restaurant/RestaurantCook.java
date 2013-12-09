package utilities.restaurant;

import java.util.List;

import market.Food;
import market.MarketCashierRole;
import market.interfaces.*;

public interface RestaurantCook {
	public abstract void msgHereisYourFood(MarketTruck t, List<Food> fList);
	public abstract void msgEmptyStock();
	public abstract void setMarketCashier(MarketCashierRole r);
}
