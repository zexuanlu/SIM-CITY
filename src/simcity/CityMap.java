package simcity;
import java.util.*;
import java.awt.Dimension;

import person.Location;
import person.Position;
import person.Location.LocationType;
import person.PersonAgent.CityMap.DistCompare;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 


public class CityMap {
//	Map<String,Dimension> simMap = new HashMap<String,Dimension>();
	
	//bus information

	Map<Bus, ArrayList<BusStop>> routes = new HashMap<Bus, ArrayList<BusStop>>();
	public Map<String,BusStop> busstops = new HashMap<String,BusStop>();  //map of name of stop to 
	public Map<BusStop,Dimension> dimensions = new HashMap<BusStop,Dimension>();
	public Map<BusStop, Bus> busses = new HashMap<BusStop, Bus>();
	public List<Location> map;
	public DistCompare comparator = new DistCompare();
	public PriorityQueue distancePriority = new PriorityQueue<Double>(10, comparator);

	public CityMap(List<Location> locations){
		map = locations; 
	}
	
	public BusRoute generateBusInformation(int finalx, int finaly, int originx, int originy){
		BusRoute b = new BusRoute();
		BusStop destStop = getClosestStop(finalx, finaly);
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

	public Location getByType(LocationType lt){

		Location destination = new Location();
		for(Location l : map){
			if(l.type == lt){
				destination = l;
			}
		}
		return destination;
	}
	public Position findNearestBusStop(Position p){
		PriorityQueue<Position> nearest = new PriorityQueue<Position>();
		List<Location> busStops = getListOfType(LocationType.BusStop);
		for(Location l : busStops){
			nearest.offer(l.position);
		}
		return nearest.peek();
	}
	public List<Location> getListOfType(LocationType type){
		List<Location> locations = new ArrayList<Location>();
		for(Location l : map){
			if(l.type == type){
				locations.add(l);
			}
		}
		return locations;
	}
	public double distanceTo(int x, int y, Location destination){
		double distance = Math.sqrt( (Math.pow((destination.getPosition().getX() - x), 2) + 
				Math.pow((destination.getPosition().getY() - y), 2)));
		return distance;
	}

	public Location chooseByName(String name){ //sync? i dont think anyone will mess with this list after init
		Location choice = null;
		for(Location l : map){
			if(l.getName().equalsIgnoreCase(name)){
				choice = l;
			}
		}
		return choice;
	}
	public Location chooseRandom(LocationType type) {
		Random chooser = new Random();
		int i = chooser.nextInt(map.size()); //number of restaurants
		return map.get(i);
	}
	public Location chooseByLocation(int yourX, int yourY, int searchRadius, LocationType type){
		Map<Double, Location> locationsNearMe = new HashMap<Double, Location>();
		for(Location l : map){
			double d = distanceTo(yourX, yourY, l);
			if( d <= (double)searchRadius && l.getType() == type){
				distancePriority.offer(d);
				locationsNearMe.put(d, l);		
			}
		}
		double nearest = (double) distancePriority.peek();
		return locationsNearMe.get(nearest);
	}
	public Location chooseByType(LocationType type){

		List<Location> types = new ArrayList<Location>();
		for(Location l : map){

			if(l.getType() == type){
				types.add(l);
			}
		}
		Random chooser = new Random();
		int i = chooser.nextInt(types.size()); //number of restaurants

		//choose randomly from a list of nearby locations 
		return types.get(i);
	}
	class DistCompare implements Comparator<Double> {

		@Override
		public int compare(Double o1, Double o2) {
			// TODO Auto-generated method stub
			if(o1 < o2){
				return -1;
			}
			if(o1 > o2){
				return 1;
			}
			return 0;
		} 
	}

}
