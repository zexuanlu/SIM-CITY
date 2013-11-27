package gui.main;

import gui.panels.*;

import javax.swing.*;

import market.gui.MarketEmployeeGui;
import market.gui.MarketTruckGui; 
import person.Bank;
import person.Home;
import person.Market;
import person.PersonAgent.MyRole;
import person.Position;
import person.PersonAgent;
import person.Restaurant;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;
import person.gui.PersonGui;
import person.SimWorldClock;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import agent.Role;
import agent.TimeCard;
import bank.*;
import bank.gui.BankHostGui;
import bank.gui.BankTellerGui;
import bank.test.mock.MockBankHost;
import market.*;
import restaurant.*;
import restaurant.gui.CookGui;
import restaurant.gui.CustomerGui;
import restaurant.gui.WaiterGui;
import market.test.mock.MockCashier;
import person.Location;
import resident.HomeOwnerRole;
import resident.gui.HomeOwnerGui;
import simcity.BusRole;
import simcity.BusStopAgent;
import simcity.PassengerRole;
import simcity.CarAgent;
import simcity.CityMap;
import simcity.astar.*;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;
import simcity.gui.CarGui;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
* Main Sim City 201 GUI Frame
* This is where the 'main' function should be
*/

public class SimCityGUI extends JFrame {

	private PersonAgent initPerson; 
	public SimWorldClock simclock;
	
	public BankDatabaseAgent bankdatabase = new BankDatabaseAgent();
	
	public BankTellerRole banktellerrole1 = new BankTellerRole(initPerson,"BTR1");
	public BankTellerRole banktellerrole2 = new BankTellerRole(initPerson, "BTR2");
	public BankHostRole bankhostrole = new BankHostRole(initPerson,"BHR"); 
	
	public MarketCashierRole marketcashierrole = new MarketCashierRole(initPerson, "MCR"); 
	public MarketEmployeeRole marketemployeerole = new MarketEmployeeRole(initPerson, "MER");
	
	public HomeOwnerRole homeOwnerRole1 = new HomeOwnerRole(initPerson, "HMO1", 1);
	public HomeOwnerRole homeOwnerRole2 = new HomeOwnerRole(initPerson, "HMO2", 2);
	public HomeOwnerRole homeOwnerRole3 = new HomeOwnerRole(initPerson, "HMO3", 3);
	public HomeOwnerRole homeOwnerRole4 = new HomeOwnerRole(initPerson, "HMO4", 4);
	
	public Restaurant1HostRole host1 = new Restaurant1HostRole("Host 1", initPerson);
	public Restaurant1CookRole cook1 = new Restaurant1CookRole("Cook 1", initPerson);
	public Restaurant1CashierRole cashier1 = new Restaurant1CashierRole("Cashier 1", initPerson);
	public Restaurant1SDWaiterRole waiter1 = new Restaurant1SDWaiterRole("Waiter 1", initPerson);
	/*
	 * Role gui's must be initialized in SimCityGui with the role as happens below
	 */

	public BankTellerGui btg1 = new BankTellerGui(banktellerrole1);
	public BankTellerGui btg2 = new BankTellerGui(banktellerrole2);
	public BankHostGui bhg = new BankHostGui(bankhostrole);
	
	public MarketEmployeeGui meg = new MarketEmployeeGui(marketemployeerole);
	
	public WaiterGui wg = new WaiterGui(waiter1, null);
	public CookGui cg = new CookGui(cook1, null);
	
	public HomeOwnerGui hg1 = new HomeOwnerGui(homeOwnerRole1);
	public HomeOwnerGui hg2 = new HomeOwnerGui(homeOwnerRole2);
	public HomeOwnerGui hg3 = new HomeOwnerGui(homeOwnerRole3);
	public HomeOwnerGui hg4 = new HomeOwnerGui(homeOwnerRole4);
	
	public CityMap citymap; 

	public List<PersonAgent> people = new ArrayList<PersonAgent>();
	public List<PersonGui> peoplegui = new ArrayList<PersonGui>();
	public List<Location> locations = new ArrayList<Location>();

	public int WINDOWX = 640; //60 across
	public int WINDOWY = 480; //60 across
	public int scale = 20; 
	public int gridX = WINDOWX/scale; 
	public int gridY = WINDOWY/scale;

	private String title = " SIM CITY 201 ";
	public static final int SCG_WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int SCG_HEIGHT = CityAnimationPanel.HEIGHT + CityControlPanel.HEIGHT;

