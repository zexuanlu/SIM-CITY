package person;

import gui.panels.CityAnimationPanel;
import gui.main.SimCityGUI;

import java.lang.Math;
import java.util.*;
import java.util.concurrent.Semaphore;

import person.Location.LocationType;
import person.SimEvent.EventImportance;
import person.SimEvent.EventType;
import bank.gui.*;
import bank.interfaces.*;
import bank.*;
import market.*;
import market.gui.MarketCustomerGui;
import market.interfaces.*;
import agent.*;
import person.gui.PersonGui;
import person.interfaces.Person;
import person.test.mock.EventLog;
import resident.ApartmentLandlordRole;
import resident.ApartmentTenantRole;
import resident.HomeOwnerRole;
import resident.gui.HomeOwnerGui;
import restaurant.Restaurant1CashierRole;
import restaurant.Restaurant1CookRole;
import restaurant.Restaurant1CustomerRole;
import restaurant.Restaurant1HostRole;
import restaurant.Restaurant1WaiterRole;
import restaurant.Restaurant1SDWaiterRole;
import restaurant.gui.CookGui;
import restaurant.gui.CustomerGui;
import restaurant.gui.WaiterGui;
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
 * @author Joseph Boman
 */
public class PersonAgent extends Agent implements Person{
	private EventLog log = new EventLog();
	public boolean testMode = false; //enabled for tests to skip semaphores
	private boolean atHome = false;
	
	private String name;
	public int hunger; // tracks hunger level
	public enum HomeType {Apartment, Home}
	public HomeType homeType;
	public int homeNumber;
	public AStarTraversal astar;

	public PersonGui gui;
	private CityAnimationPanel cap;
	public List<MyRole> roles = new ArrayList<MyRole>();

	public Wallet wallet;
	private int currentTime; 

	public Position currentLocation = new Position(0, 0); 
	public Position dest = new Position(50, 50);
	public CityMap cityMap;

	public List<SimEvent> toDo = new ArrayList<SimEvent>();

	public Map<String, Integer> shoppingList = new HashMap<String, Integer>();// for home role shopping ish
	public List<Food> shoppingBag = new ArrayList<Food>();


	public SimCityGUI simcitygui;


	CarAgent car; // car if the person has a car */ //Who is in charge of these classes?

	private Semaphore going = new Semaphore(0, true);
	//private Semaphore transport = new Semaphore(0, true);
	private Semaphore wait = new Semaphore(0, true);
	//private Semaphore driving = new Semaphore(0, true);

	public PersonAgent (String name, CityMap cm, double money){
		super();
		this.name = name;
		this.cityMap = cm;
		this.wallet = new Wallet(money, 0);//hacked in
		this.hunger = 4;
		currentTime = 7;
	}
	public PersonAgent (String name, CityMap cm, AStarTraversal astar2, double money){
		super();
		this.name = name;
		this.cityMap = cm;
		astar = astar2; 
		this.wallet = new Wallet(money, 0);//hacked in

		this.hunger = 4;
		currentTime = 7;
		this.astar = astar;
	}
	public PersonAgent () {
		super();
	}
	public PersonAgent (String name){
		super();
		this.name = name;
		this.wallet = new Wallet(600.00, 0);//hacked in
		this.hunger = 4;
	}

	/* Utilities */
	public void setName(String name){this.name = name;}

	public String getName(){ return this.name; }

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

	public CityMap getMap() {
		return cityMap;
	}

	public void addRole(MyRole r){ roles.add(r); }

	public void addRole(Role r, String type){
		MyRole mr = new MyRole(r, type);
		roles.add(mr);
	}
	public void populateCityMap(List<Location> loc){ cityMap = new CityMap(loc); } 

	/* Messages */
	public void msgAddMoney(double money){ 
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
		toDo.add(e);
		print("Message add event received");
		//stateChanged();
	}
	public void msgNewHour(int hour){ //from the world timer or gui 
		currentTime = hour;
		for(MyRole r : roles){
			if(r.role instanceof HomeOwnerRole || r.role instanceof ApartmentTenantRole){
				r.setActive(false);
			}
		}
		stateChanged();
	}
	public void msgAtDest(int x, int y){
		gui.xPos = x;
		gui.yPos = y;
		currentLocation.setX(x);
		currentLocation.setY(y);
		for(MyRole role : roles){
			if(role.role instanceof PassengerRole){
				role.setActive(false);
			}
		}
		gui.setPresent(true);
		stateChanged();
	}

