package restaurant6;

import java.util.HashMap;
import java.util.Map;

public class Restaurant6FoodMenu {
	private Map<String, Double> foodMenu = new HashMap<String, Double>();
	private Map<Double, String> reverseMenu = new HashMap<Double, String>();
	
	Restaurant6FoodMenu() {
		foodMenu.put("Mint Chip Ice Cream", 3.99);
		foodMenu.put("Rocky Road Ice Cream", 4.99);
		foodMenu.put("Green Tea Ice Cream", 5.99);
		foodMenu.put("Mocha Almond Fudge Ice Cream", 2.99);
		
		reverseMenu.put(3.99, "Mint Chip Ice Cream");
		reverseMenu.put(4.99, "Rocky Road Ice Cream");
		reverseMenu.put(5.99, "Green Tea Ice Cream");
		reverseMenu.put(2.99, "Mocha Almond Fudge Ice Cream");
	}
	
	public Map<String, Double> getMap() {
		return foodMenu;
	}
	
	public Map<Double, String> getReverseMap() {
		return reverseMenu;
	}
}