	public CityAnimationPanel cityAnimPanel = new CityAnimationPanel();
	public BuildingAnimationPanel bldngAnimPanel = new BuildingAnimationPanel();
	public CityControlPanel cityCtrlPanel = new CityControlPanel(this);

	///////////////////////////////////////////////////////////INITIALIZATION CODE FOR BUSSES	

	public Semaphore[][] grid = new Semaphore[gridX][gridY];
	public BusRole bus = new BusRole("BusRole");
	public BusRole bus2 = new BusRole("BusRole2");
	public BusGui bgui2; 
	public BusGui bgui;

	public BusStopAgent busstop1 = new BusStopAgent("Stop1");
	public BusStopAgent busstop2 = new BusStopAgent("Stop2");
	public BusStopAgent busstop3 = new BusStopAgent("Stop3");
	public BusStopAgent busstop4 = new BusStopAgent("Stop4");
	public BusStopAgent busstop5 = new BusStopAgent("Stop5");
	public BusStopAgent busstop6 = new BusStopAgent("Stop6");
	public BusStopAgent busstop7 = new BusStopAgent("Stop7");
	public BusStopAgent busstop8 = new BusStopAgent("Stop8");

	public BusStopGui bs1gui = new BusStopGui(busstop1,80,200);	
	public BusStopGui bs2gui = new BusStopGui(busstop2, 420, 200);
	public BusStopGui bs3gui = new BusStopGui(busstop3, 500, 280);
	public BusStopGui bs4gui = new BusStopGui(busstop4,180,280);
	public BusStopGui bs5gui = new BusStopGui(busstop5, 260,60);
	public BusStopGui bs6gui = new BusStopGui(busstop6, 340,140);
	public BusStopGui bs7gui = new BusStopGui(busstop7, 340, 420);
	public BusStopGui bs8gui = new BusStopGui(busstop8,260,340);	
	public MarketTruckAgent truck;


