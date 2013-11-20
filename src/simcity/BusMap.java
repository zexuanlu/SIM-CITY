package simcity;
import java.util.*;
import simcity.interfaces.BusStop; 

public class BusMap {
	public Map<String,BusStop> busstops = new HashMap<String,BusStop>(); 
	
	public BusStop getBusStop(String s){
		return busstops.get(s);
	}
	public void add(String s, BusStop b){
		busstops.put(s, b);
	}
	public int getSize(){
		return busstops.size();
	}
}
