package restaurant.interfaces;

import java.util.List;

import market.Food;
import market.interfaces.*;

public interface Cook {
	public abstract void msgAddMarket(Market m);
	public abstract void msghereisorder(Waiter w, String choice, int table);
	public abstract void msgHereisYourFood(MarketTruck t, List<Food> fList);
}
