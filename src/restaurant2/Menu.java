package restaurant2;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Menu {
	List<String> restrictions = new ArrayList<String>();
	Map<Integer, String> menu = new HashMap<Integer, String>();
	Map<String, Integer> prices = new HashMap<String, Integer>();
	Random chooser = new Random();

	Menu(){
		menu.put(0, "Steak");
		menu.put(1, "Hamburger");
		menu.put(2,"Chicken");
		menu.put(3, "Ribs");
		menu.put(4, "Salad");
		menu.put(5, "Pound Cake");

		prices.put("Steak", 20);
		prices.put("Hamburger", 8);
		prices.put("Ribs", 12);
		prices.put("Chicken", 10);
		prices.put("Salad", 5);
		prices.put("Pound Cake", 2);
	}
	void setSpecial(String food)
	{
		menu.put(6, food);
	}
	String getMeal(int index)
	{
		return menu.get(index);
	}
	String chooseMeal()
	{
		if(!checkRestrictions())//we're not out of options 
		{
			int choice = chooser.nextInt(6);
			String meal = menu.get(choice);
			for(String r : restrictions)
			{
				if(r.equals(meal))
				{
					chooseMeal();
				}
			}
			return menu.get(choice);
		}
		else 
			return "none";
	}
	boolean checkPrice(String mealChoice, int cashOnHand)
	{
		if(prices.get(mealChoice) <= cashOnHand)
		{
			return true;
		}
		else 
			return false;
	}
	void restrict(String restriction)
	{
		if(!restrictions.contains(restriction)){
			restrictions.add(restriction);
		}
	}
	boolean checkRestrictions()
	{
		if(restrictions.size() == 6)
		{
			return true;
		}
		else 
			return false;
	}
	String reChoose(String prevChoice)
	{

		int choice = chooser.nextInt(6);
		String tempChoice = menu.get(choice);
		if(tempChoice.equals(prevChoice))
		{
			reChoose(tempChoice);
		}
		return tempChoice;
	}

}
