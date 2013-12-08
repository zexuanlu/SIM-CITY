package restaurant4;

import java.util.*;

/**
 * The Menu Class
 * 
 * Contains the options (and later prices) of food in the restaurant
 */
public class Restaurant4Menu {
	public List<MenuItem> options = new ArrayList<MenuItem>();
	
	Restaurant4Menu(){
		options.add(new MenuItem(8.99, "Shrimp"));
		options.add(new MenuItem(7.99, "Scallops"));
		options.add(new MenuItem(14.99, "Lobster"));
		options.add(new MenuItem(13.99, "Crab"));
	}
	
	public void remove(String item){
		for(MenuItem mi : options){
			if(mi.type.equals(item)){
				options.remove(mi);
				System.out.println("Size: " + options.size());
				return;
			}
		}
	}
	
	public boolean contains(String item){
		for(MenuItem mi : options){
			if(mi.type.equals(item))
				return true;
		}
		return false;
	}
	
	public class MenuItem{
		String type;
		double price;
		MenuItem(double p, String t){
			price = p;
			type = t;
		}
	}
}
