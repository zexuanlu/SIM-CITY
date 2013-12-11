package simcity;
import java.util.*;
import java.awt.Dimension;

import person.Bank;
import person.Location;
import person.PersonAgent.HomeType;
import person.Position;
import person.Location.LocationType;
import person.Home;
import person.Apartment;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 


public class CityMap {
	//	Map<String,Dimension> simMap = new HashMap<String,Dimension>();
	List<Integer>push = new ArrayList<Integer>();

	//bus information
	int WIDTHTOTAL = 740; 
	int HEIGHTTOTAL = 480; 
	int Street1 = 220; 
	int Street2 =380; 

	Map<Bus, ArrayList<BusStop>> routes = new HashMap<Bus, ArrayList<BusStop>>();
	public Map<String,BusStop> busstops = new HashMap<String,BusStop>();  //map of name of stop to 
	public Map<BusStop,Dimension> dimensions = new HashMap<BusStop,Dimension>();
	public Map<BusStop, Bus> busses = new HashMap<BusStop, Bus>();
	public List<Location> map;
	public DistCompare comparator = new DistCompare();
	public PriorityQueue distancePriority = new PriorityQueue<Double>(10, comparator);
	public List<Location> history = new ArrayList<Location>(); //keep track of previous restaurants you've been to
	public boolean ateOutLast;

	public CityMap(List<Location> locations){
		map = locations;
		ateOutLast = false;

		push.add(-20);
		push.add(0);
		push.add(20);


	}

	public CityMap(){

	}
	public BusRoute generateBusInformation(int finalx, int finaly, int originx, int originy){

		BusRoute b = new BusRoute();
		BusStop destStop = getClosestStop(finalx, finaly);
		b.destination = getStopName(destStop);
		b.destinationX = dimensions.get(destStop).width;
		b.destinationY = dimensions.get(destStop).height; 
		b.bus = busses.get(destStop);

		b.busstop = getClosestStopinRoute(originx, originy, b.bus);
		b.busStopX = getDimension(b.busstop).width;
		b.busStopY = getDimension(b.busstop).height;

		System.out.println("busstop start is" + b.busstop.getName() +"busstop end is "+ destStop.getName() );

		return b;
	}

	public Position getNearestStreet(int x, int y){
		int buffer = push.get(0);
		push.remove(0);
		push.add(buffer);



		//on horizontal road
		int tempX = 0; 
		int tempY = 0; 

		//split into quadrants 
		if (x <= WIDTHTOTAL/2){ //leftside
			if (y<=HEIGHTTOTAL/2){//topleft
				tempX = Math.abs(x - 340);
				tempY = Math.abs(y - 180);
				if (tempX < tempY){ //on vertical road
					if (y>20){
						return new Position(340,y+buffer);
					}
					return new Position(340, y);
				}
				else { //on horizontal road
					if (x>20){
						return new Position(x+buffer, 180);
					}
					return new Position(x, 180);
				}
			}
			else { //bottomleft
				tempX = Math.abs(x - 340);
				tempY = Math.abs(y - 280);
				if (tempX < tempY){ //on vertical road
					if (y<440){
						return new Position(340,y+buffer);
					}
					return new Position(340, y);
				}
				else { //on horizontal road
					if (x>20){
						return new Position(x+buffer,260);
					}
					return new Position(x, 260);
				}

			}
		}
		else {//rightside
			if (y<=HEIGHTTOTAL/2){//topright
				tempX = Math.abs(x - 420);
				tempY = Math.abs(y - 180);
				if (tempX < tempY){ //on vertical road
					return new Position(420, y);
				}
				else { //on horizontal road
					return new Position(x, 180);
				}
			}
			else { //bottomright
				tempX = Math.abs(x - 440);
				tempY = Math.abs(y - 280);
				if (tempX < tempY){ //on vertical road
					return new Position(420, y);
				}
				else { //on horizontal road
					return new Position(x, 260);
				}
			}
		}



		//		
		//		if (x < WIDTHTOTAL/2){
		//			return(new Position(x,Street1));
		//		}
		//		else {
		//			return (new Position(Street2,y));
		//		}
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

	private BusStop getClosestStopinRoute(int startx, int starty, Bus b){
		ArrayList<BusStop> broute = routes.get(b);
		BusStop closest = null; 
		int tempdiff = 0; 
		int numdiff = 10000; 
		for (BusStop br: broute){
			Dimension d = dimensions.get(br);
			tempdiff = 0;
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
	public Location getHome(int homeNumber){
		Location ll = null;
		if(homeNumber <= 5){
			for(Location l : map){
				if(l.type == LocationType.Home && ((Home) l).getNumber() == homeNumber){
					ll = l;
				}
			}
		}
		else {
			for(Location l : map){
				if(l.type == LocationType.Apartment && ((Apartment) l).getNumber() == homeNumber){
					ll = l;
				}
			}
		}
		return ll;
	}
	public Location getRestaurant(int choice){
		Location l = null;
		switch(choice) {
		case 1:
			l = get(LocationType.Restaurant1);
			break;
		case 2: 
			l = get(LocationType.Restaurant2);
			break;
		case 3:
			l = get(LocationType.Restaurant3);
			break;
		case 4:
			l = get(LocationType.Restaurant4);
			break;
		case 5: 
			l = get(LocationType.Restaurant5);
			break;
		case 6:
			l = get(LocationType.Restaurant6);
			break;
		}
		return l;
	}
	public Location eatOutOrIn(){

		Random chooser = new Random();
		int i = chooser.nextInt(6);
		Location l  = getRestaurant(i);
		if(history.size() == 6){
			history.clear();
		}
		while(l != null && l.isClosed()){ 
			i = chooser.nextInt(6);
			l  = getRestaurant(i);
		}
		if(!history.contains(l)){
			history.add(l);
		}
		ateOutLast = true;

		return l;
	}
	public Location get(LocationType lt){
		Location ll = null;
		for(Location l : map){
			if(l.type == lt){
				ll = l;
			}
		}
		return ll;
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

	public Bank pickABank(int x, int y){
		Bank temp = ((Bank)chooseByName("Banco Popular"));
		Bank temp2 = ((Bank)chooseByName("Banco Popular 2"));
		if(temp.isClosed() && temp2.isClosed())
			return null;
		else if(temp2.isClosed())
			return temp;
		else if(temp.isClosed())
			return temp2;
		else if(distanceTo(x,y,temp) > distanceTo(x,y,temp2))
			return temp2;
		else 
			return temp;

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
		int i = chooser.nextInt(map.size());

		Location l = map.get(i);
		if(l.type == type){
			return l;
		}
		else {
			l = chooseRandom(type);
			return l;
		}
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
