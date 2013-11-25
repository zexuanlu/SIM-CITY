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
import bank.test.mock.MockBankHost;
import bank.*;
import market.*;
import market.interfaces.*;
import agent.*;
import person.gui.PersonGui;
import person.interfaces.Person;
import person.test.mock.EventLog;
import simcity.PassengerRole;
import simcity.CityMap;
/*
 * The PersonAgent controls the sim character. In particular his navigation, decision making and scheduling
 * The PersonAgent, once a decision has been made, will switch to the appropriate role to carry out the task given in the event
 * 
 * @author Grant Collins
 */
public class PersonAgent extends Agent implements Person{
	EventLog log = new EventLog();
	public boolean testMode = false; //enabled for tests to skip semaphores 

	private String name;
	public int hunger; // tracks hunger level
	public boolean activeRole;

	PersonGui gui = new PersonGui(this);
	public List<MyRole> roles = new ArrayList<MyRole>();

	int accountNumber; 
	public Wallet wallet;
	int currentTime; 
	public Position currentLocation = new Position(0, 0); 
	public Position dest = new Position(50, 50);
	Map<Integer, SimEvent> schedule = new HashMap<Integer, SimEvent>(); 
	public CityMap cityMap;

	@SuppressWarnings("unchecked")
	Comparator<SimEvent> comparator = new EventComparator();
	public PriorityQueue<SimEvent> toDo = new PriorityQueue<SimEvent>(10, comparator);

	public Map<String, Integer> shoppingList = new HashMap<String, Integer>();// for home role shopping ish
	public List<Food> shoppingBag = new ArrayList<Food>();

	/* Home home; // home/apartment
	Car car; // car if the person has a car */ //Who is in charge of these classes?

	Semaphore going = new Semaphore(0, true);
	Semaphore transport = new Semaphore(0, true);
	Semaphore wait = new Semaphore(0, true);
	Semaphore driving = new Semaphore(0, true);

	public PersonAgent (String name, CityMap cm){
		super();
		this.name = name;
		this.cityMap = cm;
		this.wallet = new Wallet(5000, 5000);//hacked in
		this.hunger = 4;
		currentTime = 7;
	}
	
	public PersonAgent () {
		super();
	}
	public PersonAgent (String name){
		super();
		this.name = name;
		activeRole = false;
		this.wallet = new Wallet(5000, 5000);//hacked in
		this.hunger = 4;
	}
	
	/* Utilities */
	public void setName(String name){this.name = name;}

	public String getName(){ return this.name; }

	public boolean active() {return this.activeRole; }

	public boolean containsRole(Role r){ 
		for(MyRole role : roles){
			if(role.role.getClass() == r.getClass()){
				return true;
			}
		}
		return false;
	}
	public MyRole getRoleOfType(Role r){
		for(MyRole role : roles){
			if(role.role.getClass() == r.getClass()){
				return role;
			}
		}
		return null;
	}
	public boolean containsEvent(String directive){
		for(SimEvent e : toDo){
			if(e.directive == directive){
				return true;
			}
		}
		return false;
	}
	public int getTime(){ return currentTime; }

	public void setTime(int time){ currentTime = time; }

	public void setMap(List<Location> locations){ cityMap = new CityMap(locations); }

	public void addRole(MyRole r){ roles.add(r); }

	public void populateCityMap(List<Location> loc){ cityMap = new CityMap(loc); } 