	public void setStateChanged(){
		stateChanged();
	}

	public void msgAtDest(Position destination){ // From the gui. now we can send the correct entrance message to the location manager
		//print("Received the message AtDest");
		//gui.setPresent(false);
		currentLocation = destination;
		going.release();
		stateChanged();
	}

	public void msgAtDest(Position destination,CarAgent c){ // From the gui. now we can send the correct entrance message to the location manager
		print("Received the message AtDest from car");
		gui.setPresent(true);
		currentLocation = destination;
		dowalkto(destination.getX(),destination.getY());
	}

	public void msgFinishedEvent(Role r, List<Food> foodList, double change){
		atHome = false;
		print("Received this message");
		for(MyRole role : roles){
			if(role.role == r ){
				role.setActive(false);
			}
		}
		for(Food f : foodList){
			shoppingBag.add(f);
		}
		wallet.setOnHand(change);
		stateChanged();
	}
	public void msgFinishedEvent(Role r){ //The location manager will send this message as the persons role leaves the building
		atHome = false;
		print("Received msgFinishedEvent");
		for(MyRole role : roles){
			if(role.role == r ){
				role.setActive(false);
			}
		}
		if(!testMode){
		gui.setPresent(true);
		}
		stateChanged();
	}
	public void msgReadyToWork(Role r){
		wait.release();
		print("Received msgReadyToWork");
		for(MyRole role : roles){
			if(role.role == r ){
				role.setActive(true);
			}
		}
		stateChanged();
	}
	public void msgGoOffWork(Role r, double pay){ 
		print("Received the message go off work");
		wallet.setOnHand(pay);
		if(!testMode){
		gui.isPresent = true;
		}
		for(MyRole role : roles){
			if(role.role == r ){
				role.setActive(false);
			}
		}
		stateChanged();
	}

	/* Scheduler */

	@Override
	public boolean pickAndExecuteAnAction() {
//			for(SimEvent nextEvent : toDo){
//				if(nextEvent.importance == EventImportance.EmergencyEvent){
//					goToLocation(nextEvent.location);
//					goToAndDoEvent(nextEvent);
//					return true;
//				}
//			}
			//If the person is at home, this event will break him out for events such as work
			if(atHome){
				for(SimEvent nextEvent : toDo){
					if(nextEvent.startTime == currentTime && nextEvent.importance == EventImportance.RecurringEvent){ //if we have an event and its time to start or were in the process ofgetting there
						print("Activating a recurring event");
						for(MyRole mr : roles){
							if(mr.type.equals("Home Owner")){
								mr.setActive(false);
								((HomeOwnerRole)mr.role).DoGoToFrontDoor();
							}
							else if(mr.type.equals("Apt Tenant")){
								mr.setActive(false);
								((ApartmentTenantRole)mr.role).DoGoToFrontDoor();
							}
						}
						gui.setPresent(true);
						goToLocation(nextEvent.location);
						goToAndDoEvent(nextEvent);
						atHome = false;
						return true;
					}
				}
			}
			for(MyRole r : roles){
				if(r.isActive){
					//print("Executing rule in role "+ r.role);
					boolean b;
					b =  r.role.pickAndExecuteAnAction();
					//Do("" + b + " "+ r.role);
					return b;
				}
			}
			
			for(SimEvent nextEvent : toDo){
				//print("Priority is " + nextEvent.importance + " Time = " + currentTime + " : " + nextEvent.startTime);
				if(nextEvent.startTime == currentTime && nextEvent.importance == EventImportance.RecurringEvent){ //if we have an event and its time to start or were in the process ofgetting there
					print("Activating a recurring event");
					goToLocation(nextEvent.location);
					goToAndDoEvent(nextEvent);
					return true;
				}
			}
			
			for(SimEvent nextEvent : toDo){
				if(nextEvent.importance == EventImportance.OneTimeEvent){
					goToLocation(nextEvent.location);
					goToAndDoEvent(nextEvent);
					return true;
				}
			}
				return checkVitals();
	}

	/* Actions */

