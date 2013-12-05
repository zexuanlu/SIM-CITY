package restaurant2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MarketOrder {
	Map<String, Integer> items;
	Check total;
	
	MarketOrder(Map<String, Integer> i)
	{
		items = i;
		total = new Check(items);
	}
	Check getCheck()
	{
		return total;
	}
	Map<String, Integer> getItemsInOrder()
	{
		return items;
	}
}
