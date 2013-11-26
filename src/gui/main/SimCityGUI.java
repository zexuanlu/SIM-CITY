package gui.main;

import gui.panels.*;

import javax.swing.*;

import person.Bank;
import person.Home;
import person.Market;
import person.Position;
import person.PersonAgent;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;
import person.gui.PersonGui;
import person.SimWorldClock;

import java.awt.*;

import agent.TimeCard;
import bank.*;
import bank.test.mock.MockBankHost;
import market.*;
import market.test.mock.MockCashier;
import person.Location;
import resident.HomeOwnerRole;
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
	
	List<BankTellerRole>banktellers = new ArrayList<BankTellerRole>();
	List<MarketEmployeeRole> marketemployeeroles = new ArrayList<MarketEmployeeRole>();
	public BankTellerRole banktellerrole1 = new BankTellerRole(initPerson,"BTR1");
	public BankTellerRole banktellerrole2 = new BankTellerRole(initPerson, "BTR2");
	public BankHostRole bankhostrole = new BankHostRole(initPerson,"BHR"); 
	public MarketCashierRole marketcashierrole = new MarketCashierRole(initPerson, "MCR"); 
	public MarketEmployeeRole marketemployeerole = new MarketEmployeeRole(initPerson, "MER"); 
	public HomeOwnerRole homeOwnerRole2 = new HomeOwnerRole(initPerson, "HMO1", 2);

	public CityMap citymap; 

	List<PersonAgent> people = new ArrayList<PersonAgent>();
	List<PersonGui> peoplegui = new ArrayList<PersonGui>();
	List<Location> locations = new ArrayList<Location>();

	int WINDOWX = 640; //60 across
	int WINDOWY = 480; //60 across
	int scale = 20; 
	int gridX = WINDOWX/scale; 
	int gridY = WINDOWY/scale;

	private String title = " SIM CITY 201 ";
	public static final int SCG_WIDTH = CityAnimationPanel.WIDTH + BuildingAnimationPanel.WIDTH;
	public static final int SCG_HEIGHT = CityAnimationPanel.HEIGHT + CityControlPanel.HEIGHT;

	public CityAnimationPanel cityAnimPanel = new CityAnimationPanel();
	BuildingAnimationPanel bldngAnimPanel = new BuildingAnimationPanel();
	CityControlPanel cityCtrlPanel = new CityControlPanel(this);

	///////////////////////////////////////////////////////////INITIALIZATION CODE FOR BUSSES	

	public Semaphore[][] grid = new Semaphore[gridX][gridY];
	BusRole bus = new BusRole("BusRole");
	BusRole bus2 = new BusRole("BusRole2");
	BusGui bgui2; 
	BusGui bgui;

	BusStopAgent busstop1 = new BusStopAgent("Stop1");
	BusStopAgent busstop2 = new BusStopAgent("Stop2");
	BusStopAgent busstop3 = new BusStopAgent("Stop3");
	BusStopAgent busstop4 = new BusStopAgent("Stop4");
	BusStopAgent busstop5 = new BusStopAgent("Stop5");
	BusStopAgent busstop6 = new BusStopAgent("Stop6");
	BusStopAgent busstop7 = new BusStopAgent("Stop7");
	BusStopAgent busstop8 = new BusStopAgent("Stop8");

	BusStopGui bs1gui = new BusStopGui(busstop1,80,200);	
	BusStopGui bs2gui = new BusStopGui(busstop2, 420, 200);
	BusStopGui bs3gui = new BusStopGui(busstop3, 500, 280);
	BusStopGui bs4gui = new BusStopGui(busstop4,180,280);
	BusStopGui bs5gui = new BusStopGui(busstop5, 260,60);
	BusStopGui bs6gui = new BusStopGui(busstop6, 340,140);
	BusStopGui bs7gui = new BusStopGui(busstop7, 340, 420);
	BusStopGui bs8gui = new BusStopGui(busstop8,260,340);	


	public SimCityGUI() {
		//here is where I initialize locations
		/**
		public BankTellerRole banktellerrole1 = new BankTellerRole(initPerson,"BTR1");
		public BankTellerRole banktellerrole2 = new BankTellerRole(initPerson, "BTR2");
		public BankHostRole bankhostrole = new BankHostRole(initPerson,"BHR"); 
		public MarketCashierRole marketcashierrole = new MarketCashierRole(initPerson, "MCR"); 
		public MarketEmployeeRole marketemployeerole = new MarketEmployeeRole(initPerson, "MER"); */

		banktellers.add(banktellerrole1); 
		banktellers.add(banktellerrole2);
		marketemployeeroles.add(marketemployeerole);
		
		
		Bank bank = new Bank("Banco Popular", new TimeCard(), bankhostrole, 
				new Position(140, 160), LocationType.Bank);
		Market market = new Market("Pokemart", marketcashierrole, new TimeCard(), 
				new Position(500, 60), LocationType.Market);
		Home home = new Home("Home 2", new HomeOwnerRole(initPerson, "Home Owner", 2), 
				new Position(350, 80), LocationType.Home);
		locations.add(bank);
		locations.add(market);
		locations.add(home);
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

		cityAnimPanel.BuildPanel = bldngAnimPanel;

		for (int i=0; i<gridX ; i++)
			for (int j = 0; j<gridY; j++)
				grid[i][j]=new Semaphore(1,true);
		//build the animation areas
		try {
			for (int y=0;y<30;y++){  //Create dead position
				grid[30][20].release();
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
	//	bus.msgStartBus();

		bus2.setBusMap(citymap);
		bus2.addtoRoute(busstop5.name);
		bus2.addtoRoute(busstop8.name);
		bus2.addtoRoute(busstop7.name);
		bus2.addtoRoute(busstop6.name);
		bus2.startThread();
		//bus2.msgStartBus();*/

		////////////////////////////////////////////////////////////////////////////////////INITIALIZATION FOR PEOPLE AND ROLES

		for (int i=0; i<1; i++){
			aStarTraversal = new AStarTraversal(grid);
			PersonAgent p = new PersonAgent("Person "+i,citymap,aStarTraversal);
			PersonGui pgui = new PersonGui(p);
			p.gui = pgui;
			people.add(p);
			peoplegui.add(pgui);
			cityAnimPanel.addGui(pgui);
		}
		people.get(0).addRole(banktellerrole1);
		//people.get(1).addRole(banktellerrole2);
		//people.get(2).addRole(bankhostrole);
		//people.get(3).addRole(marketcashierrole);
		//people.get(4).addRole(marketemployeerole);
		
        aStarTraversal = new AStarTraversal(grid);
        CarAgent caragent = new CarAgent(aStarTraversal);
        CarGui cgui = new CarGui(caragent,50,250);
        caragent.setGui(cgui);
        cityAnimPanel.addGui(cgui);
        caragent.startThread();
        caragent.gotoPosition(500,250);

		for (PersonAgent p: people){
			p.startThread();
		}
		 //people.get(0).startThread();
		/*for (PersonGui pgui: peoplegui){
			cityAnimPanel.addGui(pgui);     
		}*/


//		SimEvent goToBank = new SimEvent(bank, 1, 7, EventType.CustomerEvent);
//			p.setAnimationPanel(cityAnimPanel);
		// people.get(0).startThread();

		//SimEvent goToBank = new SimEvent(bank, 1, 7, EventType.CustomerEvent);
		//SimEvent goToMarket = new SimEvent(market, 1, 7, EventType.HomeOwnerEvent);
		SimEvent goHome = new SimEvent(home, 1, 7, EventType.HomeOwnerEvent);
		people.get(0).setAnimationPanel(cityAnimPanel);
		//people.get(0).toDo.offer(goToBank);
		//people.get(0).toDo.offer(goToMarket);
		people.get(0).toDo.offer(goHome);
		simclock = new SimWorldClock(7,people);
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