	private void goToAndDoEvent(SimEvent e){		
////////////////////////// REST 1 EVENTS /////////////////////////////////////////////////

			if(e.location.type == LocationType.Restaurant){
				Restaurant rest = (Restaurant)e.location;
				if(e.type == EventType.CustomerEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 Customer")){
							((Restaurant1CustomerRole)mr.role).customerGui.setPresent(true);
							((Restaurant1CustomerRole)mr.role).gotHungry();
							mr.setActive(true);
							gui.setPresent(false);
							toDo.remove(e);
							return;
						}
					}
					print("Customer not found");
					Restaurant1CustomerRole cRole = new Restaurant1CustomerRole(this.name, this);
					MyRole newRole = new MyRole(cRole, "Rest 1 Customer");
					newRole.setActive(true);
					roles.add(newRole);
					CustomerGui cg = new CustomerGui(cRole, null);
					cg.isPresent = true;
					cRole.setGui(cg);
					cap.rest1Panel.addGui(cg);
					cRole.setHost(rest.getHost());
					cRole.gotHungry();
					gui.setPresent(false);
					toDo.remove(e);
				}
				
				else if(e.type == EventType.HostEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 Host")){          
							rest.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					MyRole newRole = new MyRole(rest.getHost(), "Rest 1 Host");
					newRole.setActive(true);
					roles.add(newRole);
					rest.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.WaiterEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 Waiter")){  
							((Restaurant1WaiterRole)mr.role).waiterGui.setPresent(true);
							rest.getTimeCard().msgBackToWork(this, mr.role); 
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					Restaurant1WaiterRole wRole = new Restaurant1WaiterRole(this.name, this); 
					MyRole newRole = new MyRole(wRole, "Rest 1 Waiter");
					newRole.setActive(true);
					roles.add(newRole);
					WaiterGui wg = new WaiterGui((Restaurant1WaiterRole)newRole.role, null);
					wg.isPresent = true;
					cap.rest1Panel.addGui(wg);
					rest.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.SDWaiterEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 SDWaiter")){

						}
						else {
							((Restaurant1SDWaiterRole)mr.role).waiterGui.isPresent = true;
							rest.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					Restaurant1SDWaiterRole sdRole = new Restaurant1SDWaiterRole(this.name, this);
					MyRole newRole = new MyRole(sdRole, "Rest 1 SDWaiter");
					newRole.setActive(true);
					roles.add(newRole);
					WaiterGui wg = new WaiterGui((Restaurant1SDWaiterRole)newRole.role, null);
					wg.isPresent = true;
					cap.rest1Panel.addGui(wg);
					rest.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.CookEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 Cook")){                                                       
							rest.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							((Restaurant1CookRole)mr.role).cookGui.setPresent(true);
							gui.setPresent(false);
							return;
						}
					} 
					MyRole newRole = new MyRole(rest.getCook(), "Rest 1 Cook");//FIX
					newRole.setActive(true);
					roles.add(newRole);
					((Restaurant1CookRole)newRole.role).cookGui.setPresent(true);
					rest.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.CashierEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Rest 1 Cashier")){                                                       
							rest.getTimeCard().msgBackToWork(this, (Restaurant1CashierRole)mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					MyRole newRole = new MyRole(rest.getCashier(), "Rest 1 Cashier");//FIX
					newRole.setActive(true);
					roles.add(newRole);
					rest.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
			}
			
