package person;

import gui.panels.CityAnimationPanel;

import gui.main.SimCityGUI;

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
import bank.gui.BankCustomerGui;
import bank.gui.BankHostGui;
import bank.gui.BankTellerGui;
import bank.interfaces.*;
import bank.test.mock.MockBankHost;
import bank.*;
import market.*;
import market.interfaces.*;
import agent.*;
import person.gui.PersonGui;
import person.interfaces.Person;
import person.test.mock.EventLog;
import resident.ApartmentLandlordRole;
import resident.ApartmentTenantRole;
import resident.HomeOwnerRole;
import resident.gui.HomeOwnerGui;
import simcity.CarAgent;
import simcity.gui.CarGui; 
import simcity.PassengerRole;
import simcity.CityMap;
import simcity.astar.AStarTraversal;
import simcity.gui.PassengerGui;
/*
 * The PersonAgent controls the sim character. In particular his navigation, decision making and scheduling
 * The PersonAgent, once a decision has been made, will switch to the appropriate role to carry out the task given in the event
 * 
 * @author Grant Collins
 */
public class PersonAgent extends Agent implements Person{
	private EventLog log = new EventLog();
	public boolean testMode = false; //enabled for tests to skip semaphores 

	private String name;
	public int hunger; // tracks hunger level
	public int homeNumber;
	public boolean activeRole;
	public boolean arrived;
	public AStarTraversal astar;

	public PersonGui gui;
	private CityAnimationPanel cap = new CityAnimationPanel();
	public List<MyRole> roles = new ArrayList<MyRole>();

	private int accountNumber; 
	public Wallet wallet;
	private int currentTime; 

	public Position currentLocation = new Position(0, 0); 
	public Position dest = new Position(50, 50);
	//private Map<Integer, SimEvent> schedule = new HashMap<Integer, SimEvent>(); 
	public CityMap cityMap;

	@SuppressWarnings("unchecked")
	private Comparator<SimEvent> comparator = new EventComparator();
	public PriorityQueue<SimEvent> toDo = new PriorityQueue<SimEvent>(10, comparator);

	public Map<String, Integer> shoppingList = new HashMap<String, Integer>();// for home role shopping ish
	public List<Food> shoppingBag = new ArrayList<Food>();
	
	public SimCityGUI simcitygui;
	
	CarAgent car; // car if the person has a car */ //Who is in charge of these classes?

	private Semaphore going = new Semaphore(0, true);
	//private Semaphore transport = new Semaphore(0, true);
	private Semaphore wait = new Semaphore(0, true);
	//private Semaphore driving = new Semaphore(0, true);

	public PersonAgent (String name, CityMap cm){
		super();
		this.name = name;
		this.cityMap = cm;
		this.wallet = new Wallet(5000, 5000);//hacked in
		this.hunger = 4;
		currentTime = 7;
		arrived = false;
	}
	public PersonAgent (String name, CityMap cm, AStarTraversal astar2){
		super();
		this.name = name;
		this.cityMap = cm;
		astar = astar2; 
		this.wallet = new Wallet(5000, 5000);//hacked in

		this.hunger = 4;
		currentTime = 7;
		this.astar = astar;
		arrived = false;
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

	public void setAnimationPanel(CityAnimationPanel c){ cap = c; }

	public void setHomeNumber(int hn){ homeNumber = hn; }

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

	public void addRole(Role r){
		MyRole mr = new MyRole(r);
		roles.add(mr);
	}
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
		//stateChanged();
	}
	public void msgNewHour(int hour){ //from the world timer or gui 
		currentTime = hour;
		stateChanged();
	}
	public void msgAtDest(int x, int y){
		gui.xPos = x;
		gui.yPos = y;
		currentLocation.setX(x);
		currentLocation.setY(y);
		for(MyRole role : roles){
			if(role.role instanceof PassengerRole){
				role.isActive(false);
			}
		}
		activeRole = false;
		gui.setPresent(true);
		arrived = true;
		stateChanged();
	}

	public void setStateChanged(){
		stateChanged();
	}

	public void msgAtDest(Position destination){ // From the gui. now we can send the correct entrance message to the location manager
		print("Recieved the message AtDest");
		gui.setPresent(true);
		currentLocation = destination;
		going.release();
		stateChanged();
	}
	
