package marketinterface;

import java.util.List;

import market.CookAgent;
import market.Food;

public interface Truck {
	
	public void msgPleaseDiliver(Cook c, List<Food> food);
}
