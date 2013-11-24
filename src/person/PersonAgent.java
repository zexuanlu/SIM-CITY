package person;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.Semaphore;

import person.Location.LocationType;
import person.Restaurant;
import person.SimEvent.EventType;
import bank.interfaces.*;
import bank.*;
import market.*;
import market.interfaces.*;
import agent.*;
import person.gui.PersonGui;
import person.interfaces.Person;
/*
 * The PersonAgent controls the sim character. In particular his navigation, decision making and scheduling
 * The PersonAgent, once a decision has been made, will switch to the appropriate role to carry out the task given in the event
 * 
 * @author Grant Collins
 */
public class PersonAgent extends Agent implements Person{

	private String name;
	public int hunger; // tracks hunger level
	public boolean activeRole;

	PersonGui gui = new PersonGui(this);
	public List<Role> roles = new ArrayList<Role>();

	int accountNumber; 
	public Wallet wallet;
	int currentTime; 
	public Position currentLocation; 
	public Position dest = new Position(50, 50);
	Map<Integer, SimEvent> schedule = new HashMap<Integer, SimEvent>(); 
	public CityMap cityMap;

	@SuppressWarnings("unchecked")
	Comparator<SimEvent> comparator = new EventComparator();
	public PriorityQueue<SimEvent> toDo = new PriorityQueue<SimEvent>(10, comparator);

	public Map<String, Integer> shoppingList = new HashMap<String, Integer>();// for home role shopping ish

	/* Home home; // home/apartment
	Car car; // car if the person has a car */ //Who is in charge of these classes?

	//ApartmentLandlord :  
	//ApartmentTenant : msgGotHungry(), msgPayRent(), 
	//HomeOwner  : msgGotHungry(), msgMaintainHome();
	//MaintenancePerson : 

	Semaphore going = new Semaphore(0, true);
	Semaphore transport = new Semaphore(0, true);
	Semaphore wait = new Semaphore(0, true);
	/*HostRole hr = new HostRole(this.getName(), this);
	Restaurant restaurant= new Restaurant("Work", hr, dest, LocationType.Restaurant);
	Event e = new Event(restaurant, currentTime, currentTime, EventType.HostEvent);*/

	public PersonAgent (String name, List<Location> l){
		super();
		this.name = name;
		this.cityMap = new CityMap(l);
		this.wallet = new Wallet(5000, 5000);//hacked in
		this.hunger = 4;
		currentTime = 7;
		Location location = new Location("Work", LocationType.Restaurant, new Position(100, 100));
		SimEvent e = new SimEvent(location, currentTime, currentTime, EventType.HostEvent);
		//toDo.offer(e);
	}
	public PersonAgent () {
		super();
	}
	public PersonAgent (String name){
		super();
		this.name = name;
		//this.name = name;
		//this.cityMap = new CityMap(l);
		activeRole = false;
		this.wallet = new Wallet(5000, 5000);//hacked in
		this.hunger = 4;
		currentTime = 7;
		//toDo.offer(e);
	}
	/* Utilities */
	public void setName(String name){this.name = name;}

	public String getName(){ return this.name; }

	public boolean active() {return this.activeRole; }

	public int getTime(){ return currentTime; }

	public void setMap(List<Location> locations){ cityMap = new CityMap(locations); }

	private void activateRole(Role r){ r.setActive(true); }

	public void deactivateRole(Role r){ r.setActive(false); }

	public void addRole(Role r){ roles.add(r); }

	public void populateCityMap(List<Location> loc){ cityMap = new CityMap(loc); } 

	/* Messages */
	public void msgAddMoney(int money){ 
		print("added an amount of "+money+" dollars");
		wallet.setOnHand(money); 
		stateChanged();
	}
	public void msgAtHome(){
		print("Back home");
	}
	public void msgAddEvent(SimEvent e){
		toDo.offer(e);
		stateChanged();
	}
	public void msgNewHour(int hour){ //from the world timer or gui 
		currentTime = hour;
		stateChanged();
	}

	public void msgAtDest(Position destination){ // From the gui. now we can send the correct entrance message to the location manager
		print("Recieved the message AtDest");
		going.release();
		currentLocation = destination;
		stateChanged();
	}
	public void msgFinishedEvent(Role r){ //The location manager will send this message as the persons role leaves the building
		deactivateRole(r);
		activeRole = false;
		stateChanged();
	}
	public void msgReadyToWork(Role r){
		print("Recieved msgReadyToWork");
		for(Role role : roles){
			if(role == r ){
				role.setActive(true);
			}
		}
		wait.release();
		stateChanged();
	}
	public void msgGoOffWork(Role r /*, int pay*/){ 
		//wallet.setOnHand(pay);
		deactivateRole(r);
		activeRole = false;
		stateChanged();
	}

