package market.interfaces;
import java.util.*;

import market.Food;

public interface MarketCustomer {

	public abstract void msgHello(double m, List<Food> f);
	
	public abstract void msgPleasePay(int b);

	public abstract void msgHereisYourChange(double change, int num);

	public abstract void msgYourFoodReady();

	public abstract void msgHereisYourOrder(List<Food> order);
	
	public void msgDoneLeaving();
}