	/* Messages */
	public void msgAddMoney(double money){ 
		print("added an amount of "+money+" dollars");
		wallet.setOnHand(money); 
		stateChanged();
	}
	public void msgNewBalance(double money){
		wallet.setNewBankBalance(money);
	}
	public double msgCheckWallet(){
		return wallet.getOnHand();
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
	public void msgAtDest(int x, int y){
		print("Recieved the message AtDest(x,y)");
		//driving.release();
		currentLocation.setX(x);
		currentLocation.setY(y);
		activeRole = false;
		stateChanged();
	}
	public void msgAtDest(Position destination){ // From the gui. now we can send the correct entrance message to the location manager
		print("Recieved the message AtDest");
		going.release();
		currentLocation = destination;
		stateChanged();
	}
	public void msgFinishedEvent(Role r, List<Food> foodList, double change){
		print("Recieved this message");
		for(MyRole role : roles){
			if(role.role == r ){
				role.isActive(false);
			}
		}
		activeRole = false;
		for(Food f : foodList){
			shoppingBag.add(f);
		}
		wallet.setOnHand(change);
		stateChanged();
	}
	public void msgFinishedEvent(Role r){ //The location manager will send this message as the persons role leaves the building
		print("Recieved msgFinishedEvent");
		for(MyRole role : roles){
			if(role.role == r ){
				role.isActive(false);
			}
		}
		activeRole = false;
		stateChanged();
	}
	public void msgReadyToWork(Role r){
		wait.release();
		print("Recieved msgReadyToWork");
		for(MyRole role : roles){
			if(role.role == r ){
				role.isActive(true);
			}
		}
		stateChanged();
	}
	public void msgGoOffWork(Role r , double pay){ 
		print("Recieved the message go off work");
		wallet.setOnHand(pay);
		for(MyRole role : roles){
			if(role.role == r ){
				role.isActive(false);
			}
		}
		activeRole = false;
		stateChanged();
	}

	/* Scheduler */

	@Override
	public boolean pickAndExecuteAnAction() {
		if(activeRole) { //run role code
			print("Executing an event as a Role");
			for(MyRole r : roles){
				if(r.isActive){
					print("Executing rule in role");
					return r.role.pickAndExecuteAnAction();
				}
			}
		}
		else {
			SimEvent nextEvent = toDo.peek(); //get the highest priority element (w/o deleting)
			if(nextEvent != null && nextEvent.start == currentTime) {
				print("Executing an event as a Person");
				goToAndDoEvent(nextEvent); 
				//toDo.remove(nextEvent);
				return true;
			}
			else{ //check vitals and find something to do on the fly
				print("nextEvent was either null or not starting right now: "+nextEvent);
				checkVitals();
				return true;
			}
		}
		return false;
	}

	/* Actions */

	private void goToAndDoEvent(SimEvent e){
		print("going");
		if(!isInWalkingDistance(e.location)){ //if its not in walking distance we ride the bus
			//make  a PassengerRole and start it
			activeRole = true;
			PassengerRole pRole = new PassengerRole(this.name, this);
			if(!containsRole(pRole)){ //if we dont already have a PassengerRole make one
				MyRole newRole = new MyRole(pRole);
				newRole.isActive(true);
				roles.add(newRole);
				((PassengerRole)newRole.role).setDestination(e.location.name);
				((PassengerRole)newRole.role).gotoBus();
			}
			else{ //if we already have a PassengerRole, use it
				((PassengerRole)getRoleOfType(pRole).role).setDestination(e.location.name);
				((PassengerRole)getRoleOfType(pRole).role).gotoBus();
				getRoleOfType(pRole).isActive(true);
			}
		}
		else{
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
				if(!containsRole(cRole)){
					roles.add(cRole);
				}
				rest.getHost().msgIWantFood(this, cRole);
				cRole.setActive(true);
			}
			else if(e.type == EventType.HostEvent){
				print("Role");
				activeRole = true;
				HostRole hostRole = new HostRole(this.name, this); 

				if(!containsRole(hostRole)){                                                       

					roles.add(hostRole);
				}
				rest.getHost().msgClockIn(this, hostRole);
				hostRole.setActive(true);
			}
			else if(e.type == EventType.WaiterEvent){}
			else if(e.type == EventType.CookEvent){}
			else if(e.type == EventType.CashierEvent){}
		}*/
			if(e.location.type == LocationType.Bank){ //if our event happens at a bank
				Bank bank = (Bank)e.location;
				if(e.type == EventType.CustomerEvent){ //if our intent is to act as a customer
					activeRole = true; 
					BankCustomerRole bcr = new BankCustomerRole(this, this.name); //create the role
					if(!containsRole(bcr)){   //check if we dont already have it 
						MyRole newRole = new MyRole(bcr); //make a new MyRole
						newRole.isActive(true); //set it active
						roles.add(newRole); 
						bcr.msgGoToBank(e.directive, 10); //message it with what we want to do
					}
					else { //we already have one use it
						((BankCustomerRole)getRoleOfType(bcr).role).msgGoToBank(e.directive, 10);
						getRoleOfType(bcr).isActive(true);
					}
					toDo.remove(e); //remove the event from the queue
				}
				else if(e.type == EventType.TellerEvent){
					activeRole = true;
					BankTellerRole btr = new BankTellerRole(this, this.name);; 
					if(!containsRole(btr)){ 
						print("Teller not found");
						MyRole newRole = new MyRole(btr);
						bank.getTimeCard().msgBackToWork(this, (BankTellerRole)newRole.role);
						newRole.isActive(true);
						roles.add(newRole);

					}
					else { 
						bank.getTimeCard().msgBackToWork(this, (BankTellerRole)getRoleOfType(btr).role); 
						getRoleOfType(btr).isActive(true);
					}
					if(!testMode){
						try {
							print("acquiring wait");
							wait.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					print("releasing wait");
					toDo.remove(e);
				}
				else if(e.type == EventType.HostEvent){
					activeRole = true;
					BankHostRole bhr = new BankHostRole(this, this.name);

					if(!containsRole(bhr)){ 
						print("Host not found");
						MyRole newRole = new MyRole(bhr);
						newRole.isActive(true);
						bank.getTimeCard().msgBackToWork(this, (BankHostRole)newRole.role);
						roles.add(newRole);
					}
					else { 
						bank.getTimeCard().msgBackToWork(this, (BankHostRole)getRoleOfType(bhr).role); 
						getRoleOfType(bhr).isActive(true);
					}
					if(!testMode){
						try {
							wait.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					toDo.remove(e);
				}
			}
			else if(e.location.type == LocationType.Market){
				Market market = (Market)e.location;
				if(e.type == EventType.CustomerEvent){
					activeRole = true;
					MarketCustomerRole mcr = new MarketCustomerRole(this, this.name);

					if(!containsRole(mcr)){
						print("Market customer not found");
						MyRole newRole = new MyRole(mcr);
						newRole.isActive(true);
						roles.add(newRole);
						((MarketCustomerRole)newRole.role).msgHello();
					}
					else{ 
						((MarketCustomerRole)getRoleOfType(mcr).role).msgHello(); 
						getRoleOfType(mcr).isActive(true);
					}
					toDo.remove(e);
				}
				else if(e.type == EventType.EmployeeEvent){
					activeRole = true;
					MarketEmployeeRole mer = new MarketEmployeeRole(this, this.name);

					if(!containsRole(mer)){
						MyRole newRole = new MyRole(mer);
						newRole.isActive(true);
						roles.add(newRole);
						market.getTimeCard().msgBackToWork(this,(MarketEmployeeRole)getRoleOfType(mer).role );
					}
					else {
						market.getTimeCard().msgBackToWork(this,(MarketEmployeeRole)getRoleOfType(mer).role);
						getRoleOfType(mer).isActive(true);
					}
					if(!testMode){
						try {
							wait.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					toDo.remove(e);
				}
				else if(e.type == EventType.CashierEvent){
					activeRole = true;
					MarketCashierRole mcash = new MarketCashierRole(this, this.name);

					if(!containsRole(mcash)){
						MyRole newRole = new MyRole(mcash);
						newRole.isActive(true);
						roles.add(newRole);
						market.getTimeCard().msgBackToWork(this,(MarketCashierRole)getRoleOfType(mcash).role );
					}
					else{
						market.getTimeCard().msgBackToWork(this,(MarketCashierRole)getRoleOfType(mcash).role);
						getRoleOfType(mcash).isActive(true);
					}
					if(!testMode){
						try {
							wait.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					toDo.remove(e);
				}
			}
		}
	}
	/*else if(e.location.type == LocationType.Home){
			if(e.type == EventType.HomeOwnerEvent){
				Home home = (Home)e.location;
				activeRole = true;
				HomeOwnerRole hr = new HomeOwnerRole(this.name, this);
				if(!containsRole(hr)){
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
				if(!containsRole(mtr)){
					roles.add(mtr);
				}
				mtr.setActive(true);

			}
			else if(e.type == EventType.AptTenantEvent){
				Apartment apt = (Apartment)e.location;
				activeRole = true;
				ApartmentTenantRole ten = new AprtmentTenantRole(this.name, this);
				if(!containsRole(ten)){
					roles.add(ten);
				}
				ten.msgUpdateVitals(hunger, currentTime);
				ten.setActive(true);
			}
			else if(e.type == EventType.LandlordEvent){
				Apartment apt = (Apartment)e.location;
				activeRole = true;
				ApartmentLandlordRole llr = new ApartmentLandlordRole(this.name, this);
				if(!containsRole(llr)){
					roles.add(llr);
				}
				llr.setActive(true);
			}
		}*/

	//}
	
	/* checkVitals is a method to figure out misc things to do on the fly*/
	private void checkVitals() { 
		/*NOTE: the locations in this method are hard coded until we get the init script that 
		 * puts together the people's cityMap objects which then will allow the people to 
		 * find locations on the fly via look up 
		 */
		if(wallet.getOnHand() <= 100){ //get cash
			SimEvent needMoney = new SimEvent("Need Money", new Bank("Banco Popular", new TimeCard(), new MockBankHost("Gil"), 
					new Position(100, 50), LocationType.Bank), 4, EventType.CustomerEvent);
			if(!containsEvent("Need Money")){ 
				toDo.offer(needMoney);
				wallet.addTransaction("Witdrawl", 100);
			}
		}
		if(wallet.getOnHand() >= 500){ //deposit cash
			SimEvent needDeposit = new SimEvent("Need Deposit", new Bank("Banco Popular", new TimeCard(), new MockBankHost("Gil"), 
					new Position(100, 50), LocationType.Bank), 4, EventType.CustomerEvent);
			if(!containsEvent("Need Deposit")){
				toDo.offer(needDeposit);
				wallet.addTransaction("Deposit", 200);
			}
		}
		/*if(hunger >= 3){ //go eat
			Location restaurantChoice = cityMap.chooseRandom(LocationType.Restaurant);
			SimEvent needFood = new SimEvent("Go eat", (Restaurant)restaurantChoice, 4, EventType.CustomerEvent);
			if(!containsEvent("Go eat")){
				toDo.add(needFood);
			}
		}*/
		//buy a car
		//rob the bank
		//etc
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
	public class MyRole{
		public Role role;
		public boolean isActive;

		MyRole(Role r){
			role = r;
			isActive = false;
		}
		void isActive(boolean tf){ isActive = tf; }
	}
	/* 
	 * the cityMap will be the person's guide to locations in the city 
	 * holds all methods relevant to choosing destinations by a few criteria
	 */
	/*public class CityMap {

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
		public class DistCompare implements Comparator<Double> {

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
	}*/
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

		private double onHand;
		private double inBank;
		private double balance; 
		private List<BankTicket> transactions 
		= new ArrayList<BankTicket>();
		Wallet(double oh, double ib){
			this.onHand = oh;
			this.inBank = ib;
			this.balance = oh + ib;
		}
		public void addTransaction(String action, double amount){
			BankTicket bt = new BankTicket(action, amount);
			transactions.add(bt);
		}
		public double getTransaction(String action){
			for(BankTicket bt : transactions){
				if(bt.action == action){
					return bt.amount;
				}
			}
			return 0;
		}
		public double getOnHand(){
			return onHand;
		}
		public double getInBank(){
			return inBank;
		}
		public double getBalance(){
			return balance;
		}
		public void setOnHand(double money){
			onHand += money;
		}
		public void setInBank(double newAmount){
			inBank += newAmount;
		}
		public void setNewBankBalance(double newAmount){
			inBank = newAmount;
		}
		class BankTicket {
			String action;
			double amount;

			BankTicket(String action, double amount){
				this.action = action;
				this.amount = amount;
			}
			public String getAction(){ return this.action; }
			public double getAmount(){ return this.amount; }
		}
	}
}