	/* Scheduler */

	@Override
	public boolean pickAndExecuteAnAction() {
		if(activeRole) {
			print("Executing an event as a Role");
			for(Role r : roles){
				if(r.isActive() ){
					return r.pickAndExecuteAnAction();
				}
			}
		}
		else {
			SimEvent nextEvent = toDo.peek(); //get the highest priority element (w/o deleting)
			if(nextEvent != null) {
				print("Executing an event as a Person");
				nextEvent.inProgress = true; //using in progress to keep these events in the pq like active not active 
				goToAndDoEvent(nextEvent); 
				toDo.remove(nextEvent);
				return true;
			}
		}
		return false;
	}

	/* Actions */

	private void goToAndDoEvent(SimEvent e){
		print("going");
		/*if(!isInWalkingDistance(e.location)){
			activeRole = true;
			PassengerRole pRole = new PassengerRole(this.name, this);
			if(!roles.contains(pRole)){
				roles.add(pRole);
			}
			pRole.setActive(true);
			pRole.msgDestination(e.location.name);
			pRole.msgGo();
		}*/
		//else{
		//Location l = e.location;
		/*DoGoTo(l); // Handles picking mode of transport and animating there

		try {
			print("Acquired");
			going.acquire();
			print("Released");
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		/*if(e.location.type == LocationType.Restaurant){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				activeRole = true;
				CustomerRole cRole = new CustomerRole(this.name, this);
				if(!roles.contains(cRole)){
					roles.add(cRole);
				}
				rest.getHost().msgIWantFood(this, cRole);
				cRole.setActive(true);
			}
			else if(e.type == EventType.HostEvent){
				print("Role");
				activeRole = true;
				HostRole hostRole = new HostRole(this.name, this); 

				if(!roles.contains(hostRole)){                                                       

					roles.add(hostRole);
				}
				rest.getHost().msgClockIn(this, hostRole);
				hostRole.setActive(true);
			}
			else if(e.type == EventType.WaiterEvent){}
			else if(e.type == EventType.CookEvent){}
			else if(e.type == EventType.CashierEvent){}
		}*/
		if(e.location.type == LocationType.Bank){
			Bank bank = (Bank)e.location;
			if(e.type == EventType.CustomerEvent){
				activeRole = true;
				BankCustomerRole bcr = new BankCustomerRole(this, this.name);

				if(!roles.contains(bcr)){                                                       
					roles.add(bcr);
				}
				print("bank host "+bank.getHost()); 
				bcr.msgGoToBank(e.directive, 10);
				bcr.setActive(true);
			}
			else if(e.type == EventType.TellerEvent){
				activeRole = true;
				BankTellerRole btr = new BankTellerRole(this, this.name);

				if(!roles.contains(btr)){                                                       
					roles.add(btr);
				}
				bank.getTimeCard().msgBackToWork(this, btr);
				try {
					wait.acquire();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				btr.setActive(true);			
			}
			else if(e.type == EventType.HostEvent){
				activeRole = true;
				BankHostRole bhr = new BankHostRole(this, this.name);

				if(!roles.contains(bhr)){                                                       
					roles.add(bhr);
				}
				bank.getTimeCard().msgBackToWork(this, bhr);
				bhr.setActive(true);
			}
		}
		else if(e.location.type == LocationType.Market){
			Market market = (Market)e.location;
			if(e.type == EventType.CustomerEvent){
				activeRole = true;
				MarketCustomerRole mcr = new MarketCustomerRole(this, this.name);

				if(!roles.contains(mcr)){
					roles.add(mcr);
				}
				mcr.msgHello();
				mcr.setActive(true);
			}
			else if(e.type == EventType.EmployeeEvent){
				activeRole = true;
				MarketEmployeeRole mer = new MarketEmployeeRole(this, this.name);

				if(!roles.contains(mer)){
					roles.add(mer);
				}
				market.getTimeCard().msgBackToWork(this, mer);
				mer.setActive(true);
			}
			else if(e.type == EventType.CashierEvent){
				activeRole = true;
				MarketCashierRole mcash = new MarketCashierRole(this, this.name);

				if(!roles.contains(mcash)){
					roles.add(mcash);
				}
				market.getTimeCard().msgBackToWork(this,mcash);
				mcash.setActive(true);
			}
		}
		/*else if(e.location.type == LocationType.Home){
			if(e.type == EventType.HomeOwnerEvent){
				Home home = (Home)e.location;
				activeRole = true;
				HomeOwnerRole hr = new HomeOwnerRole(this.name, this);
				if(!roles.contains(hr)){
					roles.add(hr);
				}
				//set time and hunger 
				hr.msgUpdateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level
				hr.setActive(true);			
			}
			else if(e.type == EventType.MaintenceRole){
				Home home = (Home)e.location;
				activeRole = true;
				MaintenenceRole mtr = new MaintenenaceRole(this.name, this);
				if(!roles.contains(mtr)){
					roles.add(mtr);
				}
				mtr.setActive(true);

			}
			else if(e.type == EventType.AptTenantEvent){
				Apartment apt = (Apartment)e.location;
				activeRole = true;
				ApartmentTenantRole ten = new AprtmentTenantRole(this.name, this);
				if(!roles.contains(ten)){
					roles.add(ten);
				}
				ten.msgUpdateVitals(hunger, currentTime);
				ten.setActive(true);
			}
			else if(e.type == EventType.LandlordEvent){
				Apartment apt = (Apartment)e.location;
				activeRole = true;
				ApartmentLandlordRole llr = new ApartmentLandlordRole(this.name, this);
				if(!roles.contains(llr)){
					roles.add(llr);
				}
				llr.setActive(true);
			}
		}*/

	}
	//we're in our free time so we pick what we need to do based on our non mandatory events (pQ)
	/*else {
			checkVitals();
			Event eventToExec = toDo.peek();
			//if(event)
			//createAndAddRole(eventToExec); // a stub for the procedure shown above of checking what type of event it is and creating a role for it
		}*/

	private void checkVitals() {

		if(wallet.getOnHand() <= 100) {
			SimEvent needMoney = new SimEvent(cityMap.getByType(LocationType.Bank), 4, currentTime + 30, currentTime + 60, EventType.CustomerEvent);
			if(!toDo.contains(needMoney)){ 
				toDo.offer(needMoney);
			}
		}
		if(hunger >= 4){
			Location restaurantChoice = cityMap.chooseRandom(LocationType.Restaurant);
			SimEvent needFood = new SimEvent((Restaurant)restaurantChoice, 4, currentTime + 30, currentTime + 60, EventType.CustomerEvent);
			if(! toDo.contains(needFood)){
				toDo.add(needFood);
			}
		}
		//will implement errands later after these are tested well
	}

	private void DoGoTo(Location loc){
		gui.DoGoTo();
	}
	private Position calculateTransportation(Location loc){
		//if the person has a car use it
		/*
		 * if(car){ return an event with the same location }
		 */
		if(cityMap.distanceTo(currentLocation.getX(), currentLocation.getY(), loc) > 10){
			return cityMap.findNearestBusStop(currentLocation);
		}
		return currentLocation; //hack life
	}
	private boolean isInWalkingDistance(Location loc){
		if(cityMap.distanceTo(currentLocation.getX(), currentLocation.getY(), loc) < 10){
			return true;
		}
		return false;
	}

	/* 
	 * the cityMap will be the person's guide to locations in the city 
	 * holds all methods relevant to choosing destinations by a few criteria
	 */
	public class CityMap {

		public List<Location> map;
		public DistCompare comparator = new DistCompare();
		public PriorityQueue distancePriority = new PriorityQueue<Double>(10, comparator);
		CityMap(List<Location> locations){ map = locations; }

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
	class EventComparator implements Comparator{

		//this method calculates the priority based on the priority string in event
		//and how far into the future it is.

		public int getComposite(SimEvent e){

			int time = Math.abs(e.start - currentTime); //how far into the future is this event
			int priority = e.priority; //how urgent is this?
			int score = time + priority;
			System.out.println("Score of: "+score);
			return score;
		}

		//given that there are never two events happening at the same time 
		//i think we can negate the equals case?

		@Override
		public int compare(Object o1, Object o2)
		{
			int e1Composite = getComposite((SimEvent)o1);
			int e2Composite = getComposite((SimEvent)o2);

			if (e1Composite > e2Composite)
			{
				return 1;
			}
			if (e1Composite < e2Composite)
			{
				return -1;
			}
			return 0;
		}
	}
	public class Wallet {
		
		private int onHand;
		private int inBank;
		private int balance; 

		Wallet(int oh, int ib){
			this.onHand = oh;
			this.inBank = ib;
			this.balance = oh + ib;
		}
		public int getOnHand(){
			return onHand;
		}
		public int getInBank(){
			return inBank;
		}
		public int getBalance(){
			return balance;
		}
		public void setOnHand(int newAmount){
			onHand += newAmount;
		}
		public void setInBank(int newAmount){
			inBank += newAmount;
		}
	}
}