////////////////////////// BANK EVENTS /////////////////////////////////////////////////
			
			if(e.location.type == LocationType.Bank){ //if our event happens at a bank
				Bank bank = (Bank)e.location;
				if(e.type == EventType.CustomerEvent){ //if our intent is to act as a customer
					for(MyRole mr : roles){
						if(mr.type.equals("Bank Customer")){   //check if we don't already have it 
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, 200.00);//FIX - Should be fixed at some point to reflect a percent
							mr.setActive(true);
							gui.setPresent(false);
							((BankCustomerRole)mr.role).gui.setPresent(true);
							toDo.remove(e);
							return;
						}
					}
					BankCustomerRole bcr = new BankCustomerRole(this, this.name);
					MyRole newRole = new MyRole(bcr, "Bank Customer"); //make a new MyRole
					bcr.bh = bank.getHost();
					newRole.setActive(true); //set it active
					roles.add(newRole); 
					BankCustomerGui bcg = new BankCustomerGui((BankCustomerRole)newRole.role);
					((BankCustomerRole)newRole.role).setGui(bcg);
					cap.bankPanel.addGui(bcg);
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, 200.00); //FIX - Should be fixed at some point to reflect a percent
					gui.setPresent(false);
					toDo.remove(e); //remove the event from the queue
					return;
				}
				
				else if(e.type == EventType.TellerEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Bank Teller")){ 
							gui.setPresent(false);
							bank.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							((BankTellerRole)mr.role).gui.setPresent(true);
							mr.setActive(true);
							return;
						}
					}
					BankTellerRole btr = new BankTellerRole(this, this.name);
					MyRole newRole = new MyRole(btr, "Bank Teller");
					bank.getHost().addTeller(btr);
					bank.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					newRole.setActive(true);
					roles.add(newRole);
					BankTellerGui btg = new BankTellerGui(btr);
					btr.setGui(btg);
					btr.gui.setPresent(true);
					cap.bankPanel.addGui(btg);
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.HostEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Bank Host")){
							bank.getTimeCard().msgBackToWork(this, mr.role); 
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							((BankHostRole)mr.role).gui.setPresent(true);
							gui.setPresent(false);
							mr.setActive(true);
							return;
						}
					}
					MyRole newRole = new MyRole(bank.getHost(), "Bank Host");
					newRole.setActive(true);
					roles.add(newRole);
					bank.getTimeCard().msgBackToWork(this, (BankHostRole)newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					((BankHostRole)newRole.role).gui.setPresent(true);
					gui.setPresent(false);
					return;
				}
			}
			
////////////////////////// MARKET EVENTS /////////////////////////////////////////////////
			
			else if(e.location.type == LocationType.Market){
				Market market = (Market)e.location;
				
				if(e.type == EventType.CustomerEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Market Customer")){
							((MarketCustomerRole)mr.role).msgHello(wallet.getOnHand(), shoppingBag);
							mr.setActive(true);
							gui.setPresent(true);
							((MarketCustomerRole)mr.role).customerGui.setPresent(true);
							toDo.remove(e);
							return;
						}
					}
					MarketCustomerRole mcr = new MarketCustomerRole(this, this.name);
					MyRole newRole = new MyRole(mcr, "Market Customer");
					mcr.setCashier(market.getCashier());
					newRole.setActive(true);
					roles.add(newRole);
					MarketCustomerGui mcg = new MarketCustomerGui((MarketCustomerRole)newRole.role);
					((MarketCustomerRole)newRole.role).setGui(mcg);
					cap.marketPanel.addGui(mcg);
					mcg.setPresent(true);
					((MarketCustomerRole)newRole.role).msgHello(wallet.getOnHand(), shoppingBag);
					gui.setPresent(false);
					toDo.remove(e);
					return;
				}
				
				else if(e.type == EventType.EmployeeEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Market Employee")){
							market.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							((MarketEmployeeRole)mr.role).employeeGui.setPresent(true);
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					MarketEmployeeRole mer = new MarketEmployeeRole(this, this.name);
					MyRole newRole = new MyRole(mer, "Market Employee");
					newRole.setActive(true);
					roles.add(newRole);
					
					//Need to add a gui or not? FIX
		
					market.getTimeCard().msgBackToWork(this, newRole.role );
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
				
				else if(e.type == EventType.CashierEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Market Cashier")){
							market.getTimeCard().msgBackToWork(this, mr.role);
							try{
								wait.acquire();
							}
							catch(InterruptedException ie){
								ie.printStackTrace();
							}
							mr.setActive(true);
							gui.setPresent(false);
							return;
						}
					}
					MyRole newRole = new MyRole(market.getCashier(), "Market Cashier");
					newRole.setActive(true);
					roles.add(newRole);
					market.getTimeCard().msgBackToWork(this, newRole.role);
					try{
						wait.acquire();
					}
					catch(InterruptedException ie){
						ie.printStackTrace();
					}
					gui.setPresent(false);
					return;
				}
			}
			
