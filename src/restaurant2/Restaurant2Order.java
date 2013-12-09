package restaurant2;

import java.util.HashMap;
import java.util.Map;

import restaurant2.interfaces.Restaurant2Waiter;
import restaurant2.interfaces.Restaurant2Customer;
import restaurant2.Restaurant2CookRole.OrderState;

public class Restaurant2Order {

	Restaurant2Waiter waiter;
	Restaurant2Customer customer;
	String order;
	Map<String, String> Abbr = new HashMap<String, String>();
	OrderState state;
	
	Restaurant2Order (Restaurant2Waiter waiter2, Restaurant2Customer c, String o)
	{
		Abbr.put("Steak", "ST");
		Abbr.put("Hamburger", "B");
		Abbr.put("Salad", "SL");
		Abbr.put("Chicken", "C");
		Abbr.put("Ribs", "R");
		Abbr.put("Pound Cake", "PC");

		waiter = waiter2;
		customer = c;
		order = o;

		state = OrderState.Uncooked;

	}
	Restaurant2Waiter getWaiter(){
		return waiter;
	}
	Restaurant2Customer getCustomer(){
		return customer;
	}
	String getOrder(){
		return order;
	}
	String getOrderAbbr(String full)
	{
		return Abbr.get(full);
	}

}
