package person;

import gui.panels.CityAnimationPanel;
import gui.main.SimCityGUI;
import restaurant5.gui.Restaurant5FoodGui; 
import java.lang.Math;
import java.util.*;
import java.util.concurrent.Semaphore;
import restaurant5.*; 
import restaurant5.gui.*; 
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
import person.test.mock.LoggedEvent;
import resident.ApartmentLandlordRole;
import resident.ApartmentTenantRole;
import resident.HomeOwnerRole;
import resident.gui.HomeOwnerGui;
import restaurant1.Restaurant1CashierRole;
import restaurant1.Restaurant1CookRole;
import restaurant1.Restaurant1CustomerRole;
import restaurant1.Restaurant1HostRole;
import restaurant1.Restaurant1SDWaiterRole;
import restaurant1.Restaurant1WaiterRole;
import restaurant1.gui.CookGui;
import restaurant1.gui.Restaurant1CustomerGui;
import restaurant1.gui.WaiterGui;
import restaurant1.interfaces.Restaurant1Host;
import restaurant2.Restaurant2AbstractWaiterRole;
import restaurant2.Restaurant2CashierRole;
import restaurant2.Restaurant2CookRole;
import restaurant2.Restaurant2CustomerRole;
import restaurant2.Restaurant2HostRole;
import restaurant2.Restaurant2SDWaiterRole;
import restaurant2.Restaurant2WaiterRole;
import restaurant2.gui.Restaurant2CustomerGui;
import restaurant2.gui.Restaurant2WaiterGui;
import restaurant3.Restaurant3CashierRole;
import restaurant3.Restaurant3CookRole;
import restaurant3.Restaurant3CustomerRole;
import restaurant3.Restaurant3HostRole;
import restaurant3.Restaurant3SDWaiterRole;
import restaurant3.Restaurant3WaiterRole;
import restaurant3.gui.Restaurant3CustomerGui;
import restaurant3.gui.Restaurant3CookGui;
import restaurant3.gui.Restaurant3WaiterGui;
import restaurant4.Restaurant4CashierRole;
import restaurant4.Restaurant4CookRole;
import restaurant4.Restaurant4CustomerRole;
import restaurant4.Restaurant4HostRole;
import restaurant4.Restaurant4SDWaiterRole;
import restaurant4.Restaurant4WaiterRole;
import restaurant4.gui.Restaurant4CustomerGui;
import restaurant4.gui.Restaurant4WaiterGui;
import restaurant4.interfaces.Restaurant4Host;
import restaurant5.*;
import restaurant6.Restaurant6CashierRole;
import restaurant6.Restaurant6CookRole;
import restaurant6.Restaurant6CustomerRole;
import restaurant6.Restaurant6HostRole;
import restaurant6.Restaurant6SDWaiterRole;
import restaurant6.Restaurant6WaiterRole;
import restaurant6.gui.Restaurant6CustomerGui;
import restaurant6.gui.Restaurant6WaiterGui;
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
	public EventLog log = new EventLog();
	public boolean testMode = false; //enabled for tests to skip semaphores
	private boolean atHome = false;
	private boolean atCasino = false;
	public boolean walking = false;

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

	public Position currentLocation; 
	public Position dest = new Position(50, 50);
	public CityMap cityMap;

	public List<SimEvent> toDo = new ArrayList<SimEvent>();

	public Map<String, Integer> shoppingList = new HashMap<String, Integer>();// for home role shopping ish
	public List<Food> shoppingBag = new ArrayList<Food>();

	public SimCityGUI simcitygui;


	CarAgent car; // car if the person has a car */ //Who is in charge of these classes?

	private Semaphore going = new Semaphore(0, true);
	private Semaphore wait = new Semaphore(0, true);

	public PersonAgent (String name, CityMap cm, double money){
		super();
		this.name = name;
		this.cityMap = cm;
		this.wallet = new Wallet(money, 0);//hacked in
		this.hunger = 0;
		currentTime = 7;

		shoppingBag.add(new Food("Chicken", 1));
	}
	public PersonAgent (String name, CityMap cm, AStarTraversal astar2, double money){
		super();
		this.name = name;
		this.cityMap = cm;
		astar = astar2; 
		this.wallet = new Wallet(money, 0);//hacked in

		this.hunger = 0;
		currentTime = 7;
		this.astar = astar;
		PersonAgent.tracePanel.print("Initializing", this);
	}
	public PersonAgent () {
		super();
	}
	public PersonAgent (String name){
		super();
		this.name = name;
		this.wallet = new Wallet(600.00, 0);//hacked in
		this.hunger = 0;
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
	public boolean active(){
		for (MyRole r : roles){
			if(r.isActive){
				return true;
			}
		}
		return false;
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
	public void msgGoHome(){
		atCasino = false;
		SimEvent goHome = null;
		if(homeType == HomeType.Home){
			goHome = new SimEvent("Go Home", (Home)cityMap.getHome(homeNumber), EventType.HomeOwnerEvent);
		}
		else if(homeType == HomeType.Apartment){
			goHome = new SimEvent("Go Home", (Apartment)cityMap.getHome(homeNumber), EventType.AptTenantEvent);
		}
		toDo.add(goHome);
		stateChanged();
	}
	public void setGui(PersonGui pg){
		gui = pg; 
		currentLocation = new Position(pg.xPos, pg.yPos);

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
				if(r.isActive)
					atHome = true;
				r.setActive(false);
			}
		}
		if(hour % 3 == 0){
			hunger++;
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

	public void msgAtDest(Position destination){ 
		//From the gui. now we can send the correct entrance message to the location manager
		//print("Received the message AtDest");
		//gui.setPresent(false);
		currentLocation = destination;
		going.release();
		stateChanged();
	}

	public void msgAtDest(Position destination,CarAgent c){ // From the gui. now we can send the correct entrance message to the location manager
		print("Received the message AtDest from car");
		going.release();
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
		gui.setPresent(true);
		wallet.setOnHand(change);
		stateChanged();
	}
	public void msgFinishedEvent(Role r){ 
		atHome = false;
		print("Received msgFinishedEvent");
		for(MyRole role : roles){
			if(role.role == r){
				role.setActive(false);
				utilities.Gui ug = role.role.getGui();
				if (ug != null){
					ug.setPresent(false);
				}
			}
		}
		if(!testMode){
			gui.setPresent(true);
		}
		stateChanged();
	}
	public void msgReadyToWork(Role r){
		log.add(new LoggedEvent("Recieved msgReadyToWork"));
		wait.release();
		print("Received msgReadyToWork");
		for(MyRole role : roles){
			if(role.role == r ){
				role.setActive(true);
			}
		}
		gui.setPresent(false);
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
				if(role.role.getGui() != null)
					role.role.getGui().setPresent(false);
				role.setActive(false);
			}
		}
		stateChanged();
	}

	public void msgBanished(){
		gui.setPresent(true);
		if(gui.xPos > 350){
			gui.xDestination = 440;
			gui.yDestination = -30;
			gui.walkto(gui.xPos, gui.yPos);
		}
		else{
			gui.xDestination = 330;
			gui.yDestination = -30;
			gui.walkto(gui.xPos, gui.yPos);
		}
		try{
			going.acquire();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
		Do("Terminating thread");
		gui.setPresent(false);
		this.stopThread();
	}
	/* Scheduler */

	@Override
	public boolean pickAndExecuteAnAction() {
		if(!atCasino){
			for(MyRole r : roles){
				if(r.isActive){
					boolean b;
					b =  r.role.pickAndExecuteAnAction();
					return b;
				}
			}

			for(SimEvent nextEvent : toDo){
				if(nextEvent.startTime == currentTime && nextEvent.importance == EventImportance.RecurringEvent){ //if we have an event and its time to start or were in the process ofgetting there
					if(atHome){
						for(MyRole r : roles){
							if(r.role instanceof HomeOwnerRole){
								((HomeOwnerRole)r.role).homeGui.DoGoToFrontDoor();
								try{
									((HomeOwnerRole)r.role).atFrontDoor.acquire();
								}
								catch(InterruptedException ie){
									ie.printStackTrace();
								}
								((HomeOwnerRole)r.role).homeGui.setPresent(false);
							}
							else if(r.role instanceof ApartmentTenantRole){
								((ApartmentTenantRole)r.role).aptGui.DoGoToFrontDoor();
								try{
									((ApartmentTenantRole)r.role).atFrontDoor.acquire();
								}
								catch(InterruptedException ie){
									ie.printStackTrace();
								}
								((ApartmentTenantRole)r.role).aptGui.DoGoToFrontDoor();
							}

						}
						atHome = false;
					}			
					print("Activating a recurring event");
					goToLocation(nextEvent.location);
					goToAndDoEvent(nextEvent);
					return true;
				}
			}

			for(SimEvent nextEvent : toDo){
				if(nextEvent.importance == EventImportance.OneTimeEvent){
					System.err.println("THIS IS THE EVENT AND IT IS "+nextEvent.location.isClosed);
					if(!nextEvent.location.isClosed()){
						if(!atHome)
							goToLocation(nextEvent.location);
						goToAndDoEvent(nextEvent);
						return true;
					}
					toDo.remove(nextEvent);
					return true;
				}
			}
			return checkVitals();
		}
		return false;
	}

	/* Actions */
	private void goToAndDoEvent(SimEvent e){		
		////////////////////////// REST 1 EVENTS /////////////////////////////////////////////////
		if(e.location.type == LocationType.Restaurant1){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
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
				Restaurant1CustomerGui cg = new Restaurant1CustomerGui(cRole, null);
				cg.isPresent = true;
				cRole.setGui(cg);
				if(!testMode){
					cap.rest1Panel.addGui(cg);
				}
				cRole.setHost(((Restaurant1Host)rest.getHost()));
				cRole.gotHungry();
				if(!testMode){
					gui.setPresent(false);
				}
				toDo.remove(e);
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
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
				MyRole newRole = new MyRole(((Restaurant1HostRole)rest.getHost()), "Rest 1 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
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
				WaiterGui wg = new WaiterGui((Restaurant1WaiterRole)newRole.role);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 1 SDWaiter")){
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
				WaiterGui wg = new WaiterGui((Restaurant1SDWaiterRole)newRole.role);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
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
				MyRole newRole = new MyRole(((Restaurant1CookRole)rest.getCook()), "Rest 1 Cook");//FIX
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
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
				MyRole newRole = new MyRole(((Restaurant1CashierRole)rest.getCashier()), "Rest 1 Cashier");//FIX
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
		////////////////////////////////REST 2 EVENTS//////////////////////////////////////////////////////
		else if(e.location.type == LocationType.Restaurant2){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 Customer")){
						((Restaurant2CustomerRole)mr.role).customerGui.setPresent(true);
						((Restaurant2CustomerRole)mr.role).gotHungry();
						mr.setActive(true);
						gui.setPresent(false);
						toDo.remove(e);
						return;
					}
				}
				print("Customer not found");
				Restaurant2CustomerRole cRole = new Restaurant2CustomerRole(this.name, this);
				MyRole newRole = new MyRole(cRole, "Rest 2 Customer");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant2CustomerGui cg = new Restaurant2CustomerGui(cRole, 20, 20);
				cg.isPresent = true;
				cRole.setGui(cg);
				cap.rest2Panel.addGui(cg);
				cRole.setHost(((Restaurant2HostRole)rest.getHost()));
				cRole.gotHungry();
				gui.setPresent(false);
				toDo.remove(e);
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 Host")){          
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
				MyRole newRole = new MyRole(((Restaurant2HostRole)rest.getHost()), "Rest 2 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 Waiter")){  
						((Restaurant2WaiterRole)mr.role).waiterGui.setPresent(true);
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
				Restaurant2WaiterRole wRole = new Restaurant2WaiterRole(this.name, this); 
				MyRole newRole = new MyRole(wRole, "Rest 4 Waiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant2WaiterGui wg = new Restaurant2WaiterGui((Restaurant2WaiterRole)newRole.role);
				wg.setPresent(true);
				cap.rest2Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 SDWaiter")){
						((Restaurant2SDWaiterRole)mr.role).waiterGui.setPresent(true);
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
				Restaurant2SDWaiterRole sdRole = new Restaurant2SDWaiterRole(this.name, this);
				MyRole newRole = new MyRole(sdRole, "Rest 2 SDWaiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant2WaiterGui wg = new Restaurant2WaiterGui((Restaurant2SDWaiterRole)newRole.role);
				wg.setPresent(true);
				cap.rest4Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 Cook")){                                                       
						rest.getTimeCard().msgBackToWork(this, mr.role);
						try{
							wait.acquire();
						}
						catch(InterruptedException ie){
							ie.printStackTrace();
						}
						mr.setActive(true);
						((Restaurant2CookRole)mr.role).cookGui.setPresent(true);
						gui.setPresent(false);
						return;
					}
				} 
				MyRole newRole = new MyRole(((Restaurant2CookRole)rest.getCook()), "Rest 2 Cook");//FIX
				newRole.setActive(true);
				roles.add(newRole);
				((Restaurant2CookRole)newRole.role).cookGui.setPresent(true);
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 2 Cashier")){                                                       
						rest.getTimeCard().msgBackToWork(this, (Restaurant2CashierRole)mr.role);
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
				MyRole newRole = new MyRole(((Restaurant2CashierRole)rest.getCashier()), "Rest 2 Cashier");//FIX
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
		////////////////////////////////REST 3 EVENTS//////////////////////////////////////////////////////
		else if(e.location.type == LocationType.Restaurant3){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 Customer")){
						((Restaurant3CustomerRole)mr.role).custGui.setPresent(true);
						((Restaurant3CustomerRole)mr.role).gotHungry();
						mr.setActive(true);
						gui.setPresent(false);
						toDo.remove(e);
						return;
					}
				}
				print("Customer not found");
				Restaurant3CustomerRole cRole = new Restaurant3CustomerRole(this.name, this);
				MyRole newRole = new MyRole(cRole, "Rest 3 Customer");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant3CustomerGui cg = new Restaurant3CustomerGui(cRole);
				cg.isPresent = true;
				cRole.setGui(cg);
				cap.rest3Panel.addGui(cg);
				cRole.setHost(((Restaurant3HostRole)rest.getHost()));
				cRole.gotHungry();
				gui.setPresent(false);
				toDo.remove(e);
			}
			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 Host")){          
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
				MyRole newRole = new MyRole(((Restaurant3HostRole)rest.getHost()), "Rest 3 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 Waiter")){  
						((Restaurant3WaiterRole)mr.role).waiterGui.setPresent(true);
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
				Restaurant3WaiterRole wRole = new Restaurant3WaiterRole(this.name, this); 
				MyRole newRole = new MyRole(wRole, "Rest 3 Waiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant3WaiterGui wg = new Restaurant3WaiterGui((Restaurant3WaiterRole)newRole.role);
				wg.isPresent = true;
				cap.rest3Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 SDWaiter")){

						((Restaurant3SDWaiterRole)mr.role).waiterGui.isPresent = true;
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
				Restaurant3SDWaiterRole sdRole = new Restaurant3SDWaiterRole(this.name, this);
				MyRole newRole = new MyRole(sdRole, "Rest 3 SDWaiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant3WaiterGui wg = new Restaurant3WaiterGui((Restaurant3SDWaiterRole)newRole.role);
				wg.isPresent = true;
				cap.rest3Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 Cook")){                                                       
						rest.getTimeCard().msgBackToWork(this, mr.role);
						try{
							wait.acquire();
						}
						catch(InterruptedException ie){
							ie.printStackTrace();
						}
						mr.setActive(true);
						((Restaurant3CookRole)mr.role).cookGui.setPresent(true);
						gui.setPresent(false);
						return;
					}
				} 
				MyRole newRole = new MyRole(((Restaurant3CookRole)rest.getCook()), "Rest 3 Cook");//FIX
				newRole.setActive(true);
				roles.add(newRole);
				((Restaurant3CookRole)newRole.role).cookGui.setPresent(true);
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 3 Cashier")){                                                       
						rest.getTimeCard().msgBackToWork(this, (Restaurant3CashierRole)mr.role);
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
				MyRole newRole = new MyRole(((Restaurant3CashierRole)rest.getCashier()), "Rest 3 Cashier");//FIX
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
		////////////////////////////////REST 4 EVENTS//////////////////////////////////////////////////////
		else if(e.location.type == LocationType.Restaurant4){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 4 Customer")){
						((Restaurant4CustomerRole)mr.role).customerGui.setPresent(true);
						((Restaurant4CustomerRole)mr.role).gotHungry();
						((Restaurant4CustomerRole)mr.role).money = wallet.onHand;
						mr.setActive(true);
						gui.setPresent(false);
						toDo.remove(e);
						return;
					}
				}
				print("Customer not found");
				Restaurant4CustomerRole cRole = new Restaurant4CustomerRole(this.name, this);
				MyRole newRole = new MyRole(cRole, "Rest 4 Customer");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant4CustomerGui cg = new Restaurant4CustomerGui(cRole);
				cg.isPresent = true;
				cRole.setGui(cg);
				cap.rest4Panel.addGui(cg);
				cRole.money = wallet.onHand;
				cRole.setHost(((Restaurant4HostRole)rest.getHost()));
				cRole.gotHungry();
				gui.setPresent(false);
				toDo.remove(e);
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 4 Host")){          
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
				MyRole newRole = new MyRole(((Restaurant4HostRole)rest.getHost()), "Rest 4 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					Do("Checking Role " + mr.type);
					if(mr.type.equals("Rest 4 Waiter")){  
						Do("Found ONe!");
						((Restaurant4WaiterRole)mr.role).gui.setPresent(true);
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
				Restaurant4WaiterRole wRole = new Restaurant4WaiterRole(this.name, this); 
				MyRole newRole = new MyRole(wRole, "Rest 4 Waiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant4WaiterGui wg = new Restaurant4WaiterGui((Restaurant4WaiterRole)newRole.role, 0, 0);
				wg.isPresent = true;
				cap.rest4Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					Do("Checking Role " + mr.type);
					if(mr.type.equals("Rest 4 SDWaiter")){
						Do("Found one!");
						((Restaurant4SDWaiterRole)mr.role).gui.isPresent = true;
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
				Restaurant4SDWaiterRole sdRole = new Restaurant4SDWaiterRole(this.name, this);
				MyRole newRole = new MyRole(sdRole, "Rest 4 SDWaiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant4WaiterGui wg = new Restaurant4WaiterGui((Restaurant4SDWaiterRole)newRole.role, 0, 0);
				wg.isPresent = true;
				cap.rest4Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 4 Cook")){                                                       
						rest.getTimeCard().msgBackToWork(this, mr.role);
						try{
							wait.acquire();
						}
						catch(InterruptedException ie){
							ie.printStackTrace();
						}
						mr.setActive(true);
						((Restaurant4CookRole)mr.role).gui.setPresent(true);
						gui.setPresent(false);
						return;
					}
				} 
				MyRole newRole = new MyRole(((Restaurant4CookRole)rest.getCook()), "Rest 4 Cook");//FIX
				newRole.setActive(true);
				roles.add(newRole);
				((Restaurant4CookRole)newRole.role).gui.setPresent(true);
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 4 Cashier")){                                                       
						rest.getTimeCard().msgBackToWork(this, (Restaurant4CashierRole)mr.role);
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
				MyRole newRole = new MyRole(((Restaurant4CashierRole)rest.getCashier()), "Rest 4 Cashier");//FIX
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

		//////////////////////////REST 5 EVENTS /////////////////////////////////////////////////
		if(e.location.type == LocationType.Restaurant5){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 Customer")){
						((Restaurant5CustomerAgent)mr.role).customerGui.setPresent(true);
						((Restaurant5CustomerAgent)mr.role).gotHungry();
						mr.setActive(true);
						gui.setPresent(false);
						toDo.remove(e);
						return;
					}
				}
				print("Customer not found");
				Restaurant5CustomerAgent cRole = new Restaurant5CustomerAgent(this.name, this);
				Restaurant5FoodGui fgui = new Restaurant5FoodGui();
				cRole.setFoodGui(fgui);
				MyRole newRole = new MyRole(cRole, "Rest 5 Customer");

				newRole.setActive(true);
				roles.add(newRole);
				Restaurant5CustomerGui cg = new Restaurant5CustomerGui(cRole);
				cg.isPresent = true;
				cRole.setGui(cg);
				cap.rest5Panel.addGui(cg);
				cRole.setHost(((Restaurant5HostAgent)rest.getHost()));
				cRole.setCashier(((Restaurant5Cashier)rest.getCashier()));
				((Restaurant5CustomerAgent)cRole).customerGui.setPresent(true);
				cRole.gotHungry();
				gui.setPresent(false);
				toDo.remove(e);
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 Host")){          
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
				MyRole newRole = new MyRole(((Restaurant5HostAgent)rest.getHost()), "Rest 5 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 Waiter")){  
						((Restaurant5WaiterAgent)mr.role).waiterGui.isPresent = true;
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
				Restaurant5WaiterAgent wRole = new Restaurant5WaiterAgent(this.name, this); 
				MyRole newRole = new MyRole(wRole, "Rest 5 Waiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant5WaiterGui wg = new Restaurant5WaiterGui((Restaurant5WaiterAgent)newRole.role);
				wg.isPresent = true;
				cap.rest5Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 SDWaiter")){
						((Restaurant5SDWaiterAgent)mr.role).waiterGui.isPresent = true;
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
					else {
						((Restaurant5SDWaiterAgent)mr.role).waiterGui.isPresent = true;
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
				Restaurant5SDWaiterAgent sdRole = new Restaurant5SDWaiterAgent(this.name, this);
				MyRole newRole = new MyRole(sdRole, "Rest 5 SDWaiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant5WaiterGui wg = new Restaurant5WaiterGui((WaiterBase5)newRole.role);
				wg.isPresent = true;
				cap.rest5Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 Cook")){                                                       
						rest.getTimeCard().msgBackToWork(this, mr.role);
						try{
							wait.acquire();
						}
						catch(InterruptedException ie){
							ie.printStackTrace();
						}
						mr.setActive(true);
						((Restaurant5CookAgent)mr.role).cookGui.isPresent = true;
						gui.setPresent(false);
						return;
					}
				} 
				MyRole newRole = new MyRole(((Restaurant5CookAgent)rest.getCook()), "Rest 5 Cook");//FIX
				newRole.setActive(true);
				roles.add(newRole);
				((Restaurant5CookAgent)newRole.role).cookGui.isPresent = true;
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 5 Cashier")){                                                       
						rest.getTimeCard().msgBackToWork(this, (Restaurant5Cashier)mr.role);
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
				MyRole newRole = new MyRole(((Restaurant5Cashier)rest.getCashier()), "Rest 5 Cashier");//FIX
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
		//////////////////////////REST 6 EVENTS /////////////////////////////////////////////////
		if(e.location.type == LocationType.Restaurant6){
			Restaurant rest = (Restaurant)e.location;
			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Restaurant Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 Customer")){
						((Restaurant6CustomerRole)mr.role).customerGui.setPresent(true);
						((Restaurant6CustomerRole)mr.role).gotHungry();
						mr.setActive(true);
						gui.setPresent(false);
						toDo.remove(e);
						return;
					}
				}
				print("Customer not found");
				Restaurant6CustomerRole cRole = new Restaurant6CustomerRole(this.name, this);
				MyRole newRole = new MyRole(cRole, "Rest 6 Customer");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant6CustomerGui cg = new Restaurant6CustomerGui(cRole);
				cg.isPresent = true;
				cRole.setGui(cg);
				cap.rest6Panel.addGui(cg);
				cRole.setHost(((Restaurant6HostRole)rest.getHost()));
				cRole.setCashier(((Restaurant6CashierRole)rest.getCashier()));
				((Restaurant6CustomerRole)cRole).customerGui.setPresent(true);
				cRole.gotHungry();
				gui.setPresent(false);
				toDo.remove(e);
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Restaurant Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 Host")){          
						rest.getTimeCard().msgBackToWork(this, mr.role);
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
				MyRole newRole = new MyRole(((Restaurant6HostRole)rest.getHost()), "Rest 6 Host");
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
				PersonAgent.tracePanel.print("Restaurant Waiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 Waiter")){  
						((Restaurant6WaiterRole)mr.role).waiterGui.isPresent = true;
						rest.getTimeCard().msgBackToWork(this, mr.role); 
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
				Restaurant6WaiterRole wRole = new Restaurant6WaiterRole(this.name, this); 
				MyRole newRole = new MyRole(wRole, "Rest 6 Waiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant6WaiterGui wg = new Restaurant6WaiterGui((Restaurant6WaiterRole)newRole.role, -20, -20);
				wg.isPresent = true;
				cap.rest6Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant SDWaiter at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 SDWaiter")){

						((Restaurant6SDWaiterRole)mr.role).waiterGui.isPresent = true;
						rest.getTimeCard().msgBackToWork(this, mr.role);
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
				Restaurant6SDWaiterRole sdRole = new Restaurant6SDWaiterRole(this.name, this);
				MyRole newRole = new MyRole(sdRole, "Rest 6 SDWaiter");
				newRole.setActive(true);
				roles.add(newRole);
				Restaurant6WaiterGui wg = new Restaurant6WaiterGui((Restaurant6SDWaiterRole)newRole.role, -20, -20);
				wg.isPresent = true;
				cap.rest6Panel.addGui(wg);
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
				PersonAgent.tracePanel.print("Restaurant Cook at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 Cook")){                                                       
						rest.getTimeCard().msgBackToWork(this, mr.role);
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
				MyRole newRole = new MyRole(((Restaurant6CookRole)rest.getCook()), "Rest 6 Cook");//FIX
				newRole.setActive(true);
				roles.add(newRole);
				((Restaurant6CookRole)newRole.role).cookGui.isPresent = true;
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
				PersonAgent.tracePanel.print("Restaurant Cashier at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Rest 6 Cashier")){                                                       
						rest.getTimeCard().msgBackToWork(this, (Restaurant6CashierRole)mr.role);
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
				MyRole newRole = new MyRole(((Restaurant6CashierRole)rest.getCashier()), "Rest 6 Cashier");//FIX
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
				PersonAgent.tracePanel.print("Bank Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Bank Customer")){   //check if we don't already have it 
						if(e.directive.equals("deposit"))
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, wallet.onHand/2);
						else if(e.directive.equals("withdraw"))
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, 500.00);//FIX?
						else if(e.directive.equals("robBank"))
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, 100000.00);
						mr.setActive(true);
						gui.setPresent(false);
						((BankCustomerRole)mr.role).gui.setPresent(true);
						toDo.remove(e);
						return;
					}
				}
				System.err.println("Creating new role");
				BankCustomerRole bcr = new BankCustomerRole(this, this.name);
				MyRole newRole = new MyRole(bcr, "Bank Customer"); //make a new MyRole
				bcr.bh = bank.getHost();
				newRole.setActive(true); //set it active
				roles.add(newRole); 
				BankCustomerGui bcg = new BankCustomerGui((BankCustomerRole)newRole.role);
				((BankCustomerRole)newRole.role).setGui(bcg);
				if(!testMode){
					cap.bankPanel.addGui(bcg);
				}
				if(e.directive.equals("deposit"))
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, wallet.onHand/2);
				else if(e.directive.equals("withdraw"))
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, 500.00);//FIX?
				else if(e.directive.equals("robBank"))
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, 100000.00);
				gui.setPresent(false);
				((BankCustomerRole)newRole.role).gui.setPresent(true);
				toDo.remove(e); //remove the event from the queue
				return;
			}

			else if(e.type == EventType.TellerEvent){
				PersonAgent.tracePanel.print("Bank Teller at " + e.location.getName(), this);
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
				if(!testMode){
					cap.bankPanel.addGui(btg);
				}
				gui.setPresent(false);
				return;
			}

			else if(e.type == EventType.HostEvent){
				PersonAgent.tracePanel.print("Bank Host at " + e.location.getName(), this);
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
		//////////////////////////BANK 2 EVENTS /////////////////////////////////////////////////

		else if(e.location.type == LocationType.Bank2){ //if our event happens at a bank
			Bank bank = (Bank)e.location;
			if(e.type == EventType.CustomerEvent){ //if our intent is to act as a customer
				PersonAgent.tracePanel.print("Bank Customer at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Bank Customer")){   //check if we don't already have it 
						if(e.directive.equals("deposit"))
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, wallet.onHand/2);
						else if(e.directive.equals("withdraw"))
							((BankCustomerRole)mr.role).msgGoToBank(e.directive, 500.00);//FIX?
						mr.setActive(true);
						gui.setPresent(false);
						((BankCustomerRole)mr.role).gui.setPresent(true);
						toDo.remove(e);
						return;
					}
				}
				System.err.println("Creating new role");
				BankCustomerRole bcr = new BankCustomerRole(this, this.name);
				MyRole newRole = new MyRole(bcr, "Bank Customer"); //make a new MyRole
				bcr.bh = bank.getHost();
				newRole.setActive(true); //set it active
				roles.add(newRole); 
				BankCustomerGui bcg = new BankCustomerGui((BankCustomerRole)newRole.role);
				((BankCustomerRole)newRole.role).setGui(bcg);
				cap.bankPanel.addGui(bcg);
				if(e.directive.equals("deposit"))
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, wallet.onHand/2);
				else if(e.directive.equals("withdraw"))
					((BankCustomerRole)newRole.role).msgGoToBank(e.directive, 500.00);//FIX?
				gui.setPresent(false);
				((BankCustomerRole)newRole.role).gui.setPresent(true);
				toDo.remove(e); //remove the event from the queue
				return;
			}

			else if(e.type == EventType.TellerEvent){
				for(MyRole mr : roles){
					PersonAgent.tracePanel.print("Bank Teller at " + e.location.getName(), this);
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
				PersonAgent.tracePanel.print("Bank Host at " + e.location.getName(), this);
				for(MyRole mr : roles){
					if(mr.type.equals("Bank Host")){
						print("Messaging the Time Card!");
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
				PersonAgent.tracePanel.print("Market Customer at " + e.location.getName(), this);
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
					PersonAgent.tracePanel.print("Market Employee at " + e.location.getName(), this);
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
					PersonAgent.tracePanel.print("Market Cashier at " + e.location.getName(), this);
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

		//////////////////////////MARKET 2 EVENTS /////////////////////////////////////////////////

		else if(e.location.type == LocationType.Market2){
			Market market = (Market)e.location;

			if(e.type == EventType.CustomerEvent){
				PersonAgent.tracePanel.print("Market Customer at " + e.location.getName(), this);
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
				PersonAgent.tracePanel.print("Market Employee at " + e.location.getName(), this);
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
				PersonAgent.tracePanel.print("Market Cashier at " + e.location.getName(), this);
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
		//////////////////////// CASINO //////////////////////////////////////////////////////////////////////
		else if(e.location.type == LocationType.Casino){
			PersonAgent.tracePanel.print("Casino Player at " + e.location.getName(), this);
			gui.setPresent(false);
			toDo.remove(e);
			atCasino = true;
			return;
		}
		/////////////////////// HOME EVENTS /////////////////////////////////////////////////////////////////

		else if(e.location.type == LocationType.Home){
			if(!atHome){
				PersonAgent.tracePanel.print("At Home", this);
				atHome = true;
			}
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
			if(!atHome){
				PersonAgent.tracePanel.print("At Apartment", this);
				atHome = true;
			}
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
				/// FIX - Should we create a gui here? Or not?
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

		Bank b = cityMap.pickABank(gui.xPos, gui.yPos);//(Bank)cityMap.getByType(LocationType.Bank);
		if(b != null){
			if(wallet.getOnHand() <= 100 && wallet.inBank > 200.00){ //get cash
				SimEvent needMoney = new SimEvent("withdraw", b, EventType.CustomerEvent);
				if(!containsEvent("withdraw")){ 
					toDo.add(needMoney);
					addedAnEvent = true;
				}
			}
			if(wallet.getOnHand() >= 1400){ //deposit cash
				SimEvent needDeposit = new SimEvent("deposit", b, EventType.CustomerEvent);
				if(!containsEvent("deposit")){
					toDo.add(needDeposit);
					addedAnEvent = true;
				}
			}
		}
		if(wallet.getOnHand() >= 2500 && car == null){
			Market m = (Market)cityMap.getByType(LocationType.Market);
			SimEvent buyCar = new SimEvent("Go buy a car", m,EventType.CustomerEvent);
			if(!containsEvent("Go buy a car")){
				toDo.add(buyCar);
				addedAnEvent = true;
			}
		}
		if(hunger > 3 && !containsEvent("Go Eat") && !cityMap.ateOutLast){
			System.err.println("Going to eat!");
			toDo.add(new SimEvent("Go Eat", (Restaurant)cityMap.eatOutOrIn(), EventType.CustomerEvent));
			addedAnEvent = true;
		}
		if(!addedAnEvent && !containsEvent("Go home")){
			SimEvent goHome = null;
			if(homeNumber > 5){
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
		PersonAgent.tracePanel.print("Going to " + loc.getName(), this);
		Do(loc.position.toString() + ":" + loc.getName());
		if (!walking) {
			if(!isInWalkingDistance(loc)){ //if its not in walking distance we ride the bus
				//			//make a PassengerRole and start it
				PassengerRole pRole = new PassengerRole(this.name, this);
				//			if(!containsRole(pRole)){ //if we dont already have a PassengerRole make one
				MyRole newRole = new MyRole(pRole, "Passenger");
				newRole.setActive(true);
				roles.add(newRole);
				if(!testMode){
					PassengerGui pg = new PassengerGui(((PassengerRole)newRole.role), gui.xPos, gui.yPos);
					pg.setPresent(true);
					print("gui xpos is " + gui.xPos + " " + gui.yPos);
					((PassengerRole)newRole.role).setGui(pg);
					cap.addGui(pg);
				}
				((PassengerRole)newRole.role).setCityMap(cityMap);
				((PassengerRole)newRole.role).setPassDestination(loc.position.getX(), loc.position.getY());
				//lizhi added this testing:
				gui.xDestination = loc.position.getX();
				gui.yDestination = loc.position.getY();

				((PassengerRole)newRole.role).gotoBus();
				gui.setPresent(false);

				while(newRole.isActive){
					while(newRole.role.pickAndExecuteAnAction()){}
					try{
						stateChange.acquire();
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			}
		}
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
		print("Arrived at Destination");
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
		if(testMode){ return; }//}
		if(car != null){
			car.myGui.isPresent = true;
			gui.isPresent = false;

			//SWITCHES LOCATION WHY
			Position p = cityMap.getNearestStreet(currentLocation.getX(), currentLocation.getY());
			Position l = cityMap.getNearestStreet(loc.position.getX(), loc.position.getY());
			print ("origin position "+ currentLocation.getX() + " " + currentLocation.getY() + " "+p.getX() + " " + p.getY());
			print("goingto position "+loc.position.getX() + " " + loc.position.getY() + " " + l.getX() + " " + l.getY());

			print("gotoposition from person");
			going.drainPermits();
			car.gotoPosition(p.getX(), p.getY(), l.getX(), l.getY());
			try {
				going.acquire();
			} 
			catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			gui.DoGoTo(loc.getPosition()); 
		}
		else{ gui.DoGoTo(loc.getPosition()); }
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

	public String toString(){
		return name;
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


	public void crashed(){
		car = null; 
		print ("My car crashed and now I have no car :("); 
	}

	public List<SimEvent> getToDo() {
		return toDo;
	}

}
