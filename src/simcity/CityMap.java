package simcity;
import java.util.*;
import java.awt.Dimension;

import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 


public class CityMap {
	Map<String,Dimension> simMap = new HashMap<String,Dimension>();
	//bus information
	
	Map<Bus, ArrayList<BusStop>> routes = new HashMap<Bus, ArrayList<BusStop>>();
	public Map<String,BusStop> busstops = new HashMap<String,BusStop>();  //map of name of stop to 
	public Map<BusStop,Dimension> dimensions = new HashMap<BusStop,Dimension>();
	public Map<BusStop, Bus> busses = new HashMap<BusStop, Bus>();
	
	public void addDestination(String s,Dimension d){
		simMap.put(s, d);
	}
	
	public BusRoute generateBusInformation(String destination, int originx, int originy){
		BusRoute b = new BusRoute();
		

		
		Dimension destdim = simMap.get(destination);
		BusStop destStop = getClosestStop(destdim.width, destdim.height);
		b.destination = getStopName(destStop);
		b.destinationX = dimensions.get(destStop).width;
		b.destinationY = dimensions.get(destStop).height; 
		b.bus = busses.get(destStop);
		
		b.busstop = getClosestStopinRoute(originx, originy,b.bus);
		b.busStopX = getDimension(b.busstop).width; 
		b.busStopY = getDimension(b.busstop).height;
		
		
		return b;
	}
	
	public String getStopName(BusStop b){
		for (Map.Entry<String, BusStop> entry: busstops.entrySet()){
			if (entry.getValue() == b){
				return entry.getKey();
			}
		}
		return null; 
	}
	
	private BusStop getClosestStop(int startx, int starty){
		
		BusStop closest = null; 
		int tempdiff; //kinda weird but I set it as the biggest for comparison reasons
		int numdiff = 1000000; 
		for (Map.Entry<BusStop,Dimension> entry: dimensions.entrySet()){
			tempdiff = 0; 
			tempdiff = tempdiff + Math.abs(entry.getValue().width - startx); 
			tempdiff = tempdiff + Math.abs(entry.getValue().height - starty);
			if (tempdiff < numdiff){
				numdiff = tempdiff; 
				closest = entry.getKey();
			}
		}
		return closest; 
	}
	
private BusStop getClosestStopinRoute(int startx, int starty,Bus b){
		ArrayList<BusStop> broute = routes.get(b);
		BusStop closest = null; 
		int tempdiff = 0; 
		int numdiff = 10000; 
		for (BusStop br: broute){
			Dimension d = dimensions.get(br);
			tempdiff = tempdiff + Math.abs(d.width - startx);
			tempdiff = tempdiff + Math.abs(d.height - starty);
			if (tempdiff < numdiff){
				numdiff = tempdiff; 
				closest = br;
			}
		}
		return closest; 

	}
	
    public Dimension getDimension(BusStop bs){
    	return dimensions.get(bs);
    }

	public BusStop getBusStop(String s){
		return busstops.get(s);
	}
	public void addBusStop(String s, BusStop b){
		busstops.put(s, b);
		dimensions.put(b, b.getDim());
	}
	
	public void addBus(BusStop bs, Bus b){
		busses.put(bs, b);
		if (routes.containsKey(b)){
			routes.get(b).add(bs);
		}
		else {
			ArrayList<BusStop> m = new ArrayList<BusStop>();
			m.add(bs);
			routes.put(b, m);
		}
	}

	public int getNumBusStops(){
		return busstops.size();
	}
	
	public Dimension getDestination(String dest){
		return simMap.get(dest);
	}
	
}
