package market.interfaces;

import java.util.List;

import market.Food;
import restaurant1.Restaurant1CookRole;
import restaurant1.interfaces.Cook;

public interface MarketEmployee {

	public abstract void msgCollectOrer(MarketCustomer customer, List<Food> food);

	public abstract void msgCollectTheDilivery(Cook cook, List<Food> food, MarketTruck truck, int number);

	public abstract void msgWorkDayOver();
	
}
