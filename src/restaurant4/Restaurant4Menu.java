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
		options.add(new MenuItem(15.99, "Steak"));
		options.add(new MenuItem(10.99, "Chicken"));
		options.add(new MenuItem(5.99, "Salad"));
		options.add(new MenuItem(8.99, "Pizza"));
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
