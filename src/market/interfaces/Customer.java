package market.interfaces;
import java.util.*;

import market.Food;

public interface Customer {

	public abstract void msgHello();
	
	public abstract void msgPleasePay(int b);

	public abstract void msgHereisYourChange(double change);

	public abstract void msgYourFoodReady();

	public abstract void msgHereisYourOrder(List<Food> order);
	
	
}
