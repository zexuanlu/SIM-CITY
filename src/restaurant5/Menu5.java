package restaurant5;
import java.util.*;


public class Menu5 {
	private ArrayList<String> myMenu; 
	public Map<String,Integer> prices = new HashMap<String, Integer>();

	Menu5(){
		myMenu = new ArrayList<String>();
		myMenu.add("Steak");
		prices.put("Steak", 16);
		myMenu.add("Chicken");
		prices.put("Chicken", 11);
		myMenu.add("Salad");
		prices.put("Salad", 6);
		myMenu.add("Pizza");
		prices.put("Pizza", 9);
		}
	
	public int getSize(){
		return myMenu.size();
		}
	
	public int getPrice(String choice){
		return prices.get(choice);
	}
	
	public int getCheapest(){
		int temp = 1000000; 
		for (Integer i:prices.values()){
			if (i<temp){
				temp = i;
			}
		}
		return temp; 
	}
	
	public boolean find(String choice){
		for (String s: myMenu){
			if (s.equals(choice)){
				return true; 
			}
		}
		return false; 
	}
	
	public String at(int x){
		return myMenu.get(x);
	}
	
	public void remove(String choice){
		for (String s: myMenu){
			if (s.equals(choice)){
				myMenu.remove(s);
				break;
			}
		}
		prices.remove(choice);
	}
}
