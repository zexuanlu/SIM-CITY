package restaurant6;

import java.util.HashMap;
import java.util.Map;

public class Restaurant6FoodMenu {
	private Map<String, Double> foodMenu = new HashMap<String, Double>();
	private Map<Double, String> reverseMenu = new HashMap<Double, String>();
	
	Restaurant6FoodMenu() {
		foodMenu.put("Chicken", 10.99);
		foodMenu.put("Steak", 15.99);
		foodMenu.put("Salad", 5.99);
		foodMenu.put("Pizza", 8.99);
		
		reverseMenu.put(10.99, "Chicken");
		reverseMenu.put(15.99, "Steak");
		reverseMenu.put(5.99, "Salad");
		reverseMenu.put(8.99, "Pizza");
	}
	
	public Map<String, Double> getMap() {
		return foodMenu;
	}
	
	public Map<Double, String> getReverseMap() {
		return reverseMenu;
	}
}
