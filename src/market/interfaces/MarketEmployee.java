package market.interfaces;

import java.util.List;

import market.Food;
import restaurant1.Restaurant1CookRole;
import utilities.restaurant.RestaurantCook;

public interface MarketEmployee {

	public void msgCollectOrer(MarketCustomer customer, List<Food> food);

	public  void msgCollectTheDilivery(RestaurantCook cook, List<Food> food, MarketTruck truck, int number);

	public  void msgWorkDayOver();
	
}