	public void msgAtDest(Position destination,CarAgent c){ // From the gui. now we can send the correct entrance message to the location manager
		print("Recieved the message AtDest from car");
		gui.setPresent(true);
		currentLocation = destination;
		dowalkto(destination.getX(),destination.getY());
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
		gui.setPresent(true);
		//arrived = false;
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
			for(MyRole r : roles){
				if(r.isActive){
					print("Executing rule in role "+ r.role);
					boolean b = r.role.pickAndExecuteAnAction();
					return b;
				}
			}
		}
		else {
			SimEvent nextEvent = toDo.peek(); //get the highest priority element (w/o deleting)
			if((nextEvent != null && nextEvent.start == currentTime) 
					|| nextEvent != null && nextEvent.inProgress){ //if we have an event and its time to start or were in the process ofgetting there
				print("Executing an event as a Person");
				goToAndDoEvent(nextEvent); 
				nextEvent.setProgress(true);
				return true;
			}
			else{ //check vitals and find something to do on the fly
				checkVitals();
				return true;
			}
		}
		return false;
	}

	/* Actions */

	private void goToAndDoEvent(SimEvent e){
		print("going");
		if(!isInWalkingDistance(e.location) && !arrived){ //if its not in walking distance we ride the bus
			//make a PassengerRole and start it
			activeRole = true;
			PassengerRole pRole = new PassengerRole(this.name, this);
			if(!containsRole(pRole)){ //if we dont already have a PassengerRole make one
				MyRole newRole = new MyRole(pRole);
				newRole.isActive(true);
				roles.add(newRole);
				PassengerGui pg = new PassengerGui(((PassengerRole)newRole.role), gui.getX(), gui.getY());
				((PassengerRole)newRole.role).setGui(pg);
				cap.addGui(pg);
				((PassengerRole)newRole.role).setCityMap(cityMap);
				((PassengerRole)newRole.role).setPassDestination(e.location.position.getX(), e.location.position.getY());

				//lizhi added this testing:
				gui.xDestination = e.location.position.getX();
				gui.yDestination = e.location.position.getY();

				((PassengerRole)newRole.role).gotoBus();
				gui.setPresent(false);
				arrived = false;
			}
			else{ //if we already have a PassengerRole, use it
				((PassengerRole)getRoleOfType(pRole).role).setDestination(e.location.name);
				((PassengerRole)getRoleOfType(pRole).role).gotoBus();
				getRoleOfType(pRole).isActive(true);
				gui.setPresent(false);
				arrived = false;
			}
		}
		else{
			print("going to location");
			Location l = e.location;
			DoGoTo(l); 
			if(!testMode){
				try {
					print("going acquire");
					going.acquire();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
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
					bcr.bh = bank.host;
					if(!containsRole(bcr)){   //check if we don't already have it 
						MyRole newRole = new MyRole(bcr); //make a new MyRole
						newRole.isActive(true); //set it active
						roles.add(newRole); 
						BankCustomerGui bcg = new BankCustomerGui((BankCustomerRole)newRole.role);
						((BankCustomerRole)newRole.role).setGui(bcg);
						cap.bankPanel.addGui(bcg);
						((BankCustomerRole)newRole.role).msgGoToBank(e.directive, 10); //message it with what we want to do
					}
					else { //we already have one use it
						((BankCustomerRole)getRoleOfType(bcr).role).msgGoToBank(e.directive, 10);
						getRoleOfType(bcr).isActive(true);
					}
					gui.setPresent(false);
					toDo.remove(e); //remove the event from the queue
				}
				else if(e.type == EventType.TellerEvent){
					activeRole = true;
					BankTellerRole btr = new BankTellerRole(this, this.name);; 
					if(!containsRole(btr)){ 
						MyRole newRole = new MyRole(btr);
						bank.getTimeCard().msgBackToWork(this, (BankTellerRole)newRole.role);
						newRole.isActive(true);
						roles.add(newRole);
						//add a gui
						BankTellerGui btg = new BankTellerGui((BankTellerRole)newRole.role);
						((BankTellerRole)newRole.role).setGui(btg);
						cap.bankPanel.addGui(btg);
					}
					else { 
						bank.getTimeCard().msgBackToWork(this, (BankTellerRole)getRoleOfType(btr).role); 
						getRoleOfType(btr).isActive(true);
					}
					if(!testMode){
						try {
							wait.acquire();
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					print("releasing wait");
					gui.setPresent(false);
					toDo.remove(e);
				}
				else if(e.type == EventType.HostEvent){
					activeRole = true;
					BankHostRole bhr = new BankHostRole(this, this.name);

					if(!containsRole(bhr)){ 
						print("Host not found");
						MyRole newRole = new MyRole(bhr);
						newRole.isActive(true);
						roles.add(newRole);
						BankHostGui btg = new BankHostGui((BankHostRole)newRole.role);
						((BankHostRole)newRole.role).setGui(btg);
						cap.bankPanel.addGui(btg);
						bank.getTimeCard().msgBackToWork(this, (BankHostRole)newRole.role);
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
					gui.setPresent(false);
					toDo.remove(e);
				}
			}
			else if(e.location.type == LocationType.Market){
				Market market = (Market)e.location;
				if(e.type == EventType.CustomerEvent){
					activeRole = true;
					MarketCustomerRole mcr = new MarketCustomerRole(this, this.name);

					if(!containsRole(mcr)){
						MyRole newRole = new MyRole(mcr);
						newRole.isActive(true);
						roles.add(newRole);
						((MarketCustomerRole)newRole.role).msgHello();
					}
					else{ 
						((MarketCustomerRole)getRoleOfType(mcr).role).msgHello(); 
						getRoleOfType(mcr).isActive(true);
					}
					gui.setPresent(false);
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
					gui.setPresent(false);
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
					gui.setPresent(false);
					toDo.remove(e);
				}
			}
			else if(e.location.type == LocationType.Home){
				if(e.type == EventType.HomeOwnerEvent){
					//Home home = (Home)e.location;
					activeRole = true;
					HomeOwnerRole hr = new HomeOwnerRole(this, this.name, homeNumber);
					//hr.myFridge.add(new Food("Chicken", 5));
					if(!containsRole(hr)){
						MyRole newRole = new MyRole(hr);
						newRole.isActive(true);
						roles.add(newRole);
						HomeOwnerGui hog = new HomeOwnerGui((HomeOwnerRole)newRole.role);
						((HomeOwnerRole)newRole.role).setGui(hog);
						cap.getHouseGui(2).addGui(hog);
						((HomeOwnerRole)newRole.role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level	
					}
					else{
						getRoleOfType(hr).isActive(true);
						((HomeOwnerRole)getRoleOfType(hr).role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level	
					}
					gui.setPresent(false);
					toDo.remove(e);
				}
				else if(e.type == EventType.AptTenantEvent){
					//Apartment apt = (Apartment)e.location;
					activeRole = true;
					ApartmentTenantRole tr = new ApartmentTenantRole(this.name, homeNumber, this);
					if(!containsRole(tr)){
						MyRole newRole = new MyRole(tr);
						newRole.isActive(true);
						roles.add(newRole);
						((ApartmentTenantRole)newRole.role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level	
					}
					else{
						getRoleOfType(tr).isActive(true);
						((ApartmentTenantRole)getRoleOfType(tr).role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level	
					}
					gui.setPresent(false);
					toDo.remove(e);
				}
				else if(e.type == EventType.LandlordEvent){
					//Apartment apt = (Apartment)e.location;
					activeRole = true;
					ApartmentLandlordRole llr = new ApartmentLandlordRole(this.name, homeNumber, this);
					if(!containsRole(llr)){
						MyRole newRole = new MyRole(llr);
						newRole.isActive(true);
						roles.add(newRole);
					}
					else{
						getRoleOfType(llr).isActive(true);
					}
					gui.setPresent(false);
					toDo.remove(e);
				}
			}
		}
	}

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
				wallet.addTransaction("Withdrawal", 100);
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

	private void dowalkto(int originx, int originy){
		gui.isPresent = true; 
		gui.walkto(originx, originy);
		//currentLocation.setX(originx);
		//currentLocation.setY(originy);
	}
	
	private void DoGoTo(Location loc){
		//if(car != null){
			//Position p = cityMap.getNearestStreet(currentLocation.getX(), currentLocation.getY());
			//car.setatPosition(loc.position.getX(), loc.position.getY());
			//car.goToPosition(p.getX(), p.getY());
		//}
		//else{ 
		gui.DoGoTo(loc.getPosition()); //}
		if(car != null){
			print("i've got a car whore");
			car.myGui.isPresent = true;
			gui.isPresent = false;
			Position p = cityMap.getNearestStreet(currentLocation.getX(), currentLocation.getY());
			print("My Location: "+currentLocation.getX()+ " , "+ currentLocation.getY()+ "   Position x: "+ p.getX() +" y: "+p.getY());
			car.setatPosition(p.getX(), p.getY());
		//	car.setatPosition(50,250);
			
			Position l = cityMap.getNearestStreet(loc.position.getX(), loc.position.getY());
			print("My Location: "+loc.position.getX()+ " , "+ loc.position.getY()+ "   Position x: "+ l.getX() +" y: "+l.getY());
			car.gotoPosition(l.getX(), l.getY());
           // car.gotoPosition(500,250);
		}
		else{ gui.DoGoTo(loc.getPosition()); }
	}
	private boolean isInWalkingDistance(Location loc){
		if(car != null){
			return true;
		}
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
	
	public void setcitygui(SimCityGUI scg){
		simcitygui = scg; 
		
		if (this.wallet.getOnHand() >= 100){
			print("Im rich gul");
			car = simcitygui.createCar(this);
		}
	}
}
