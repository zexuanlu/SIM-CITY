package market.interfaces;

import java.util.List;
import market.Food;

public interface Employee {

	public abstract void msgCollectOrer(Customer customer, List<Food> food);

	public abstract void msgCollectTheDilivery(Cook cook, List<Food> food, Truck truck);
	
}
