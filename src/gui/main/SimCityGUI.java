package gui.main;

import gui.panels.*;
import utilities.TrafficLightAgent; 

import javax.swing.*;

import market.gui.MarketEmployeeGui;
import market.gui.MarketTruckGui; 
import person.Apartment;
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
import bank.*;
import bank.gui.BankHostGui;
import bank.gui.BankTellerGui;
import market.*;
import restaurant1.*;
import restaurant1.gui.CookGui;
import restaurant1.gui.WaiterGui;
import restaurant3.*;
import restaurant3.gui.Restaurant3CookGui;
import restaurant3.gui.Restaurant3CustomerGui;
import restaurant3.gui.Restaurant3WaiterGui;
import restaurant4.*;
import person.Location;
import resident.ApartmentLandlordRole;
import resident.ApartmentTenantRole;
import resident.HomeOwnerRole;
import resident.gui.ApartmentTenantGui;
import resident.gui.HomeOwnerGui;
import simcity.BusRole;
import simcity.BusStopAgent;
import simcity.CarAgent;
import simcity.CityMap;
import simcity.astar.*;
import simcity.gui.BusGui;
import simcity.gui.BusStopGui;
import simcity.gui.CarGui;
import utilities.TimeCard;

import java.util.ArrayList;
import java.util.Collections;
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
        
        public ApartmentLandlordRole landlord = new ApartmentLandlordRole("APT5", 5, initPerson);

        // List of apartment tenant roles
        private List<ApartmentTenantRole> aptTenants;
        public TrafficLightAgent trafficlightagent = new TrafficLightAgent(); 
        // List of apartment tenant GUIs
        private List<ApartmentTenantGui> aptGuis;
        
        public Restaurant1HostRole host1 = new Restaurant1HostRole("Host 1", initPerson);
        public Restaurant1CookRole cook1 = new Restaurant1CookRole("Cook 1", initPerson);
        public Restaurant1CustomerRole cust1 = new Restaurant1CustomerRole("Customer 1", initPerson);
        public Restaurant1CashierRole cashier1 = new Restaurant1CashierRole("Cashier 1", initPerson);
        public Restaurant1SDWaiterRole waiter1 = new Restaurant1SDWaiterRole("Waiter 1", initPerson);
        
        //RESTAURANT 3 ROLES
        public Restaurant3HostRole host3 = new Restaurant3HostRole("Host 3", initPerson);
        public Restaurant3CookRole cook3 = new Restaurant3CookRole("Cook 3", initPerson);
        public Restaurant3CashierRole cashier3 = new Restaurant3CashierRole("Cashier 3", initPerson);
        public Restaurant3CustomerRole customer3 = new Restaurant3CustomerRole("Customer 3", initPerson);
        public Restaurant3WaiterRole waiter3 = new Restaurant3WaiterRole("Waiter 3", initPerson);
        public Restaurant3SDWaiterRole sdwaiter3 = new Restaurant3SDWaiterRole("SDWaiter 3", initPerson);
        
        public Restaurant4HostRole host4 = new Restaurant4HostRole("Host 4", initPerson);
        public Restaurant4CookRole cook4 = new Restaurant4CookRole("Cook 4", initPerson);
        public Restaurant4CashierRole cashier4 = new Restaurant4CashierRole("Cashier 4", initPerson);
        /*
         * Role gui's must be initialized in SimCityGui with the role as happens below
         */

        public BankTellerGui btg1 = new BankTellerGui(banktellerrole1);
        public BankTellerGui btg2 = new BankTellerGui(banktellerrole2);
        public BankHostGui bhg = new BankHostGui(bankhostrole);
        
        public MarketEmployeeGui meg = new MarketEmployeeGui(marketemployeerole);
        
        //Restaurant 1 GUIs
        public WaiterGui wg = new WaiterGui(waiter1);
        public CookGui cg = new CookGui(cook1, null);
        
        //Restaurant 3 GUIs
        public Restaurant3CookGui ck3g = new Restaurant3CookGui(cook3);
        public Restaurant3WaiterGui w3g = new Restaurant3WaiterGui(waiter3);
        public Restaurant3WaiterGui sdw3g = new Restaurant3WaiterGui(sdwaiter3);
        
        public HomeOwnerGui hg1 = new HomeOwnerGui(homeOwnerRole1);
        public HomeOwnerGui hg2 = new HomeOwnerGui(homeOwnerRole2);
        public HomeOwnerGui hg3 = new HomeOwnerGui(homeOwnerRole3);
        public HomeOwnerGui hg4 = new HomeOwnerGui(homeOwnerRole4);
        
        public CityMap citymap; 

        public List<PersonAgent> people = new ArrayList<PersonAgent>();
        public List<PersonGui> peoplegui = new ArrayList<PersonGui>();
        public List<Location> locations = new ArrayList<Location>();

        public int WINDOWX = 740; 
        public int WINDOWY = 480; 
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

    	public Semaphore[][]origgrid = new Semaphore[gridX][gridY];
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

        public BusStopGui bs1gui = new BusStopGui(busstop1, 30, 170);        
        public BusStopGui bs2gui = new BusStopGui(busstop2, 700, 170);
        public BusStopGui bs3gui = new BusStopGui(busstop3, 700, 280);
        public BusStopGui bs4gui = new BusStopGui(busstop4, 30, 280);
        public BusStopGui bs5gui = new BusStopGui(busstop5, 330, 30);
        public BusStopGui bs6gui = new BusStopGui(busstop6, 440, 30);
        public BusStopGui bs7gui = new BusStopGui(busstop7, 440, 420);
        public BusStopGui bs8gui = new BusStopGui(busstop8, 330, 420);        
        public MarketTruckAgent truck;

        public SimCityGUI() {
                
        		trafficlightagent.startThread();
        	
                // List of apartment tenant roles
                aptTenants = new ArrayList<ApartmentTenantRole>();
                
                for (int i = 5; i < 23; ++i) {
                        aptTenants.add(new ApartmentTenantRole("APT"+i, i, initPerson));
                }
                
                // List of apartment tenant GUIs
                aptGuis = new ArrayList<ApartmentTenantGui>();
                
                for (ApartmentTenantRole r : aptTenants) {
                        aptGuis.add(new ApartmentTenantGui(r));
                }
                
                // First quadrant locations

                Bank bank = new Bank("Banco Popular", new TimeCard(), bankhostrole, 
                                new Position(120, 170), LocationType.Bank);
                Market market = new Market("Pokemart", marketcashierrole, new TimeCard(),           		
                                new Position(180, 170), LocationType.Market);
                Restaurant rest1 = new Restaurant("Rest 1", host1, new TimeCard(), new Position(240, 170), LocationType.Restaurant1);
                Restaurant rest2 = new Restaurant("Rest 2", host1, new TimeCard(), new Position(320, 170), LocationType.Restaurant2);
                Restaurant rest3 = new Restaurant("Rest 3", host1, new TimeCard(), new Position(330, 100), LocationType.Restaurant3);
                
                // Second quadrant locations
                Bank bank2 = new Bank("Banco Popular 2", new TimeCard(), bankhostrole, 
                                new Position(660, 170), LocationType.Bank);
                Market market2 = new Market("Pokemart 2", marketcashierrole, new TimeCard(), 
                                new Position(460, 170), LocationType.Market);
                // FIX HOST ROLES
                Restaurant rest4 = new Restaurant("Rest 4", host4, new TimeCard(), new Position(520, 170), LocationType.Restaurant4);
                Restaurant rest5 = new Restaurant("Rest 5", host1, new TimeCard(), new Position(600, 170), LocationType.Restaurant2);
                Restaurant rest6 = new Restaurant("Rest 6", host1, new TimeCard(), new Position(450, 100), LocationType.Restaurant3);                
                
                // Third quadrant locations
                Home home1 = new Home("Home 1", homeOwnerRole1, 
                                new Position(460, 280), 1, LocationType.Home);
                Home home2 = new Home("Home 2", homeOwnerRole2, 
                                new Position(440, 380), 2, LocationType.Home);
                Home home3 = new Home("Home 3", homeOwnerRole3, 
                                new Position(520, 280), 3, LocationType.Home);
                Home home4 = new Home("Home 4", homeOwnerRole4, 
                                new Position(600, 280), 4, LocationType.Home);
                // FIX
                Home home5 = new Home("Home 5", homeOwnerRole4, 
                                new Position(660, 280), 4, LocationType.Home);
                
                // Fourth quadrant locations
                // First apartment complex
                Apartment apt1 = new Apartment("Apartment 1", aptTenants.get(0), 
                                new Position(80, 280), 5, LocationType.Apartment);
                Apartment apt2 = new Apartment("Apartment 2", aptTenants.get(1), 
                                new Position(80, 280), 6, LocationType.Apartment);
                Apartment apt3 = new Apartment("Apartment 3", aptTenants.get(2), 
                                new Position(80, 280), 7, LocationType.Apartment);
                Apartment apt4 = new Apartment("Apartment 4", aptTenants.get(3), 
                                new Position(80, 280), 8, LocationType.Apartment);
                
                
                Apartment apt5 = new Apartment("Apartment 5", aptTenants.get(4), 
                                new Position(80, 280), 9, LocationType.Apartment);
                Apartment apt6 = new Apartment("Apartment 6", aptTenants.get(5), 
                                new Position(80, 280), 10, LocationType.Apartment);
                Apartment apt7 = new Apartment("Apartment 7", aptTenants.get(6), 
                                new Position(80, 280), 11, LocationType.Apartment);
                Apartment apt8 = new Apartment("Apartment 8", aptTenants.get(7), 
                                new Position(80, 280), 12, LocationType.Apartment);
                Apartment apt9 = new Apartment("Apartment 9", aptTenants.get(8), 
                                new Position(80, 280), 13, LocationType.Apartment);
                
                
                Apartment apt10 = new Apartment("Apartment 10", aptTenants.get(10), 
                                new Position(80, 280), 14, LocationType.Apartment);
                Apartment apt11 = new Apartment("Apartment 11", aptTenants.get(11), 
                                new Position(80, 280), 15, LocationType.Apartment);
                Apartment apt12 = new Apartment("Apartment 12", aptTenants.get(12), 
                                new Position(80, 280), 16, LocationType.Apartment);
                Apartment apt13 = new Apartment("Apartment 13", aptTenants.get(13), 
                                new Position(80, 280), 17, LocationType.Apartment);
                Apartment apt14 = new Apartment("Apartment 14", aptTenants.get(14), 
                                new Position(80, 280), 18, LocationType.Apartment);
                Apartment apt15 = new Apartment("Apartment 15", aptTenants.get(15), 
                                new Position(80, 280), 19, LocationType.Apartment);
                Apartment apt16 = new Apartment("Apartment 16", aptTenants.get(16), 
                                new Position(80, 280), 20, LocationType.Apartment);
                
                // Second apartment complex
                Apartment apt17 = new Apartment("Apartment 17", aptTenants.get(17), 
                                new Position(160, 280), 21, LocationType.Apartment);
                
                
                rest1.setCashier(cashier1);
                rest1.setCook(cook1);
                rest3.setCashier(cashier3);
                rest3.setCook(cook3);
                rest4.setCashier(cashier4);
                rest4.setCook(cook4);
                
                locations.add(bank);
                locations.add(bank2);
                locations.add(market);
                locations.add(market2);
                locations.add(home1);
                locations.add(home2);
                locations.add(home3);
                locations.add(home4);
                locations.add(apt1);
                locations.add(apt2);
                locations.add(apt3);
                locations.add(apt4);
                locations.add(apt5);
                locations.add(apt6);
                locations.add(apt7);
                locations.add(apt8);
                locations.add(apt9);
                locations.add(apt10);
                locations.add(apt11);
                locations.add(apt12);
                locations.add(apt13);
                locations.add(apt14);
                locations.add(apt15);
                locations.add(apt16);
                locations.add(apt17);
                locations.add(rest1);
                locations.add(rest2);
                locations.add(rest3);
                locations.add(rest4);
                locations.add(rest5);
                locations.add(rest6);
                
                for(Location location : locations){
                        cityAnimPanel.addLocation(location);
                }
                
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
        			for (int j = 0; j<gridY; j++){
        				grid[i][j]=new Semaphore(1,true);
        				origgrid[i][j]= new Semaphore(1,true);
        			}
        		//build the animation areas
        		try {
        			for (int y=0;y<30;y++){  //Create dead position
        				grid[36][23].release();
        				origgrid[36][23].release();
        			}
        			for (int x=0;x<17;x++){
        				for (int y = 0; y<9; y++){
        					grid[x][y].acquire();
        					origgrid[x][y].acquire();
        				}
        				
        				for (int y = 14; y<gridY;y++){
        					grid[x][y].acquire();
        					origgrid[x][y].acquire();
        				}
        			}
        			
        			for (int x=22; x<gridX;x++){
        				for (int y=0;y<9;y++){
        					origgrid[x][y].acquire();
        					grid[x][y].acquire();
        				}
        				for (int y=14;y<gridY;y++){
        					origgrid[x][y].acquire();
        					grid[x][y].acquire();
        				}	
                        }
                }catch (Exception e) {
                        System.out.println("Unexpected exception caught in during setup:"+ e);
                }
        

                bgui = new BusGui(bus,0,220);        
                bgui2 = new BusGui(bus2,380,00);
                bus.setGui(bgui);
                AStarTraversal aStarTraversal = new AStarTraversal(grid);
        		aStarTraversal.originalgrid = origgrid; 

                bus.setAStar(aStarTraversal);

                bus2.setGui(bgui2);
                aStarTraversal = new AStarTraversal(grid);
        		aStarTraversal.originalgrid = origgrid; 

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
                

                truck = new MarketTruckAgent(1);
                MarketTruckGui truckGui = new MarketTruckGui(truck, bldngAnimPanel,1);
                truck.setGui(truckGui);
                truck.startThread();
                cityAnimPanel.addGui(truckGui);
                
                meg.isPresent = false;
                marketemployeerole.setGui(meg);
                cityAnimPanel.marketPanel.addGui(meg);
                
                //SETTING RESTAURANT 1 GUIS
                //Rest 1 waiter gui
                wg.isPresent = false;
                waiter1.setGui(wg);
                cityAnimPanel.rest1Panel.addGui(wg);
                
                //Rest 1 cook gui
                cg.isPresent = false;
                cook1.setGui(cg);
                cityAnimPanel.rest1Panel.addGui(cg);
                
                //SETTING RESTAURANT 3 GUIS
                //Rest 3 waiter gui
                waiter3.setGui(w3g);
                cityAnimPanel.rest3Panel.addGui(w3g);
                
                sdwaiter3.setGui(sdw3g);
                cityAnimPanel.rest3Panel.addGui(sdw3g);
                
                //Rest 3 cook gui
                cook3.setGui(ck3g);
                cityAnimPanel.rest3Panel.addGui(ck3g);
                
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
                
                // Loops through the apartment guis to add them to their animation panels
                for (ApartmentTenantGui aptGui : aptGuis) {
                        aptGui.isPresent = false;
                        cityAnimPanel.apartments.get(aptGuis.indexOf(aptGui)).addGui(aptGui);
                }
                
                // Loops through apartment tenant roles and sets to respective GUI
                for (ApartmentTenantRole r : aptTenants) {
                        r.setGui(aptGuis.get(aptTenants.indexOf(r)));
                }
                
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
                bus.addtoRoute(busstop4.name);
                bus.addtoRoute(busstop3.name);
                bus.addtoRoute(busstop2.name);
                bus.addtoRoute(busstop1.name);
                bus.setTrafficLightAgent(trafficlightagent);
                bus.startThread();
                bus.msgStartBus();

                bus2.setBusMap(citymap);
                bus2.addtoRoute(busstop5.name);
                bus2.addtoRoute(busstop8.name);
                bus2.addtoRoute(busstop7.name);
                bus2.addtoRoute(busstop6.name);
                bus2.setTrafficLightAgent(trafficlightagent);
                bus2.startThread();
                bus2.msgStartBus();

                ////////////////////////////////////////////////////////////////////////////////////INITIALIZATION FOR PEOPLE AND ROLES
                /*
                 * Adds the people with a given house number. This is for homes. Any dynamically added person will
                 * be added as an apartment tenant.
                 */

//                for (int i=1; i<6; i++){
                int x = 40; 
                int y = 330; 
                for (int i=0; i<22; i++){
                        aStarTraversal = new AStarTraversal(grid);
                		aStarTraversal.originalgrid = origgrid; 
                        PersonAgent p  = new PersonAgent("Person "+i, citymap, aStarTraversal, 500.00);
                        PersonGui pgui = new PersonGui(p, x, y);
                        x = x+40; 
                        p.setGui(pgui);
                        System.out.println(""+i);
                        if(i < 21){
                                p.gui.setStart(citymap.getHome(i+1).position.getX(), citymap.getHome(i+1).position.getY());
                                p.homeNumber = i+1;
                        }
                        else
                        p.homeNumber = i;
                        people.add(p);
                        peoplegui.add(pgui);
                        cityAnimPanel.addGui(pgui);
                        p.setAnimationPanel(cityAnimPanel);
                        //p.setcitygui(this);
                }
                
                for (int i=6;i<22;i++){
                        people.get(i).msgAddMoney(-200);
                }
                
                people.get(0).addRole(bankhostrole, "Bank Host");
                people.get(0).roles.get(0).role.switchPerson(people.get(0));
                people.get(0).addRole(homeOwnerRole1, "Home Owner");
                people.get(0).roles.get(1).role.switchPerson(people.get(0));
                people.get(1).addRole(bankhostrole, "Bank Host");
                people.get(1).addRole(homeOwnerRole2, "Home Owner");
                people.get(1).roles.get(1).role.switchPerson(people.get(1));
                people.get(2).addRole(banktellerrole1, "Bank Teller");
                people.get(2).roles.get(0).role.switchPerson(people.get(2));
                people.get(2).addRole(homeOwnerRole3, "Home Owner");
                people.get(2).roles.get(1).role.switchPerson(people.get(2));
                people.get(3).addRole(banktellerrole1, "Bank Teller");
                people.get(3).addRole(homeOwnerRole4, "Home Owner");
                people.get(3).roles.get(1).role.switchPerson(people.get(3));
                people.get(4).addRole(banktellerrole2, "Bank Teller");
                people.get(4).roles.get(0).role.switchPerson(people.get(4));

                people.get(4).addRole(aptTenants.get(0), "Apt Tenant");
                people.get(4).roles.get(1).role.switchPerson(people.get(4));
                people.get(4).addRole(landlord, "Apt Landlord");
                people.get(4).roles.get(2).role.switchPerson(people.get(4));
                people.get(5).addRole(banktellerrole2, "Bank Teller");
                people.get(5).addRole(aptTenants.get(1), "Apt Tenant");
                people.get(5).roles.get(1).role.switchPerson(people.get(5));
                people.get(6).addRole(marketemployeerole, "Market Employee");
                people.get(6).roles.get(0).role.switchPerson(people.get(6));
                people.get(6).addRole(aptTenants.get(2), "Apt Tenant");
                people.get(6).roles.get(1).role.switchPerson(people.get(6));
                people.get(7).addRole(marketemployeerole, "Market Employee");
                people.get(7).addRole(aptTenants.get(3), "Apt Tenant");
                people.get(7).roles.get(1).role.switchPerson(people.get(7));
                people.get(8).addRole(marketcashierrole, "Market Cashier");
                people.get(8).roles.get(0).role.switchPerson(people.get(8));
                people.get(8).addRole(aptTenants.get(4), "Apt Tenant");
                people.get(8).roles.get(1).role.switchPerson(people.get(8));
                people.get(9).addRole(marketcashierrole, "Market Cashier");
                people.get(9).addRole(aptTenants.get(5), "Apt Tenant");
                people.get(9).roles.get(1).role.switchPerson(people.get(9));
                people.get(10).addRole(cashier1, "Rest 1 Cashier");
                people.get(10).roles.get(0).role.switchPerson(people.get(10));
                people.get(10).addRole(aptTenants.get(6), "Apt Tenant");
                people.get(10).roles.get(1).role.switchPerson(people.get(10));
                people.get(11).addRole(cashier1, "Rest 1 Cashier");
                people.get(11).addRole(aptTenants.get(7), "Apt Tenant");
                people.get(11).roles.get(1).role.switchPerson(people.get(11));
                people.get(12).addRole(cook1, "Rest 1 Cook");
                people.get(12).roles.get(0).role.switchPerson(people.get(12));
                people.get(12).addRole(aptTenants.get(8), "Apt Tenant");
                people.get(12).roles.get(1).role.switchPerson(people.get(12));
                people.get(13).addRole(cook1, "Rest 1 Cook");
                people.get(13).addRole(aptTenants.get(9), "Apt Tenant");
                people.get(13).roles.get(1).role.switchPerson(people.get(13));
                people.get(14).addRole(waiter1, "Rest 1 Waiter");
                people.get(14).roles.get(0).role.switchPerson(people.get(14));
                people.get(14).addRole(aptTenants.get(10), "Apt Tenant");
                people.get(14).roles.get(1).role.switchPerson(people.get(14));
                people.get(15).addRole(waiter1, "Rest 1 Waiter");
                people.get(15).addRole(aptTenants.get(11), "Apt Tenant");
                people.get(15).roles.get(1).role.switchPerson(people.get(15));
                people.get(16).addRole(host1, "Rest 1 Host");
                people.get(16).roles.get(0).role.switchPerson(people.get(16));
                people.get(16).addRole(aptTenants.get(12), "Apt Tenant");
                people.get(16).roles.get(1).role.switchPerson(people.get(16));
                people.get(17).addRole(host1, "Rest 1 Host");
                people.get(17).addRole(aptTenants.get(13), "Apt Tenant");
                people.get(17).roles.get(1).role.switchPerson(people.get(17));
                people.get(18).addRole(aptTenants.get(14), "Apt Tenant");
                people.get(18).roles.get(0).role.switchPerson(people.get(18));
                people.get(19).addRole(aptTenants.get(15), "Apt Tenant");
                people.get(19).roles.get(0).role.switchPerson(people.get(19));
                people.get(20).addRole(aptTenants.get(16), "Apt Tenant");
                people.get(20).roles.get(0).role.switchPerson(people.get(20));
                people.get(21).addRole(aptTenants.get(17), "Apt Tenant");
                people.get(21).roles.get(0).role.switchPerson(people.get(21));

                for(int i = 18; i < 22; i++){
                        people.get(i).wallet.setOnHand(0.00);
                }
                
                rest1.getTimeCard().startThread();
                rest3.getTimeCard().startThread();
                market.getTimeCard().startThread();
                bank.getTimeCard().startThread();
                bankdatabase.startThread();
                
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
                SimEvent hostGoToRestaurant = new SimEvent(rest1, 8, EventType.HostEvent);
                SimEvent hostGoToRestaurant2 = new SimEvent(rest1, 14, EventType.HostEvent);
                SimEvent hostGoToRestaurant3 = new SimEvent(rest3, 8, EventType.HostEvent);	//CHANGE EVENT?
                SimEvent cookGoToRestaurant = new SimEvent(rest1, 8, EventType.CookEvent);
                SimEvent cookGoToRestaurant2 = new SimEvent(rest1, 14, EventType.CookEvent);
                SimEvent cookGoToRestaurant3 = new SimEvent(rest3, 8, EventType.CookEvent);	//CHANGE EVENT?
                SimEvent cashierGoToRestaurant = new SimEvent(rest1, 8, EventType.CashierEvent);
                SimEvent cashierGoToRestaurant2 = new SimEvent(rest1, 14, EventType.CashierEvent);
                SimEvent cashierGoToRestaurant3 = new SimEvent(rest3, 8, EventType.CashierEvent);
                SimEvent waiterGoToRestaurant = new SimEvent(rest1, 8, EventType.SDWaiterEvent);
                SimEvent waiterGoToRestaurant2 = new SimEvent(rest1, 14, EventType.SDWaiterEvent);
                SimEvent waiterGoToRestaurant3 = new SimEvent(rest3, 8, EventType.WaiterEvent);	//CHANGE EVENT?
                SimEvent tellerGoToBank = new SimEvent(bank, 8, EventType.TellerEvent);
                SimEvent tellerGoToBank2 = new SimEvent(bank, 14, EventType.TellerEvent);
                SimEvent hostGoToBank = new SimEvent(bank, 8, EventType.HostEvent);
                SimEvent hostGoToBank2 = new SimEvent(bank, 14, EventType.HostEvent);
                SimEvent employeeGoToMarket = new SimEvent(market, 8, EventType.EmployeeEvent);
                SimEvent employeeGoToMarket2 = new SimEvent(market, 14, EventType.EmployeeEvent);
                SimEvent cashierGoToMarket = new SimEvent(market, 8, EventType.CashierEvent);
                SimEvent cashierGoToMarket2 = new SimEvent(market, 14, EventType.CashierEvent);


                bankhostrole.addTeller(banktellerrole1);
                bankhostrole.addTeller(banktellerrole2);
                banktellerrole1.bh = bankhostrole;
                banktellerrole2.bh = bankhostrole;
                banktellerrole1.bd = bankdatabase;
                banktellerrole2.bd = bankdatabase;
                
                marketcashierrole.addEmployee(marketemployeerole);
                waiter1.setcook(cook1);
                waiter1.sethost(host1);
                waiter1.setRevolvingStand(cook1.getRevStand());
                marketemployeerole.setCashier(marketcashierrole);
                host1.msgaddwaiter(waiter1);
                waiter1.setCashier(cashier1);
                cook1.setMarketCashier(marketcashierrole);
                cook1.setCashier(cashier1);
                
                //Set Restaurant 3 agent references
                waiter3.setHost(host3);
                waiter3.setCook(cook3);
                waiter3.setCashier(cashier3);
                cook3.setCashier(cashier3);
                cook3.setMarketCashier(marketcashierrole);
                //host3.addWaiter(waiter3);
                

                // Messages landlord with initial tenants
                for (ApartmentTenantRole r : aptTenants) {
                        landlord.msgNewTenant(r, r.getAptNum());
                }
                
                /*
                 * toDO.offer(e) adds the SimEvent to the person's list and gives him/her purpose in SimCity
                 * Host, cook, cashier, waiter and teller events
                 */
                people.get(0).toDo.add(hostGoToBank);
                people.get(1).toDo.add(hostGoToBank2);
                people.get(2).toDo.add(tellerGoToBank);
                people.get(3).toDo.add(tellerGoToBank2);
                people.get(4).toDo.add(tellerGoToBank);
                people.get(5).toDo.add(tellerGoToBank2);
                people.get(6).toDo.add(employeeGoToMarket);
                people.get(7).toDo.add(employeeGoToMarket2);
                people.get(8).toDo.add(cashierGoToMarket);
                people.get(9).toDo.add(cashierGoToMarket2);
                people.get(10).toDo.add(cashierGoToRestaurant);
                people.get(11).toDo.add(cashierGoToRestaurant2);
                people.get(12).toDo.add(cookGoToRestaurant);
                people.get(13).toDo.add(cookGoToRestaurant2);
                people.get(14).toDo.add(waiterGoToRestaurant);
                people.get(15).toDo.add(waiterGoToRestaurant2);
                people.get(16).toDo.add(hostGoToRestaurant);
                people.get(17).toDo.add(hostGoToRestaurant2);
                
                truck.setCashier(marketcashierrole);
                marketcashierrole.addTruck(truck);
                /*Create the SimWorldClock with the starting time and the list of people*/
                simclock = new SimWorldClock(8,people, citymap);
                simclock.timeCards.add(bank.getTimeCard());
                simclock.timeCards.add(market.getTimeCard());
                simclock.timeCards.add(rest1.getTimeCard());
                simclock.timeCards.add(rest3.getTimeCard());
                for (PersonAgent p: people){
                        p.setcitygui(this);
                       p.startThread();
                }
                
                // Sets the sim world clock to the interaction panel's so events can be created with correct start time
                cityCtrlPanel.interactPanel.setClock(simclock);
                cityCtrlPanel.scenarioPanel.setCityAnim(cityAnimPanel);
        		cityCtrlPanel.scenarioPanel.setSimCityGUI(this);
        		cityCtrlPanel.scenarioPanel.setClock(simclock);
        
        
//        		PersonAgent p  = new PersonAgent("Crash dummy1", citymap, aStarTraversal, 500.00);
//                PersonGui pgui = new PersonGui(p,-20, -20);
//                p.setGui(pgui);
//                p.setAnimationPanel(cityAnimPanel);
//                cityAnimPanel.addGui(pgui);
//                simclock.addPerson(p);
//        		p.msgAddMoney(-400);
//        		AStarTraversal aStarTrav = new AStarTraversal(grid);
//        		aStarTrav.originalgrid = origgrid; 
//                CarAgent caragent = new CarAgent(aStarTrav, p);
//                caragent.percentCrash = 100; 
//                CarGui cgui = new CarGui(caragent,720,460);
//                caragent.setGui(cgui);
//                cityAnimPanel.addGui(cgui);
//                caragent.startThread();
//                caragent.gotoPosition(60, 180, 640, 180);
//                
//            	p  = new PersonAgent("Crash dummy2", citymap, aStarTraversal, 500.00);
//            	pgui = new PersonGui(p,-20,-20);
//                p.setGui(pgui);
//                p.setAnimationPanel(cityAnimPanel);
//                cityAnimPanel.addGui(pgui);
//                simclock.addPerson(p);
//        		p.msgAddMoney(-400);
//        		aStarTrav = new AStarTraversal(grid);
//        		aStarTrav.originalgrid = origgrid; 
//                caragent = new CarAgent(aStarTrav, p);
//                caragent.percentCrash = 100; 
//                cgui = new CarGui(caragent,720,460);
//                caragent.setGui(cgui);
//                cityAnimPanel.addGui(cgui);
//                caragent.startThread();
//                caragent.gotoPosition(80, 180, 640, 180);
        
        
        
        
        
        }

        public CarAgent createCar(PersonAgent p){
                AStarTraversal aStarTraversal = new AStarTraversal(grid);
        		aStarTraversal.originalgrid = origgrid; 
        	   CarAgent caragent = new CarAgent(aStarTraversal, p);
        	   caragent.setTrafficLightAgent(trafficlightagent);

        CarGui cgui = new CarGui(caragent,720,460);
        caragent.setGui(cgui);
        cityAnimPanel.addGui(cgui);
        caragent.startThread();
        return caragent; 
        }
        
        public void addPerson(PersonAgent p) {
                PersonGui pgui = new PersonGui(p,100,100);
                p.setGui(pgui);
                p.setAnimationPanel(cityAnimPanel);
                people.add(p);
                peoplegui.add(pgui);
                simclock.addPerson(p);
                cityAnimPanel.addGui(pgui);
        }

        
        public void crashcars(){
    		
    		//THIS IS CREATED ONLY TO TEST OUT THE CAR CRASHING SCENARIO
    		AStarTraversal aStarTraversal = new AStarTraversal(grid);

    		PersonAgent p  = new PersonAgent("Crash dummy1", citymap, aStarTraversal, 500.00);
            PersonGui pgui = new PersonGui(p,-20, -20);
            p.setGui(pgui);
            p.setAnimationPanel(cityAnimPanel);
            cityAnimPanel.addGui(pgui);
            simclock.addPerson(p);
    		p.msgAddMoney(-400);
    		AStarTraversal aStarTrav = new AStarTraversal(grid);
    		aStarTrav.originalgrid = origgrid; 
            CarAgent caragent = new CarAgent(aStarTrav, p);
            caragent.percentCrash = 100; 
            CarGui cgui = new CarGui(caragent,720,460);
            caragent.setGui(cgui);
            cityAnimPanel.addGui(cgui);
            caragent.startThread();
            caragent.gotoPosition(720, 180, 100, 180);
            
        	p  = new PersonAgent("Crash dummy2", citymap, aStarTraversal, 500.00);
        	pgui = new PersonGui(p,-20,-20);
            p.setGui(pgui);
            p.setAnimationPanel(cityAnimPanel);
            cityAnimPanel.addGui(pgui);
            simclock.addPerson(p);
    		p.msgAddMoney(-400);
    		aStarTrav = new AStarTraversal(grid);
    		aStarTrav.originalgrid = origgrid; 
            caragent = new CarAgent(aStarTrav, p);
            caragent.percentCrash = 100; 
            cgui = new CarGui(caragent,720,460);
            caragent.setGui(cgui);
            cityAnimPanel.addGui(cgui);
            caragent.startThread();
            caragent.gotoPosition(420, 460, 420, 0);
    	}
    	
        

        public static void main(String[] args){
                SimCityGUI scg = new SimCityGUI();
                scg.setVisible(true);
                scg.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

}
