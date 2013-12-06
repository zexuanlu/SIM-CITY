package restaurant2;

import java.util.HashMap;
import java.util.Map;

import restaurant2.interfaces.Waiter;
import restaurant2.interfaces.Customer;
import restaurant2.Restaurant2CookRole.OrderState;

public class Restaurant2Order {

	Waiter waiter;
	Customer customer;
	String order;
	Map<String, String> Abbr = new HashMap<String, String>();
	OrderState state;
	
	Restaurant2Order (Waiter waiter2, Customer c, String o)
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
	Waiter getWaiter(){
		return waiter;
	}
	Customer getCustomer(){
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
