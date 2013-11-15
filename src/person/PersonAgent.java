package person;

import agent.Role;
import agent.Agent;

import java.lang.Math;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Semaphore;

import person.Location.LocationType;
import person.interfaces.Person;

public class PersonAgent extends Agent implements Person{
	
	String name;
	int hunger; // tracks hunger level
	boolean activeRole;

	//PersonGui gui;
	List<Role> roles = new ArrayList<Role>();

	int accountNumber; 
	int wallet; // cash on hand

	int currentTime; 
	Location currentLocation; 

	Map<Integer, Event> schedule = new HashMap<Integer, Event>(); 
	Map<Integer, Location> cityMap = new HashMap<Integer, Location>();
	PriorityQueue<Event> toDo = new PriorityQueue<Event>();

	/*Home home; // home/apartment
	Car car; // car if the person has a car*/ //Who is in charge of these classes?

	Semaphore going;
	
	/* Utilities */
	
	private void activateRole(Role r){ r.setActive(true); }
	private void deactivateRole(Role r){ r.setActive(false); }
	public void addRole(Role r){ roles.add(r); }
	
	/* Messages */
	
	public void msgNewHour(int hour){ //from the world timer or gui 

	    currentTime = hour;
	    stateChanged();
	}

	public void msgAtDest(Location destination){ // From the gui. now we can send the correct entrance message to the location manager

	    going.release();
	    currentLocation = destination;
	    stateChanged();

	}

	public void msgFinishedEvent(Role r){ //The location manager will send this message as the persons role leaves the building

	    deactivateRole(r);
	    activeRole = false;
	    stateChanged();

	}
	/* Scheduler */
	
	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}
	
	/* Actions */
	
	
	
	/* 
	 * the cityMap will be the person's guide to locations in the city 
	 * holds all methods relevant to choosing destinations by a few criteria
	*/
	class cityMap {
		
		List<Location> map;
		
		cityMap(List<Location> locations){ map = locations; }
		
		double distanceTo(Location yourLoc, Location destination){
			
			double distance = Math.sqrt( (Math.pow((destination.getX() - yourLoc.getX()), 2) + 
							  Math.pow((destination.getY() - yourLoc.getY()), 2)));
			return distance;
			
		}
		
		Location chooseByName(String name){ //sync? i dont think anyone will mess with this list after init
			
			Location choice = new Location();
			
			for(Location l : map){
				
				if(l.getName().equalsIgnoreCase(name)){
					choice = l;
				}
			}
			return choice;
		}
		
		Location chooseRandom(LocationType type) {
			
			Random chooser = new Random();
			int i = chooser.nextInt(map.size()); //number of restaurants
			return map.get(i);
		}
		
		Location chooseByLocation(Location yourLoc, int searchRadius, LocationType type){
			Random chooser = new Random();
			int i = chooser.nextInt(5); //number of restaurants
			
			List<Location> locationsNearMe = new ArrayList<Location>();
			for(Location l : map){
				
				if(distanceTo(yourLoc, l) <= searchRadius && l.getType() == type){
					locationsNearMe.add(l);
				}
			}
			//choose randomly from a list of nearby locations 
			return locationsNearMe.get(i);
		}
		
		Location chooseByType(LocationType type){
			
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
		 
	}
	
}
