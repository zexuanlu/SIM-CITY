package restaurant2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check {
	public Map<String, Integer> price = new HashMap<String, Integer>();
	private String itemToBill;
	private Map<String, Integer> itemsToBill = new HashMap<String, Integer>();
	public int total;

	public Check(String item)
	{
		price.put("Steak", 20);
		price.put("Hamburger", 8);
		price.put("Ribs", 12);
		price.put("Chicken", 10);
		price.put("Salad", 5);
		price.put("Pound Cake", 2);

		itemToBill = item;
		Compute();
	}

	public Check(Map<String, Integer> items)
	{
		price.put("Steak", 20);
		price.put("Hamburger", 8);
		price.put("Ribs", 12);
		price.put("Chicken", 10);
		price.put("Salad", 5);
		price.put("Pound Cake", 2);

		itemsToBill = items;
		Compute();
	}
	public void setCheck(Map<String, Integer> items)
	{
		itemsToBill = items;
		Compute();
	}
	private void Compute()
	{
		if(itemsToBill.isEmpty())
		{
			total = price.get(itemToBill);
		}
		else
		{
			for(Map.Entry<String, Integer> entry: itemsToBill.entrySet())
			{
				total += (price.get(entry.getKey()) * entry.getValue());//get the price from our price map and multiply by the quanity of the order
			}
		}
	}
	public void setCheck(int newBalance){
		total -= newBalance;
	}
	public int getCheck()
	{	
		/*if(itemsToBill.isEmpty())
		{
			total = price.get(itemToBill);
		}
		else
		{
			for(Map.Entry<String, Integer> entry: itemsToBill.entrySet())
			{
				total += (price.get(entry.getKey()) * entry.getValue());//get the price from our price map and multiply by the quanity of the order
			}
		}
		System.out.println("Total to pay: "+total);*/
		return total;

	}
}
