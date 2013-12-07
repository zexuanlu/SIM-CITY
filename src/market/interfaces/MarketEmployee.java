package market.interfaces;

import java.util.List;

import market.Food;
import restaurant.RestaurantCook;
import restaurant1.Restaurant1CookRole;

public interface MarketEmployee {

	public abstract void msgCollectOrer(MarketCustomer customer, List<Food> food);

	public abstract void msgCollectTheDilivery(RestaurantCook cook, List<Food> food, MarketTruck truck, int number);

	public abstract void msgWorkDayOver();
	
}