	public SimCityGUI() {
		//here is where I initialize locations
		/**
		public BankTellerRole banktellerrole1 = new BankTellerRole(initPerson,"BTR1");
		public BankTellerRole banktellerrole2 = new BankTellerRole(initPerson, "BTR2");
		public BankHostRole bankhostrole = new BankHostRole(initPerson,"BHR"); 
		public MarketCashierRole marketcashierrole = new MarketCashierRole(initPerson, "MCR"); 
		public MarketEmployeeRole marketemployeerole = new MarketEmployeeRole(initPerson, "MER"); */
		
		
		Bank bank = new Bank("Banco Popular", new TimeCard(), bankhostrole, 
				new Position(140, 160), LocationType.Bank);
		Market market = new Market("Pokemart", marketcashierrole, new TimeCard(), 
				new Position(220, 160), LocationType.Market);
		Home home1 = new Home("Home 1", homeOwnerRole1, 
				new Position(340, 160), 1, LocationType.Home);
		Home home2 = new Home("Home 2", homeOwnerRole2, 
				new Position(340, 80), 2, LocationType.Home);
		Home home3 = new Home("Home 3", homeOwnerRole3, 
				new Position(450, 160), 3, LocationType.Home);
		Home home4 = new Home("Home 4", homeOwnerRole4, 
				new Position(540, 160), 4, LocationType.Home);
		Restaurant rest1 = new Restaurant("Rest 1", host1, new TimeCard(), new Position(220, 80), LocationType.Restaurant);
		
		locations.add(bank);
		locations.add(market);
		locations.add(home1);
		locations.add(home2);
		locations.add(home3);
		locations.add(home4);
		locations.add(rest1);
		citymap = new CityMap(locations);

		// SETUP
		this.setTitle(title);
		this.setSize(SCG_WIDTH, SCG_HEIGHT);
		this.setLayout(new BorderLayout());
		this.setResizable(false);

		// ADD COMPONENTS
		this.add(cityAnimPanel, BorderLayout.WEST);
		this.add(bldngAnimPanel, BorderLayout.EAST);
		this.add(cityCtrlPanel, BorderLayout.SOUTH);

		cityAnimPanel.setBuildPanel(bldngAnimPanel);

		for (int i=0; i<gridX ; i++)
			for (int j = 0; j<gridY; j++)
				grid[i][j]=new Semaphore(1,true);
		//build the animation areas
		try {
			for (int y=0;y<30;y++){  //Create dead position
				grid[30][20].release();
			}
			for (int z = 0; z<20; z++){ //after creation needs this area to be able to navigate 
				for (int y = 11; y<14; y++){
					grid[0][y].release(); 
				}
			}
			for (int y =0 ;y<50; y++){ //creation location
				grid[0][12].release();
			}
			for (int y =0 ;y<50; y++){ //bank location
				grid[7][12].release();
			}
			
			for (int y = 0; y < 11; y ++){
				for (int x = 0; x < 14; x++){
					grid[x][y].acquire(); 
				}
				for (int x=17; x<gridX; x++){
					grid[x][y].acquire();
				}
			}

			for (int y = 14; y<gridY; y++){
				for (int x = 0; x < 14; x++){
					grid[x][y].acquire(); 
				}
				for (int x=17; x<gridX; x++){
					grid[x][y].acquire();
				}
			}
		}catch (Exception e) {
			System.out.println("Unexpected exception caught in during setup:"+ e);
		}
	

		bgui = new BusGui(bus,0,220);	
		bgui2 = new BusGui(bus2,280,00);
		bus.setGui(bgui);
		AStarTraversal aStarTraversal = new AStarTraversal(grid);
		bus.setAStar(aStarTraversal);

		bus2.setGui(bgui2);
		aStarTraversal = new AStarTraversal(grid);
		bus2.setAStar(aStarTraversal);

		busstop1.setGui(bs1gui);
		busstop2.setGui(bs2gui);
		busstop3.setGui(bs3gui);
		busstop4.setGui(bs4gui);
		busstop5.setGui(bs5gui);
		busstop6.setGui(bs6gui);
		busstop7.setGui(bs7gui);
		busstop8.setGui(bs8gui);

		busstop1.setDirection("down");
		busstop2.setDirection("down");
		busstop3.setDirection("up");
		busstop4.setDirection("up");
		busstop5.setDirection("right");
		busstop6.setDirection("left");
		busstop7.setDirection("left");
		busstop8.setDirection("right");

		citymap.addBusStop(busstop1.name, busstop1);
		citymap.addBusStop(busstop2.name, busstop2);
		citymap.addBusStop(busstop3.name, busstop3);
		citymap.addBusStop(busstop4.name, busstop4);
		citymap.addBusStop(busstop5.name, busstop5);
		citymap.addBusStop(busstop6.name, busstop6);
		citymap.addBusStop(busstop7.name, busstop7);
		citymap.addBusStop(busstop8.name, busstop8);
		
		//all these are along Bus 1's route so you have to add it to citymap
		citymap.addBus(busstop1, bus);
		citymap.addBus(busstop2, bus);
		citymap.addBus(busstop3, bus);
		citymap.addBus(busstop4, bus);
		citymap.addBus(busstop5, bus2);
		citymap.addBus(busstop6, bus2);
		citymap.addBus(busstop7, bus2);
		citymap.addBus(busstop8, bus2);

		cityAnimPanel.addGui(bgui);
		cityAnimPanel.addGui(bgui2);
		cityAnimPanel.addGui(bs1gui);
		cityAnimPanel.addGui(bs2gui);
		cityAnimPanel.addGui(bs3gui);
		cityAnimPanel.addGui(bs4gui);
		cityAnimPanel.addGui(bs5gui);
		cityAnimPanel.addGui(bs6gui);
		cityAnimPanel.addGui(bs7gui);
		cityAnimPanel.addGui(bs8gui);
		/*
		 * 1. For all "Job" roles the role's gui must be initialized and added to the role before 
		 * that pointer is given to people. Below the waiter1 role's gui is set and added to the right animation panel
		 * in cityAnimationPanel.
		 */
		
		truck = new MarketTruckAgent(aStarTraversal);
		MarketTruckGui truckGui = new MarketTruckGui(truck);
		truck.setGui(truckGui);
		truck.startThread();
		cityAnimPanel.addGui(truckGui);
		
		meg.isPresent = false;
		marketemployeerole.setGui(meg);
		cityAnimPanel.marketPanel.addGui(meg);
		
		wg.isPresent = false;
		waiter1.setGui(wg);
		cityAnimPanel.rest1Panel.addGui(wg);
		
		cg.isPresent = false;
		cook1.setGui(cg);
		cityAnimPanel.rest1Panel.addGui(cg);
		
		hg1.isPresent = false;
		homeOwnerRole1.setGui(hg1);
		cityAnimPanel.house1Panel.addGui(hg1);
		
		hg2.isPresent = false;
		homeOwnerRole2.setGui(hg2);
		cityAnimPanel.house2Panel.addGui(hg2);
		
		hg3.isPresent = false;
		homeOwnerRole3.setGui(hg3);
		cityAnimPanel.house3Panel.addGui(hg3);
		
		hg4.isPresent = false;
		homeOwnerRole4.setGui(hg4);
		cityAnimPanel.house4Panel.addGui(hg4);
		
		btg1.isPresent = false;
		banktellerrole1.setGui(btg1);
		cityAnimPanel.bankPanel.addGui(btg1);
		
		btg2.isPresent = false;
		banktellerrole2.setGui(btg2);
		cityAnimPanel.bankPanel.addGui(btg2);
		
		bhg.isPresent = false;
		bankhostrole.setGui(bhg);
		cityAnimPanel.bankPanel.addGui(bhg);
		
		busstop1.startThread();
		busstop2.startThread();
		busstop3.startThread();
		busstop4.startThread();
		busstop5.startThread();
		busstop6.startThread();
		busstop7.startThread();
		busstop8.startThread();

		bus.setBusMap(citymap);
		bus.addtoRoute(busstop1.name);
		bus.addtoRoute(busstop2.name);
		bus.addtoRoute(busstop3.name);
		bus.addtoRoute(busstop4.name);
		bus.startThread();
		bus.msgStartBus();

		bus2.setBusMap(citymap);
		bus2.addtoRoute(busstop5.name);
		bus2.addtoRoute(busstop8.name);
		bus2.addtoRoute(busstop7.name);
		bus2.addtoRoute(busstop6.name);
		bus2.startThread();
		bus2.msgStartBus();

		////////////////////////////////////////////////////////////////////////////////////INITIALIZATION FOR PEOPLE AND ROLES

		/*
		 * Adds the people with a given house number. This is for homes. Any dynamically added person will
		 * be added as an apartment tenant.
		 */

//		for (int i=1; i<6; i++){
		for (int i=0; i<21; i++){
			aStarTraversal = new AStarTraversal(grid);
			PersonAgent p = new PersonAgent("Person "+i, citymap, aStarTraversal, 2000);
			PersonGui pgui = new PersonGui(p);
			p.gui = pgui;
			p.homeNumber = i;
			people.add(p);
			peoplegui.add(pgui);
			cityAnimPanel.addGui(pgui);
			p.setAnimationPanel(cityAnimPanel);
			p.setcitygui(this);
		}
		
		people.get(0).addRole(bankhostrole);
		people.get(0).addRole(homeOwnerRole1);
		people.get(1).addRole(bankhostrole);
		people.get(1).addRole(homeOwnerRole2);
		people.get(2).addRole(banktellerrole1);
		people.get(2).addRole(homeOwnerRole3);
		people.get(3).addRole(banktellerrole1);
		people.get(3).addRole(homeOwnerRole4);
		people.get(4).addRole(banktellerrole2);
		people.get(5).addRole(banktellerrole2);
		people.get(6).addRole(marketemployeerole);
		people.get(7).addRole(marketemployeerole);
		people.get(8).addRole(marketcashierrole);
		people.get(9).addRole(marketcashierrole);
		people.get(10).addRole(cashier1);
		people.get(11).addRole(cashier1);
		people.get(12).addRole(cook1);
		people.get(13).addRole(cook1);
		people.get(14).addRole(waiter1);
		people.get(15).addRole(waiter1);
		people.get(16).addRole(host1);
		people.get(17).addRole(host1);
		
		rest1.getTimeCard().startThread();
		market.getTimeCard().startThread();
		bank.getTimeCard().startThread();
		bankdatabase.startThread();
		
		// Starts the thread of all the people
		for (PersonAgent p: people){
			p.startThread();
		}
		
		 //people.get(0).startThread();
		//people.get(0).setAnimationPanel(cityAnimPanel);
		/* 
		 * Every person added to SimCity must have at least one SimEvent to do much of anything
		 * SimEvent constructor goes like : Location, priority, start time, EventType
		 * Locations will have been pre-made because they must be added to the CityMap (above)
		 * so use these premade locations (Lines: 132 - 138)
		 * 
		 * Creating host, cook, cashier, waiter, and teller events
		 * */
		SimEvent hostGoToRestaurant = new SimEvent(rest1, 1, 8, EventType.HostEvent);
		SimEvent hostGoToRestaurant2 = new SimEvent(rest1, 1, 14, EventType.HostEvent);
		SimEvent cookGoToRestaurant = new SimEvent(rest1, 1, 8, EventType.CookEvent);
		SimEvent cookGoToRestaurant2 = new SimEvent(rest1, 1, 14, EventType.CookEvent);
		SimEvent cashierGoToRestaurant = new SimEvent(rest1, 1, 8, EventType.CashierEvent);
		SimEvent cashierGoToRestaurant2 = new SimEvent(rest1, 1, 14, EventType.CashierEvent);
		SimEvent waiterGoToRestaurant = new SimEvent(rest1, 1, 8, EventType.WaiterEvent);
		SimEvent waiterGoToRestaurant2 = new SimEvent(rest1, 1, 14, EventType.WaiterEvent);
		SimEvent tellerGoToBank = new SimEvent(bank, 1, 8, EventType.TellerEvent);
		SimEvent tellerGoToBank2 = new SimEvent(bank, 1, 14, EventType.TellerEvent);
		SimEvent hostGoToBank = new SimEvent(bank, 1, 8, EventType.HostEvent);
		SimEvent hostGoToBank2 = new SimEvent(bank, 1, 14, EventType.HostEvent);
		SimEvent employeeGoToMarket = new SimEvent(market, 1, 8, EventType.EmployeeEvent);
		SimEvent employeeGoToMarket2 = new SimEvent(market, 1, 14, EventType.EmployeeEvent);
		SimEvent cashierGoToMarket = new SimEvent(market, 1, 8, EventType.CashierEvent);
		SimEvent cashierGoToMarket2 = new SimEvent(market, 1, 14, EventType.CashierEvent);
		
		SimEvent goHome1 = new SimEvent(home1, 1, 7, EventType.HomeOwnerEvent);
		SimEvent goHome2 = new SimEvent(home2, 1, 7, EventType.HomeOwnerEvent);
		SimEvent goHome3 = new SimEvent(home3, 1, 7, EventType.HomeOwnerEvent);
		SimEvent goHome4 = new SimEvent(home4, 1, 7, EventType.HomeOwnerEvent);
		
		SimEvent goToBank = new SimEvent(bank, 1, 7, EventType.CustomerEvent);
		SimEvent goToBank2 = new SimEvent(bank, 1, 9, EventType.CustomerEvent);
		goToBank.directive = "deposit";
		goToBank2.directive = "deposit";
//		SimEvent gotoMarket = new SimEvent(market, 1,7, EventType.CustomerEvent);
//		SimEvent gotoRestaurant = new SimEvent(rest1, 1,7, EventType.CustomerEvent);
//		SimEvent goToRestaurant = new SimEvent(rest1, 1, 7, EventType.WaiterEvent);
//		SimEvent goToHostRest = new SimEvent(rest1, 1, 7, EventType.HostEvent);
		
		/*
		 * 2. Since the role is initialized with the initPerson who is null 
		 * you must switch the person pointer in that role to be the first person to play the role
		 * 
		 * 
		 * Ex: people.get(5).roles.get(0).role.switchPerson(people.get(5));
		 * 
		 * The above = get the first (and only at the moment) role from person 5's role list and call switch person
		 * replacing the initPerson with person 5 (the first person to play the role of waiter1)
		 */

//		for(PersonAgent p : people){
//			for(MyRole r : p.roles){
//				r.role.switchPerson(p);
//			}
//		}
//		people.get(0).roles.get(0).role.switchPerson(people.get(0));
//		people.get(2).roles.get(0).role.switchPerson(people.get(2));
//		people.get(4).roles.get(0).role.switchPerson(people.get(4));
//		people.get(6).roles.get(0).role.switchPerson(people.get(6));

		for(int i = 0; i < people.size(); i = (i+2)){
			for(int j = 0; j < people.get(i).roles.size(); j++){
				people.get(i).roles.get(j).role.switchPerson(people.get(i));
			}
		}

		/**
		people.get(0).roles.get(0).role.switchPerson(people.get(0));
		people.get(0).roles.get(1).role.switchPerson(people.get(0));
		
		people.get(1).roles.get(0).role.switchPerson(people.get(1));
		people.get(1).roles.get(1).role.switchPerson(people.get(1));
		
		people.get(2).roles.get(0).role.switchPerson(people.get(2));
		people.get(2).roles.get(1).role.switchPerson(people.get(2));
		
		people.get(3).roles.get(0).role.switchPerson(people.get(3));
		people.get(3).roles.get(1).role.switchPerson(people.get(3));
		
		people.get(4).roles.get(0).role.switchPerson(people.get(4));
		
		//people.get(4).roles.get(0).role.switchPerson(people.get(4));
		
		/*
		 * 3. Anyone who needs to know about other job roles in the building (like the host) must be notified of each role
		 * this was done in the restaurant automatically via the gui be we have to add waiters to the host's lit of 
		 * waiters manually. It is likewise for bankhosts and tellers and the market cashier and employees
		 *
		 * Below is an example of how to do so. The cast is necessary since roles.role is a Role type and we need a specific 
		 * child class of Role.
		 */
		//((Restaurant1HostRole)people.get(0).roles.get(0).role).msgaddwaiter(waiter1);
		//((Restaurant1HostRole)people.get(0).roles.get(0).role).msgaddwaiter(waiter1);
		bankhostrole.addTeller(banktellerrole1);
		bankhostrole.addTeller(banktellerrole2);
		banktellerrole1.bh = bankhostrole;
		banktellerrole2.bh = bankhostrole;
		banktellerrole1.bd = bankdatabase;
		banktellerrole2.bd = bankdatabase;
		
		waiter1.setcook(cook1);
		waiter1.sethost(host1);
		waiter1.setRevolvingStand(cook1.getRevStand());
		host1.msgaddwaiter(waiter1);
		waiter1.setCashier(cashier1);
		
		/*
		 * toDO.offer(e) adds the SimEvent to the person's list and gives him/her purpose in SimCity
		 * Host, cook, cashier, waiter and teller events
		 */
		people.get(0).toDo.offer(hostGoToBank);
		people.get(1).toDo.offer(hostGoToBank2);
		people.get(2).toDo.offer(tellerGoToBank);
		people.get(3).toDo.offer(tellerGoToBank2);
		people.get(4).toDo.offer(tellerGoToBank);
		people.get(5).toDo.offer(tellerGoToBank2);
		people.get(6).toDo.offer(employeeGoToMarket);
		people.get(7).toDo.offer(employeeGoToMarket2);
		people.get(8).toDo.offer(cashierGoToMarket);
		people.get(9).toDo.offer(cashierGoToMarket2);
		people.get(10).toDo.offer(cashierGoToRestaurant);
		people.get(11).toDo.offer(cashierGoToRestaurant2);
		people.get(12).toDo.offer(cookGoToRestaurant);
		people.get(13).toDo.offer(cookGoToRestaurant2);
		people.get(14).toDo.offer(waiterGoToRestaurant);
		people.get(15).toDo.offer(waiterGoToRestaurant2);
		people.get(16).toDo.offer(hostGoToRestaurant);
		people.get(17).toDo.offer(hostGoToRestaurant2);
		
		truck.setCashier(marketcashierrole);
		marketcashierrole.addTruck(truck);
		/*Create the SimWorldClock with the starting time and the list of people*/
		simclock = new SimWorldClock(8,people);
		

		/*for(int i = 0; i < people.size(); i++){
			people.get(i).setAnimationPanel(cityAnimPanel);
		}*/
		//people.get(0).toDo.offer(goToBank);
		//people.get(0).toDo.offer(goToBank);
	}

	public CarAgent createCar(PersonAgent p){
		AStarTraversal aStarTraversal = new AStarTraversal(grid);
        CarAgent caragent = new CarAgent(aStarTraversal, p);
        CarGui cgui = new CarGui(caragent,600,400);
        caragent.setGui(cgui);
        cityAnimPanel.addGui(cgui);
        caragent.startThread();
        return caragent; 
	}
	
	public void addPerson(PersonAgent p) {
		PersonGui pgui = new PersonGui(p);
		p.gui = pgui;
		p.setAnimationPanel(cityAnimPanel);
		people.add(p);
		peoplegui.add(pgui);
		cityAnimPanel.addGui(pgui);
	}


	public static void main(String[] args){
		SimCityGUI scg = new SimCityGUI();
		scg.setVisible(true);
		scg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
