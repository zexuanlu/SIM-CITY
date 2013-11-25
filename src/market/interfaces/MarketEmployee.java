package market.interfaces;

import java.util.List;
import market.Food;

public interface MarketEmployee {

	public abstract void msgCollectOrer(MarketCustomer customer, List<Food> food);

	public abstract void msgCollectTheDilivery(Cook cook, List<Food> food, MarketTruck truck, int number);
	
}