/////////////////////// HOME EVENTS /////////////////////////////////////////////////////////////////
			
			else if(e.location.type == LocationType.Home){
				atHome = true;
				if(e.type == EventType.HomeOwnerEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Home Owner")){
							mr.setActive(true);
							((HomeOwnerRole)mr.role).homeGui.isPresent = true;
							((HomeOwnerRole)mr.role).updateVitals(hunger, currentTime);
							gui.setPresent(false);
							toDo.remove(e);
							return;
						}
					}
					HomeOwnerRole hr = new HomeOwnerRole(this, this.name, homeNumber);
					MyRole newRole = new MyRole(hr, "Home Owner");
					newRole.setActive(true);
					roles.add(newRole);
					HomeOwnerGui hog = new HomeOwnerGui((HomeOwnerRole)newRole.role);
					hog.setPresent(true);
					((HomeOwnerRole)newRole.role).setGui(hog);
					cap.getHouseGui(homeNumber).addGui(hog);
					((HomeOwnerRole)newRole.role).updateVitals(hunger, currentTime);
					gui.setPresent(false);
					toDo.remove(e);
					return;
				}
			}
			else if(e.location.type == LocationType.Apartment){
				atHome = true;
				if(e.type == EventType.AptTenantEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Apt Tenant")){
							mr.setActive(true);
							if(((ApartmentTenantRole)mr.role).aptGui != null)
								((ApartmentTenantRole)mr.role).aptGui.setPresent(true);
							((ApartmentTenantRole)mr.role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level
							gui.setPresent(false);
							toDo.remove(e);
							return;
						}
					}
					ApartmentTenantRole tr = new ApartmentTenantRole(this.name, homeNumber, this);
					MyRole newRole = new MyRole(tr, "Apt Tenant");
					newRole.setActive(true);
					roles.add(newRole);
					// FIX - Should we create a gui here? Or not?
					((ApartmentTenantRole)newRole.role).updateVitals(hunger, currentTime); //this will give you the current time and the persons hunger level	
					gui.setPresent(false);
					toDo.remove(e);
				}
				else if(e.type == EventType.LandlordEvent){
					for(MyRole mr : roles){
						if(mr.type.equals("Apt Landlord")){
							mr.setActive(true);
							gui.setPresent(false);
							toDo.remove(e);
							return;
						}
					}
					ApartmentLandlordRole llr = new ApartmentLandlordRole(this.name, homeNumber, this);
					MyRole newRole = new MyRole(llr, "Apt Landlord");
					newRole.setActive(true);
					roles.add(newRole);
					gui.setPresent(false);
					toDo.remove(e);
					return;
				}
		}
	}

	/* checkVitals is a method to figure out misc things to do on the fly*/
	private boolean checkVitals() { 
		/*NOTE: the locations in this method are hard coded until we get the init script that 
		 * puts together the people's cityMap objects which then will allow the people to 
		 * find locations on the fly via look up 
		 */
		boolean addedAnEvent = false;

		Bank b = (Bank)cityMap.getByType(LocationType.Bank);

		if(wallet.getOnHand() <= 100 && wallet.inBank > 200.00){ //get cash
			SimEvent needMoney = new SimEvent("withdraw", b, EventType.CustomerEvent);
			if(!containsEvent("withdraw")){ 
				toDo.add(needMoney);
				addedAnEvent = true;
			}
		}
		if(wallet.getOnHand() >= 100000){ //deposit cash
			SimEvent needDeposit = new SimEvent("deposit", b, EventType.CustomerEvent);
			if(!containsEvent("deposit")){
				toDo.add(needDeposit);
				addedAnEvent = true;
			}
		}
		if(!addedAnEvent && !containsEvent("Go home")){
			SimEvent goHome = null;
			if(homeNumber > 4){
				goHome = new SimEvent("Go home", (Apartment)cityMap.getHome(homeNumber), EventType.AptTenantEvent);
			}
			else{
				goHome = new SimEvent("Go home", (Home)cityMap.getHome(homeNumber), EventType.HomeOwnerEvent);
			}
			toDo.add(goHome);
			addedAnEvent = true;
		}
		return addedAnEvent;
		//buy a car
		//rob the bank
		//etc
	}
	
	private void goToLocation(Location loc){
//		if(!isInWalkingDistance(loc)){ //if its not in walking distance we ride the bus
//			//make a PassengerRole and start it
//			PassengerRole pRole = new PassengerRole(this.name, this);
//			if(!containsRole(pRole)){ //if we dont already have a PassengerRole make one
//				MyRole newRole = new MyRole(pRole, "Passenger");
//				newRole.setActive(true);
//				roles.add(newRole);
//				if(!testMode){
//				PassengerGui pg = new PassengerGui(((PassengerRole)newRole.role), gui.xPos, gui.yPos);
//				((PassengerRole)newRole.role).setGui(pg);
//				cap.addGui(pg);
//				}
//				((PassengerRole)newRole.role).setCityMap(cityMap);
//				((PassengerRole)newRole.role).setPassDestination(loc.position.getX(), loc.position.getY());
//				//lizhi added this testing:
//				gui.xDestination = loc.position.getX();
//				gui.yDestination = loc.position.getY();
//
//				((PassengerRole)newRole.role).gotoBus();
//				gui.setPresent(false);
//			}
//			else{ //if we already have a PassengerRole, use it
//				((PassengerRole)getRoleOfType(pRole).role).setDestination(loc.name);
//				((PassengerRole)getRoleOfType(pRole).role).gotoBus();
//				getRoleOfType(pRole).setActive(true);
//				gui.setPresent(false);
//			}
//		}
//		else{
			print("Going to location");
			DoGoTo(loc); 
			if(!testMode){
				going.drainPermits();
				try {
					going.acquire();
				} 
				catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
//		}
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
		if(testMode){ return; }
		gui.DoGoTo(loc.getPosition()); //}
//		if(car != null){
//			car.myGui.isPresent = true;
//			gui.isPresent = false;
//			Position p = cityMap.getNearestStreet(currentLocation.getX(), currentLocation.getY());
//			print("My Location: "+currentLocation.getX()+ " , "+ currentLocation.getY()+ "   Position x: "+ p.getX() +" y: "+p.getY());
//			//car.setatPosition(p.getX(), p.getY());
//
//			Position l = cityMap.getNearestStreet(loc.position.getX(), loc.position.getY());
//			print("My Location: "+loc.position.getX()+ " , "+ loc.position.getY()+ "   Position x: "+ l.getX() +" y: "+l.getY());
//			car.gotoPosition(p.getX(), p.getY(), l.getX(), l.getY());
//			// car.gotoPosition(500,250);
//		}
//		else{ gui.DoGoTo(loc.getPosition()); }
	}
	private boolean isInWalkingDistance(Location loc){
		if(testMode){
			return true;
		}
		if(car != null){
			return true;
		}
		int Quadrant = 0;

		if (gui.xPos < 280 && gui.yPos < 220) {
			Quadrant = 1;
		}
		else if(gui.xPos > 280 && gui.yPos < 220) {
			Quadrant = 2;
		}
		else if(gui.xPos < 280 && gui.yPos > 220){
			Quadrant = 3;
		}
		else if(gui.xPos > 280 && gui.yPos > 220) {
			Quadrant = 4;
		}

		if(Quadrant == loc.Quadrant){
			System.err.println("In walking distance :");
			return true;
		}
		System.err.println("not walking distance :");
		return false;
	}
	public class MyRole{
		public Role role;
		public boolean isActive;
		public String type;

		MyRole(Role r, String type){
			role = r;
			isActive = false;
			this.type = type;
		}
		void setActive(boolean tf){ isActive = tf; }
	}
	public class Wallet {

		private double onHand;
		private double inBank;
		private double balance;

		private List<BankTicket> transactions 
		= new ArrayList<BankTicket>();
		public Wallet(double oh, double ib){
			this.onHand = oh;
			this.inBank = ib;
			this.balance = oh + ib;
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
			Do("" + onHand);
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

		if (this.wallet.getOnHand() >= 1000.00){
			System.out.println("I have a car!");
			car = simcitygui.createCar(this);
		}
	}
	
	public Wallet getWallet() {
		return wallet;
	}
	
	 public MyRole getActiveRole(){
         for(MyRole role : roles){
             if(role.isActive){
            	 return role;
             }
         }
         return null;
	 }
 
	 public String getActiveRoleName(){
		 String none = "No Active Role";
         for(MyRole role : roles){
             if(role.isActive){
                 return role.role.getRoleName();
             }
         }
         return none;
	 }

	public void setHungerLevel(int i) {
		hunger = i;		
	}

}
