package gui.subpanels;
import person.*;
import person.Location.LocationType;
import person.SimEvent.EventType;
import person.gui.PersonGui; 
import resident.*;
import resident.gui.ApartmentTenantGui;
import restaurant1.Restaurant1HostRole;
import gui.panels.CityAnimationPanel;
import gui.panels.CityControlPanel;
import gui.main.SimCityGUI; 

import java.awt.*;
import java.awt.event.*;

import javax.swing.Timer;

import java.util.*;
import java.util.List;

import javax.swing.*;

import market.*;
import market.gui.MarketEmployeeGui;
import agent.Role;
import bank.*;
import bank.gui.BankCustomerGui;
import bank.gui.BankHostGui;
import bank.gui.BankTellerGui;
import restaurant1.*;
import restaurant3.*;
import restaurant3.gui.*;
import restaurant1.gui.*;
import restaurant2.gui.*;
import restaurant4.gui.*;
import restaurant5.gui.*;
import restaurant6.gui.*;
import resident.gui.*;
import restaurant2.*;
import restaurant4.*;
import restaurant5.*;
import restaurant6.*;
import simcity.CityMap;
import utilities.Gui;
import utilities.TimeCard;

/**
 * This will allow the user to run different scenarios, both normative and non-normative.
 * @author jenniezhou
 *
 */
public class ScenarioPanel extends JPanel implements ActionListener{

	private SimCityGUI simCityGui; 
	
	// Reference to the sim world clock
	private SimWorldClock clock;
	
	List<PersonAgent> people;
	List<PersonGui> peopleGuis;
	List<Gui> guis;
	List<Role> roles;
	List<Location> locations;
	
	// Sets world clock
	public void setClock(SimWorldClock c) {
		clock = c;
	}
	
	// Sets the sim city gui so that bus stops can be added
	public void setSimCityGUI(SimCityGUI sc) {
		simCityGui = sc;
	}
	
	// Temporary person agent
	private PersonAgent initPerson;

	private String title = " Scenario Person Panel ";
	private static final int WIDTH = 275;
	private static final int BUTTONWIDTH = 270;
	private static final int HEIGHT = 310;
	private final int f_width = WIDTH;
	private final int bf_width = BUTTONWIDTH;
	private final int f_height = 200;
	private Dimension size = new Dimension(WIDTH, HEIGHT);
	
	// Main control panel reference
	private CityControlPanel cntrlPanel;
	
	// Reference to city animation panel
	private CityAnimationPanel cityAnimPanel;
	
	// Formatting panels
	private JPanel restartPanel = new JPanel();
	private JPanel addOnPanel = new JPanel();
	private JPanel runPanel = new JPanel(); // To contain the run button
	
	// To keep track of what scenario button was pressed
	private JButton chosen;
	
	// Map of the city
	private CityMap cityMap;
	
	// COMPONENT DECLARATIONS
	// List of scenario buttons 
	private List<JButton> addOnScenarios = Collections.synchronizedList(new ArrayList<JButton>());
	// List of restart scenario buttons
	private List<JButton> restartScenarios = Collections.synchronizedList(new ArrayList<JButton>());
	// List of all buttons
	private List<JButton> allButtons = Collections.synchronizedList(new ArrayList<JButton>());

	private JButton normOnePerson = new JButton("One not working person");
	private JButton normThreePeople = new JButton("Three not working people");
	private JButton normFiftyPeople = new JButton("Full 50-person scenario");
	private JButton weekendBehavior = new JButton("Weekend Behavior");

	private JButton robBank = new JButton("Robber robs Bank");
	private JButton carAccident = new JButton("Vehicle collision");
	private JButton personAccident = new JButton("Person Vehicle collision");
	
	// Run scenario button
	private JButton run = new JButton("Run");
	
	// Sets city animation panel
	public void setCityAnim(CityAnimationPanel c) {
		cityAnimPanel = c;
	}
	
	public ScenarioPanel(CityControlPanel cp) {
		run.setEnabled(false);
		
		cntrlPanel = cp;
		
		// Sets button size
		Dimension buttonDim = new Dimension(170, 7);
		
		// PANEL SETUP
		this.setLayout(new GridLayout(3,1));
		this.setBorder(BorderFactory.createTitledBorder(title));
		this.setBackground(Color.GRAY);
		
		// Panel size initiations
		this.setPreferredSize(size);
		this.setMaximumSize(size);
		
		// ACTION LISTENERS FOR BUTTONS
		robBank.addActionListener(this);
		carAccident.addActionListener(this);
		personAccident.addActionListener(this);
		normOnePerson.addActionListener(this);
		normThreePeople.addActionListener(this);
		normFiftyPeople.addActionListener(this);
		weekendBehavior.addActionListener(this);
		run.addActionListener(this);

		// COMPONENT INITIALIZATIONS
		// Formatting panel setup
		Dimension fpSize = new Dimension(bf_width, f_height);
		addOnPanel.setBorder(BorderFactory.createTitledBorder("Add-on Scenarios"));
		addOnPanel.setLayout(new GridLayout(3, 1));
		addOnPanel.setBackground(Color.GRAY);
		addOnPanel.setPreferredSize(fpSize);
		addOnPanel.setMaximumSize(fpSize);
		
		// Formatting panel setup		
		restartPanel.setBorder(BorderFactory.createTitledBorder("Resetting Scenarios"));
		restartPanel.setLayout(new GridLayout(4, 1));
		restartPanel.setBackground(Color.GRAY);
		restartPanel.setPreferredSize(fpSize);
		restartPanel.setMaximumSize(fpSize);
		
		// Formatting run panel
		Dimension runSize = new Dimension(f_width, 40);
		
		runPanel.setLayout(new GridLayout(1, 1));
		runPanel.setBackground(Color.GRAY);
		runPanel.setPreferredSize(runSize);
		runPanel.setMaximumSize(runSize);
		
		// Adds run button to run panel
		runPanel.add(run);
		
		// Add buttons to the scenarios list
		addOnScenarios.add(robBank);
		addOnScenarios.add(carAccident);
		addOnScenarios.add(personAccident);
		
		// Add buttons to the restart scenarios list
		restartScenarios.add(normOnePerson);
		restartScenarios.add(normThreePeople);
		restartScenarios.add(normFiftyPeople);
		restartScenarios.add(weekendBehavior);
		
		robBank.setMaximumSize(buttonDim);
		carAccident.setMaximumSize(buttonDim);
		personAccident.setMaximumSize(buttonDim);
		normOnePerson.setMaximumSize(buttonDim);
		normThreePeople.setMaximumSize(buttonDim);
		normFiftyPeople.setMaximumSize(buttonDim);
		weekendBehavior.setMaximumSize(buttonDim);
		
		for (JButton b : addOnScenarios) {
			addOnPanel.add(b);
			allButtons.add(b);
		}
		
		for (JButton b : restartScenarios) {
			restartPanel.add(b);
			allButtons.add(b);
		}
		
		add(addOnPanel);
		add(restartPanel);
		add(runPanel);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == run) {
			if (chosen.getText().trim().equals("Robber robs Bank")) {
				robberScenario();
				//FIX
				System.err.println("Robber robs bank");
			}
			else if (chosen.getText().trim().equals("Vehicle collision")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				simCityGui.crashcars();
				
				System.err.println("Vehicle collision");
			}
			else if (chosen.getText().trim().equals("Person Vehicle collision")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("Person Vehicle collision");
			}
			else if (chosen.getText().trim().equals("One not working person")) {
				// Here we will run the scenario where all restaurants order from the market
				runOnePersonScenario();
				
				System.err.println("One not working person");
			}
			else if (chosen.getText().trim().equals("Three not working people")) {
				// Here we will run the scenario where all restaurants order from the market
				runThreePersonScenario();
				
				System.err.println("Three not working people");
			}
			else if (chosen.getText().trim().equals("50-person scenario")) {
				// Here we will run the scenario where all restaurants order from the market
				//FIX
				System.err.println("50-person scenario");
			}
			else if (chosen.getText().trim().equals("Weekend Behavior")) {
				// FIX
				System.err.println("Weekend Behavior");

			}
			run.setEnabled(false);
			chosen.setOpaque(false);
			chosen.setBackground(null);
		}
		
		for (JButton sc : allButtons) {
			if (e.getSource() == sc) {
				run.setEnabled(true);
				chosen = sc;
				sc.setOpaque(true);
				sc.setBackground(Color.BLUE);
				// Sets all other buttons in the list to have no background color 
				for (JButton ob : allButtons) {
					if (ob != sc) {
						ob.setOpaque(true);
						ob.setBackground(null);
					}
				}
			}
		}
	}
	
	public void runOnePersonScenario() {		
		// List of people
		people = Collections.synchronizedList(new ArrayList<PersonAgent>());
		
		// List of people GUIs
		peopleGuis = Collections.synchronizedList(new ArrayList<PersonGui>());
		
		// List of roles 
		roles = Collections.synchronizedList(new ArrayList<Role>());
		
		// List of guis
		guis = Collections.synchronizedList(new ArrayList<Gui>());
		
		// List of locations
		locations = Collections.synchronizedList(new ArrayList<Location>());
		
		for (int i = 1; i <= 35; ++i) {
			PersonAgent p = new PersonAgent("Person " + i); // cityMap, 500);
			//FIX
			PersonGui pgui = new PersonGui(p, 40, 170, this.cityAnimPanel);
			p.gui = pgui;
			p.homeNumber = i;
			people.add(p);
			peopleGuis.add(pgui);
			cityAnimPanel.addGui(pgui);
			p.setAnimationPanel(cityAnimPanel);
		}
		
		// Create the walking person
		PersonAgent walking = new PersonAgent("Walking Person", cityMap, 900);
		walking.walking = true;
		PersonGui pgui = new PersonGui(walking, 20, 170, this.cityAnimPanel);
		walking.gui = pgui;
		people.add(walking);
		peopleGuis.add(walking.gui);
		cityAnimPanel.addGui(walking.gui);
		walking.setAnimationPanel(cityAnimPanel);
	
		for (int i = 1; i < 11; ++i) {
			if (i <= 4) {
				BankTellerRole temp = new BankTellerRole(people.get(i-1), "BANK TELLER");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Bank Teller");
			}
			else if (i >= 5 && i < 7) {
				BankHostRole temp = new BankHostRole(people.get(i-1), "BANK HOST");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Bank Host");
			}
			else if (i >= 7 && i < 9) {
				MarketCashierRole temp = new MarketCashierRole(people.get(i-1), "MARKET CASHIER");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Market Cashier");
			}
			else if (i >= 9 && i < 11) { 
				MarketEmployeeRole temp = new MarketEmployeeRole(people.get(i-1), "MARKET EMPLOYEE");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Market Employee");
			}
		}
		
		/**
		 * BANK INITIALIZATION OF EMPLOYEES
		 */
		BankDatabaseAgent bankdatabase = new BankDatabaseAgent();
		bankdatabase.startThread();
		
		/**
		 * RESTAURANT EMPLOYEE INITIALIZATION
		 */
		// First restaurant's employees: FIRST SHIFT
		Restaurant1HostRole rest1Host = new Restaurant1HostRole("Host 1 Shift 1", people.get(10));
		people.get(10).addRole(rest1Host, "Rest 1 Host");
		Restaurant1CookRole rest1Cook = new Restaurant1CookRole("Cook 1 Shift 1", people.get(11));
		people.get(11).addRole(rest1Cook, "Rest 1 Cook");
		Restaurant1CashierRole rest1Cashier = new Restaurant1CashierRole("Cashier 1 Shift 1", people.get(12));
		people.get(12).addRole(rest1Cashier, "Rest 1 Cashier");
		Restaurant1SDWaiterRole rest1SDWaiter = new Restaurant1SDWaiterRole("Shared Data Waiter 1 Shift 1", people.get(13));
		people.get(13).addRole(rest1SDWaiter, "Rest 1 SDWaiter");
		Restaurant1WaiterRole rest1Waiter = new Restaurant1WaiterRole("Waiter 1 Shift 1", people.get(14));
		people.get(14).addRole(rest1Waiter, "Rest 1 Waiter");
		
		// Second restaurant's employees: FIRST SHIFT
		Restaurant2HostRole rest2Host = new Restaurant2HostRole("Host 2 Shift 1", people.get(15));
		people.get(15).addRole(rest2Host, "Rest 2 Host");
		Restaurant2CookRole rest2Cook = new Restaurant2CookRole("Cook 2 Shift 1", people.get(16));
		people.get(16).addRole(rest2Cook, "Rest 2 Cook");
		Restaurant2CashierRole rest2Cashier = new Restaurant2CashierRole("Cashier 2 Shift 1", people.get(17));
		people.get(17).addRole(rest2Cashier, "Rest 2 Cashier");
		Restaurant2SDWaiterRole rest2SDWaiter = new Restaurant2SDWaiterRole("Shared Data Waiter 2 Shift 1", people.get(18));
		people.get(18).addRole(rest2SDWaiter, "Rest 2 SDWaiter");
		Restaurant2WaiterRole rest2Waiter = new Restaurant2WaiterRole("Waiter 1 Shift 1", people.get(19));
		people.get(19).addRole(rest2Waiter, "Rest 2 Waiter");
		
		// Fourth restaurant's employees: FIRST SHIFT 
		Restaurant4HostRole rest4Host = new Restaurant4HostRole("Host 4 Shift 1", people.get(20));
		people.get(20).addRole(rest4Host, "Rest 4 Host");
		Restaurant4CookRole rest4Cook = new Restaurant4CookRole("Cook 4 Shift 1", people.get(21));
		people.get(21).addRole(rest4Cook, "Rest 4 Cook");
		Restaurant4CashierRole rest4Cashier = new Restaurant4CashierRole("Cashier 4 Shift 1", people.get(22));
		people.get(22).addRole(rest4Cashier, "Rest 4 Cashier");
		Restaurant4SDWaiterRole rest4SDWaiter = new Restaurant4SDWaiterRole("Shared Data Waiter 4 Shift 1", people.get(23));
		people.get(23).addRole(rest4SDWaiter, "Rest 4 SDWaiter");
		Restaurant4WaiterRole rest4Waiter = new Restaurant4WaiterRole("Waiter 4 Shift 1", people.get(24));
		people.get(24).addRole(rest4Waiter, "Rest 4 Waiter");
		
		// Fifth restaurant's employees: FIRST SHIFT
		Restaurant5HostAgent rest5Host = new Restaurant5HostAgent("Host 5 Shift 1", people.get(25));
		people.get(25).addRole(rest5Host, "Rest 5 Host");
		Restaurant5CookAgent rest5Cook = new Restaurant5CookAgent("Cook 5 Shift 1", people.get(26));
		people.get(26).addRole(rest5Cook, "Rest 5 Cook");
		Restaurant5Cashier rest5Cashier = new Restaurant5Cashier("Cashier 5 Shift 1", people.get(27));
		people.get(27).addRole(rest5Cashier, "Rest 5 Cashier");
		Restaurant5SDWaiterAgent rest5SDWaiter = new Restaurant5SDWaiterAgent("Shared Data Waiter 5 Shift 1", people.get(28));
		people.get(28).addRole(rest5SDWaiter, "Rest 5 SDWaiter");
		Restaurant5WaiterAgent rest5Waiter = new Restaurant5WaiterAgent("Waiter 5 Shift 1", people.get(29));
		people.get(29).addRole(rest5Waiter, "Rest 5 Waiter");
		
		// Sixth restaurant's employees: FIRST SHIFT
		Restaurant6HostRole rest6Host = new Restaurant6HostRole("Host 6 Shift 1", people.get(30));
		people.get(30).addRole(rest6Host, "Rest 6 Host");
		Restaurant6CookRole rest6Cook = new Restaurant6CookRole("Cook 6 Shift 1", people.get(31));
		people.get(31).addRole(rest6Cook, "Rest 6 Cook");
		Restaurant6CashierRole rest6Cashier = new Restaurant6CashierRole("Cashier 6 Shift 1", people.get(32));
		people.get(32).addRole(rest6Cashier, "Rest 6 Cashier");
		Restaurant6SDWaiterRole rest6SDWaiter = new Restaurant6SDWaiterRole("Shared Data Waiter 6 Shift 1", people.get(33));
		people.get(33).addRole(rest6SDWaiter, "Rest 6 SDWaiter");
		Restaurant6WaiterRole rest6Waiter = new Restaurant6WaiterRole("Waiter 6 Shift 1", people.get(34));
		people.get(34).addRole(rest6Waiter, "Rest 6 Waiter");
		
		/**
		 * CREATING HOME OWNER ROLE
		 */
		HomeOwnerRole homeOwner = new HomeOwnerRole(walking, "Home Owner", 1);
		
		/** 
		 * GUI CREATION AND INITIALIZATION
		 */
		int i = 0;
		walking.addRole(homeOwner, "Home Owner");
		walking.setHungerLevel(5);
		HomeOwnerGui hgGui = new HomeOwnerGui(homeOwner);
		cityAnimPanel.house1Panel.guis.add(hgGui);
		guis.add(hgGui);
		hgGui.isPresent = false;
		homeOwner.setGui(hgGui);
		
		for (Role r : roles) {
			if (r instanceof BankTellerRole) {
				BankTellerRole temp = (BankTellerRole)r;
				System.err.println("Bank teller gui created");
				BankTellerGui g = new BankTellerGui(temp);
				g.isPresent = false;
				guis.add(g);
				((BankTellerRole) r).setGui(g);
			}
			else if (r instanceof BankHostRole) {
				BankHostRole temp = (BankHostRole)r;
				System.err.println("Bank host gui created");
				BankHostGui g = new BankHostGui(temp);
				g.isPresent = false;
				guis.add(g);
				((BankHostRole) r).setGui(g);
			}
			else if (r instanceof MarketEmployeeRole) {
				MarketEmployeeRole temp = (MarketEmployeeRole)r;
				System.err.println("Market employee created");
				MarketEmployeeGui g = new MarketEmployeeGui(temp);
				g.isPresent = false;
				guis.add(g);
				((MarketEmployeeRole) r).setGui(g);
			}
			++i;
		}
		
		
		/**
		 * SETTING BANK GUIS AND MARKET GUIS TO CITY ANIMATION PANEL
		 */
		cityAnimPanel.bankPanel.addGui(((BankTellerRole)roles.get(0)).gui);
		cityAnimPanel.bankPanel.addGui(((BankTellerRole)roles.get(1)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankTellerRole)roles.get(2)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankTellerRole)roles.get(3)).gui);
		cityAnimPanel.bankPanel.addGui(((BankHostRole)roles.get(4)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankHostRole)roles.get(5)).gui);
		cityAnimPanel.marketPanel.addGui(((MarketEmployeeRole)roles.get(8)).employeeGui);
		cityAnimPanel.marketPanel2.addGui(((MarketEmployeeRole)roles.get(9)).employeeGui);
		
		/**
		 * RESTAURANT GUI CREATION AND INITIALIZATION
		 */
		// First Restaurant: FIRST SHIFT
		WaiterGui r1sharedwg1 = new WaiterGui(rest1SDWaiter);
		r1sharedwg1.isPresent = false;
		rest1SDWaiter.setGui(r1sharedwg1);
		cityAnimPanel.rest1Panel.addGui(r1sharedwg1);
		
		WaiterGui r1wg1 = new WaiterGui(rest1Waiter);
		r1wg1.isPresent = false;
		rest1Waiter.setGui(r1wg1);
		cityAnimPanel.rest1Panel.addGui(r1wg1);
		
		CookGui r1cg1 = new CookGui(rest1Cook);
		r1cg1.isPresent = false;
		rest1Cook.setGui(r1cg1);
		cityAnimPanel.rest1Panel.addGui(r1cg1);
		
		// Second Restaurant: FIRST SHIFT
		Restaurant2WaiterGui r2sharedwg1 = new Restaurant2WaiterGui(rest2SDWaiter);
		rest2SDWaiter.setGui(r2sharedwg1);
		cityAnimPanel.rest2Panel.addGui(r2sharedwg1);
		
		Restaurant2WaiterGui r2wg1 = new Restaurant2WaiterGui(rest2Waiter);
		rest2Waiter.setGui(r2wg1);
		cityAnimPanel.rest2Panel.addGui(r2wg1);
		
		Restaurant2CookGui r2cg1 = new Restaurant2CookGui(rest2Cook);
		rest2Cook.setGui(r2cg1);
		cityAnimPanel.rest2Panel.addGui(r2cg1);
		
		// Fourth Restaurant: FIRST SHIFT
		Restaurant4WaiterGui r4sharedwg1 = new Restaurant4WaiterGui(rest4SDWaiter, 52, 112);
		rest4SDWaiter.setGui(r4sharedwg1);
		cityAnimPanel.rest4Panel.addGui(r4sharedwg1);
		
		Restaurant4WaiterGui r4wg1 = new Restaurant4WaiterGui(rest4Waiter, 52, 134);
		rest4Waiter.setGui(r4wg1);
		cityAnimPanel.rest4Panel.addGui(r4wg1);
		
		Restaurant4CookGui r4cg1 = new Restaurant4CookGui(rest4Cook);
		rest4Cook.setGui(r4cg1);
		cityAnimPanel.rest4Panel.addGui(r4cg1);
		
		// Fifth Restaurant: FIRST SHIFT
		Restaurant5WaiterGui r5sharedwg1 = new Restaurant5WaiterGui(rest5SDWaiter);
		rest5SDWaiter.setGui(r5sharedwg1);
		cityAnimPanel.rest5Panel.addGui(r5sharedwg1);
		
		Restaurant5WaiterGui r5wg1 = new Restaurant5WaiterGui(rest5Waiter);
		rest5Waiter.setGui(r5wg1);
		cityAnimPanel.rest5Panel.addGui(r5wg1);
		
		Restaurant5CookGui r5cg1 = new Restaurant5CookGui(rest5Cook);
		rest5Cook.setGui(r5cg1);
		cityAnimPanel.rest5Panel.addGui(r5cg1);
		
		// Sixth Restaurant: FIRST SHIFT
		Restaurant6WaiterGui r6sharedwg1 = new Restaurant6WaiterGui(rest6SDWaiter, -20, -20);
		rest6SDWaiter.setGui(r6sharedwg1);
		cityAnimPanel.rest6Panel.addGui(r6sharedwg1);
		
		Restaurant6WaiterGui r6wg1 = new Restaurant6WaiterGui(rest6Waiter, -20, -20);
		rest6Waiter.setGui(r6wg1);
		cityAnimPanel.rest6Panel.addGui(r6wg1);
		
		Restaurant6CookGui r6cg1 = new Restaurant6CookGui(rest6Cook);
		rest6Cook.setGui(r6cg1);
		cityAnimPanel.rest6Panel.addGui(r6cg1);
		
		/**
		 * SETTING LOCATIONS
		 */
		// First quadrant locations
        Bank bank = new Bank("Banco Popular", new TimeCard(), (BankHostRole)roles.get(4), 
                        new Position(60, 170), LocationType.Bank);
        Market market = new Market("Pokemart", (MarketCashierRole)roles.get(6), new TimeCard(), 
                        new Position(130, 170), LocationType.Market);
        Restaurant rest1 = new Restaurant("Rest 1", rest1Host, new TimeCard(), new Position(200, 170), LocationType.Restaurant1);
        Restaurant rest2 = new Restaurant("Rest 2", rest2Host, new TimeCard(), new Position(270, 170), LocationType.Restaurant2);
        //Restaurant rest3 = new Restaurant("Rest 3", rest3Host, new TimeCard(), new Position(330, 40), LocationType.Restaurant);
        
        // Second quadrant locations
        Bank bank2 = new Bank("Banco Popular 2", new TimeCard(), (BankHostRole)roles.get(5), 
                        new Position(660, 170), LocationType.Bank);
        Market market2 = new Market("Pokemart 2", (MarketCashierRole)roles.get(7), new TimeCard(), 
                        new Position(460, 170), LocationType.Market);
        Restaurant rest4 = new Restaurant("Rest 4", rest4Host, new TimeCard(), new Position(520, 170), LocationType.Restaurant4);
        Restaurant rest5 = new Restaurant("Rest 5", rest5Host, new TimeCard(), new Position(600, 170), LocationType.Restaurant5);
        Restaurant rest6 = new Restaurant("Rest 6", rest6Host, new TimeCard(), new Position(440, 100), LocationType.Restaurant6);                
        
       // Third quadrant locations
        Home home = new Home("Home 1", homeOwner, new Position(460, 280), 1, LocationType.Home);
        
        // SETTING FOR RESTAURANTS AND MARKETS AND BANKS 		
  		rest1.setCashier(rest1Cashier);
  		rest1.setCook(rest1Cook);
  		rest1Waiter.setcook(rest1Cook);
  		rest1Waiter.sethost(rest1Host);
  		rest1Waiter.setCashier(rest1Cashier);
  		rest1SDWaiter.setcook(rest1Cook);
  		rest1SDWaiter.sethost(rest1Host);
  		rest1SDWaiter.setRevolvingStand(rest1Cook.getRevStand());
  		rest1SDWaiter.setCashier(rest1Cashier);
  		rest1Host.msgaddwaiter(rest1Waiter);
  		//rest1Host.msgaddwaiter(rest1SDWaiter);
  		rest1Cook.setMarketCashier((MarketCashierRole)roles.get(6));
  		rest1Cook.setCashier(rest1Cashier);
  		rest1Cashier.accountNumber = 1;
  		rest1Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest1Cashier, 5000.00, 1);
  
  		rest2.setCashier(rest2Cashier);
  		rest2.setCook(rest2Cook);
  		rest2Waiter.setCook(rest2Cook);
  		rest2Waiter.setHost(rest2Host);
  		rest2Waiter.setCashier(rest2Cashier);
  		rest2SDWaiter.setCook(rest2Cook);
  		rest2SDWaiter.setHost(rest2Host);
  		Restaurant2RevolvingStand rs2 = new Restaurant2RevolvingStand();
  		rest2Cook.setRevolvingStand(rs2);
  		rest2SDWaiter.revolver = rs2;//rest2Cook.revolver;
  		rest2SDWaiter.setCashier(rest2Cashier);
  		rest2Host.addWaiter(rest2Waiter);
  		rest2Host.addWaiter(rest2SDWaiter);
  		rest2Cook.setMarketCashier((MarketCashierRole)roles.get(6));
  		rest2Cook.cashier = rest2Cashier;
  		rest2Cashier.accountNumber = 2;
  		rest2Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest2Cashier, 5000.00, 2);
  		
  		rest4.setCashier(rest4Cashier);
  		rest4.setCook(rest4Cook);
  		rest4Waiter.setCook(rest4Cook);
  		rest4Waiter.setHost(rest4Host);
  		rest4Waiter.setCashier(rest4Cashier);
  		rest4SDWaiter.setCook(rest4Cook);
  		rest4SDWaiter.setHost(rest4Host);
  		((Restaurant4SDWaiterRole)rest4SDWaiter).stand = rest4Cook.stand;
  		rest4SDWaiter.setCashier(rest4Cashier);
  		rest4Host.addWaiter(rest4Waiter);
  		rest4Host.addWaiter(rest4SDWaiter);
  		rest4Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest4Cook.rc = rest4Cashier;
  		rest4Cashier.accountNumber = 4;
  		rest4Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest4Cashier, 5000.00, 4);
  		
  		rest5.setCashier(rest5Cashier);
  		rest5.setCook(rest5Cook);
  		rest5Waiter.setCook(rest5Cook);
  		rest5Waiter.setHost(rest5Host);
  		rest5Waiter.setCashier(rest5Cashier);
  		rest5SDWaiter.setCook(rest5Cook);
  		rest5SDWaiter.setHost(rest5Host);
  		rest5SDWaiter.revolvingstand = rest5Cook.revolvingstand;
  		rest5SDWaiter.setCashier(rest5Cashier);
  		rest5Host.addWaiter(rest5Waiter);
  		rest5Host.addWaiter(rest5SDWaiter);
  		rest5Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest5Cook.cashier = rest5Cashier;
  		rest5Cashier.accountNumber = 5;
  		rest5Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest5Cashier, 5000.00, 5);
  		
  		rest6.setCashier(rest6Cashier);
  		rest6.setCook(rest6Cook);
  		rest6Waiter.setCook(rest6Cook);
  		rest6Waiter.setHost(rest6Host);
  		rest6Waiter.setCashier(rest6Cashier);
  		rest6SDWaiter.setCook(rest6Cook);
  		rest6SDWaiter.setHost(rest6Host);
  		rest6SDWaiter.revolvingStand = rest6Cook.revolvingStand;
  		rest6SDWaiter.setCashier(rest6Cashier);
  		rest6Host.msgSetWaiter(rest6Waiter);
  		rest6Host.msgSetWaiter(rest6SDWaiter);
  		rest6Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest6Cook.cashier = rest6Cashier;
  		rest6Cashier.accountNumber = 6;
  		rest6Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest1Cashier, 5000.00, 6);
  		
  		// Setting tellers for the first bank host & vice versa
  		((BankHostRole)roles.get(4)).addTeller((BankTellerRole)roles.get(0));
  		((BankHostRole)roles.get(4)).addTeller((BankTellerRole)roles.get(1));
  		((BankTellerRole)roles.get(0)).bh = ((BankHostRole)roles.get(4));
  		((BankTellerRole)roles.get(1)).bh = ((BankHostRole)roles.get(4));
  		
  		// Setting tellers for the second bank host
  		((BankHostRole)roles.get(5)).addTeller((BankTellerRole)roles.get(2));
  		((BankHostRole)roles.get(5)).addTeller((BankTellerRole)roles.get(3));
  		((BankTellerRole)roles.get(2)).bh = ((BankHostRole)roles.get(5));
  		((BankTellerRole)roles.get(3)).bh = ((BankHostRole)roles.get(5));
  		
  		// Setting bank database for all tellers
  		for (Role r : roles) {
  			if (r instanceof BankTellerRole) {
  				((BankTellerRole)r).bd = bankdatabase;
  			}
  		}
        
  		// Setting market employee to market cashier & vice versa
  		((MarketCashierRole)roles.get(6)).addEmployee((MarketEmployeeRole)roles.get(8));
  		((MarketEmployeeRole)roles.get(8)).setCashier((MarketCashierRole)roles.get(6));
  		((MarketCashierRole)roles.get(7)).addEmployee((MarketEmployeeRole)roles.get(9));
  		((MarketEmployeeRole)roles.get(9)).setCashier((MarketCashierRole)roles.get(7));
        
		locations.add(bank);
		locations.add(bank2);
		locations.add(market);
		locations.add(market2);
		locations.add(rest1);
		locations.add(rest2);
		locations.add(rest4);
		locations.add(rest5);
		locations.add(rest6);
		locations.add(home);
		
		for (Location l : locations) {
			cityAnimPanel.addLocation(l);
		}
		
		/**
		 * ADDING EVENTS TO EACH PERSON
		 */
		// For the bank tellers
		SimEvent tellerGoToBank = new SimEvent("Go to work", bank, EventType.TellerEvent);
		SimEvent teller2GoToBank = new SimEvent("Go to work", bank, EventType.TellerEvent);
		SimEvent tellerGoToBank2 = new SimEvent("Go to work", bank2, EventType.TellerEvent);
		SimEvent teller2GoToBank2 = new SimEvent("Go to work", bank2, EventType.TellerEvent);
		
		// For the bank hosts
		SimEvent hostGoToBank = new SimEvent("Go to work", bank, EventType.HostEvent);
		SimEvent hostGoToBank2 = new SimEvent("Go to work", bank2, EventType.HostEvent);
		
		// For the market cashiers
		SimEvent cashierGoToMarket = new SimEvent("Go to work", market, EventType.CashierEvent);
		SimEvent cashierGoToMarket2 = new SimEvent("Go to work", market2, EventType.CashierEvent);
		
		// For the market employees
		SimEvent employeeGoToMarket = new SimEvent("Go to work", market, EventType.EmployeeEvent);
		SimEvent employeeGoToMarket2 = new SimEvent("Go to work", market2, EventType.EmployeeEvent);
		
		// For the first restaurant
		SimEvent hostGoToRestaurant = new SimEvent("Go to work", rest1, EventType.HostEvent);
		SimEvent cookGoToRestaurant = new SimEvent("Go to work", rest1, EventType.CookEvent);
		SimEvent cashierGoToRestaurant = new SimEvent("Go to work", rest1,EventType.CashierEvent);
		SimEvent sdWaiterGoToRestaurant2 = new SimEvent("Go to work", rest1, EventType.SDWaiterEvent);
		SimEvent waiterGoToRestaurant = new SimEvent("Go to work", rest1, EventType.WaiterEvent);
		
		// For the second restaurant
		SimEvent host2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.HostEvent);
		SimEvent cook2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.CookEvent);
		SimEvent cashier2GoToRestaurant = new SimEvent("Go to work", rest2,EventType.CashierEvent);
		SimEvent sdWaiter2GoToRestaurant2 = new SimEvent("Go to work", rest2, EventType.SDWaiterEvent);
		SimEvent waiter2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.WaiterEvent);
		
		// For the fourth restaurant
		SimEvent host4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.HostEvent);
		SimEvent cook4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.CookEvent);
		SimEvent cashier4GoToRestaurant = new SimEvent("Go to work", rest4,EventType.CashierEvent);
		SimEvent sdWaiter4GoToRestaurant2 = new SimEvent("Go to work", rest4, EventType.SDWaiterEvent);
		SimEvent waiter4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.WaiterEvent);
		
		// For the fifth restaurant
		SimEvent host5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.HostEvent);
		SimEvent cook5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.CookEvent);
		SimEvent cashier5GoToRestaurant = new SimEvent("Go to work", rest5,EventType.CashierEvent);
		SimEvent sdWaiter5GoToRestaurant2 = new SimEvent("Go to work", rest5, EventType.SDWaiterEvent);
		SimEvent waiter5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.WaiterEvent);
		
		// For the sixth restaurant
		SimEvent host6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.HostEvent);
		SimEvent cook6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.CookEvent);
		SimEvent cashier6GoToRestaurant = new SimEvent("Go to work", rest6,EventType.CashierEvent);
		SimEvent sdWaiter6GoToRestaurant2 = new SimEvent("Go to work", rest6, EventType.SDWaiterEvent);
		SimEvent waiter6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.WaiterEvent);
		
		people.get(0).msgAddEvent(tellerGoToBank);
		people.get(1).msgAddEvent(teller2GoToBank);
		people.get(2).msgAddEvent(tellerGoToBank2);
		people.get(3).msgAddEvent(teller2GoToBank2);
		people.get(4).msgAddEvent(hostGoToBank);
		people.get(5).msgAddEvent(hostGoToBank2);
		people.get(6).msgAddEvent(cashierGoToMarket);
		people.get(7).msgAddEvent(cashierGoToMarket2);
		people.get(8).msgAddEvent(employeeGoToMarket);
		people.get(9).msgAddEvent(employeeGoToMarket2);
		people.get(10).msgAddEvent(hostGoToRestaurant);
		people.get(11).msgAddEvent(cookGoToRestaurant);
		people.get(12).msgAddEvent(cashierGoToRestaurant);
		people.get(13).msgAddEvent(sdWaiterGoToRestaurant2);
		people.get(14).msgAddEvent(waiterGoToRestaurant);
		people.get(15).msgAddEvent(host2GoToRestaurant);
		people.get(16).msgAddEvent(cook2GoToRestaurant);
		people.get(17).msgAddEvent(cashier2GoToRestaurant);
		people.get(18).msgAddEvent(sdWaiter2GoToRestaurant2);
		people.get(19).msgAddEvent(waiter2GoToRestaurant);
		people.get(20).msgAddEvent(host4GoToRestaurant);
		people.get(21).msgAddEvent(cook4GoToRestaurant);
		people.get(22).msgAddEvent(cashier4GoToRestaurant);
		people.get(23).msgAddEvent(sdWaiter4GoToRestaurant2);
		people.get(24).msgAddEvent(waiter4GoToRestaurant);
		people.get(25).msgAddEvent(host5GoToRestaurant);
		people.get(26).msgAddEvent(cook5GoToRestaurant);
		people.get(27).msgAddEvent(cashier5GoToRestaurant);
		people.get(28).msgAddEvent(sdWaiter5GoToRestaurant2);
		people.get(29).msgAddEvent(waiter5GoToRestaurant);
		people.get(30).msgAddEvent(host6GoToRestaurant);
		people.get(31).msgAddEvent(cook6GoToRestaurant);
		people.get(32).msgAddEvent(cashier6GoToRestaurant);
		people.get(33).msgAddEvent(sdWaiter6GoToRestaurant2);
		people.get(34).msgAddEvent(waiter6GoToRestaurant);
		
		// This is to start all the people's threads
//		for (int j = 0; j < 14; ++j) {
//			people.get(j).startThread();
//		}
		
//		BankCustomerRole bankCust = new BankCustomerRole(walking, "Bank Customer");
//		walking.addRole(bankCust, "Bank Customer");
		
		/**
		 * GIVE WALKING PERSON EVENTS
		 */
		// Go home to eat
		SimEvent eatAtHome = new SimEvent("Eat", home, EventType.HomeOwnerEvent);
		
		// Go to bank to withdraw money
		SimEvent withdrawFromBank1 = new SimEvent("deposit", bank, EventType.CustomerEvent);
		
		// Go to market to buy shopping list items
		SimEvent goToMarket1 = new SimEvent("Buy groceries", market, EventType.CustomerEvent);
		
		// Eat at each restaurant.. fatty.
		SimEvent goToRest1 = new SimEvent("Eat", rest1, EventType.CustomerEvent);
		SimEvent goToRest2 = new SimEvent("Eat", rest2, EventType.CustomerEvent);
		SimEvent goToRest4 = new SimEvent("Eat", rest4, EventType.CustomerEvent);
		SimEvent goToRest5 = new SimEvent("Eat", rest5, EventType.CustomerEvent);
		SimEvent goToRest6 = new SimEvent("Eat", rest6, EventType.CustomerEvent);
		
		// Go to market to buy more shopping list items
		SimEvent goToMarket2 = new SimEvent("Buy groceries", market2, EventType.CustomerEvent);
		
		// Go to bank to deposit money
		SimEvent depositBank2 = new SimEvent("deposit", bank2, EventType.CustomerEvent);
		
		walking.msgAddEvent(eatAtHome);
		walking.msgAddEvent(withdrawFromBank1);
		walking.msgAddEvent(goToMarket1);
		walking.msgAddEvent(goToRest1);
		walking.msgAddEvent(goToRest2);
		walking.msgAddEvent(goToRest4);
		walking.msgAddEvent(goToRest5);
		walking.msgAddEvent(goToRest6);
		walking.msgAddEvent(goToMarket2);
		walking.msgAddEvent(depositBank2);
		
		for (PersonAgent p : people) {
			p.setcitygui(simCityGui);
			p.populateCityMap(locations);
		}
		
		//FIX.. FOR TESTING PURPOSES
		// This is to initialize all the city maps
//		for (int j = 0; j < 14; ++j) {
//			people.get(j).populateCityMap(locations);
//		}
		
		// This is to add all the bus stops to the city maps
//		for (int j = 0; j < 14; ++j) {
//			people.get(j).getMap().addBusStop(simCityGui.busstop1.name, simCityGui.busstop1);
//			people.get(j).getMap().addBusStop(simCityGui.busstop2.name, simCityGui.busstop2);
//			people.get(j).getMap().addBusStop(simCityGui.busstop3.name, simCityGui.busstop3);
//			people.get(j).getMap().addBusStop(simCityGui.busstop4.name, simCityGui.busstop4);
//			people.get(j).getMap().addBusStop(simCityGui.busstop5.name, simCityGui.busstop5);
//			people.get(j).getMap().addBusStop(simCityGui.busstop6.name, simCityGui.busstop6);
//			people.get(j).getMap().addBusStop(simCityGui.busstop7.name, simCityGui.busstop7);
//			people.get(j).getMap().addBusStop(simCityGui.busstop8.name, simCityGui.busstop8);
//			
//			people.get(j).getMap().addBus(simCityGui.busstop1, simCityGui.bus);
//			people.get(j).getMap().addBus(simCityGui.busstop2, simCityGui.bus);
//			people.get(j).getMap().addBus(simCityGui.busstop3, simCityGui.bus);
//			people.get(j).getMap().addBus(simCityGui.busstop4, simCityGui.bus);
//			people.get(j).getMap().addBus(simCityGui.busstop5, simCityGui.bus2);
//			people.get(j).getMap().addBus(simCityGui.busstop6, simCityGui.bus2);
//			people.get(j).getMap().addBus(simCityGui.busstop7, simCityGui.bus2);
//			people.get(j).getMap().addBus(simCityGui.busstop8, simCityGui.bus2);
//		}
		
		for (PersonAgent p : people) {
			p.getMap().addBusStop(simCityGui.busstop1.name, simCityGui.busstop1);
			p.getMap().addBusStop(simCityGui.busstop2.name, simCityGui.busstop2);
			p.getMap().addBusStop(simCityGui.busstop3.name, simCityGui.busstop3);
			p.getMap().addBusStop(simCityGui.busstop4.name, simCityGui.busstop4);
			p.getMap().addBusStop(simCityGui.busstop5.name, simCityGui.busstop5);
			p.getMap().addBusStop(simCityGui.busstop6.name, simCityGui.busstop6);
			p.getMap().addBusStop(simCityGui.busstop7.name, simCityGui.busstop7);
			p.getMap().addBusStop(simCityGui.busstop8.name, simCityGui.busstop8);
			
			p.getMap().addBus(simCityGui.busstop1, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop2, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop3, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop4, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop5, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop6, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop7, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop8, simCityGui.bus2);
		}
		
//		for(int j = 0; j < 25; j++){
//			people.get(j).startThread();
//		}

		for (PersonAgent p : people) {
			p.startThread();
		}
		
		// Starts the thread of each timecard
		bank.getTimeCard().startThread();
		bank2.getTimeCard().startThread();
		market.getTimeCard().startThread();
		market2.getTimeCard().startThread();
		rest1.getTimeCard().startThread();
		rest2.getTimeCard().startThread();
		rest4.getTimeCard().startThread();
		rest5.getTimeCard().startThread();
		rest6.getTimeCard().startThread();
		
		clock.timeCards.add(bank.getTimeCard());
		clock.timeCards.add(market.getTimeCard());
		clock.timeCards.add(market2.getTimeCard());
		clock.timeCards.add(rest1.getTimeCard());
		clock.timeCards.add(rest2.getTimeCard());
		clock.timeCards.add(rest4.getTimeCard());
		clock.timeCards.add(rest5.getTimeCard());
		clock.timeCards.add(rest6.getTimeCard());
	}
////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////// THREE PERSON SCENARIO////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
	public void runThreePersonScenario() {		
		// List of people
		people = Collections.synchronizedList(new ArrayList<PersonAgent>());
		
		// List of people GUIs
		peopleGuis = Collections.synchronizedList(new ArrayList<PersonGui>());
		
		// List of roles 
		roles = Collections.synchronizedList(new ArrayList<Role>());
		
		// List of guis
		guis = Collections.synchronizedList(new ArrayList<Gui>());
		
		// List of locations
		locations = Collections.synchronizedList(new ArrayList<Location>());
		
		for (int i = 1; i <= 35; ++i) {
			PersonAgent p = new PersonAgent("Person " + i); // cityMap, 500);
			//FIX
			PersonGui pgui = new PersonGui(p, 40, 170, this.cityAnimPanel);
			p.gui = pgui;
			p.homeNumber = i;
			people.add(p);
			peopleGuis.add(pgui);
			cityAnimPanel.addGui(pgui);
			p.setAnimationPanel(cityAnimPanel);
		}
		
		// Create the walking person
		PersonAgent walking = new PersonAgent("Walking Person", cityMap, 900);
		walking.walking = true;
		PersonGui pgui = new PersonGui(walking, 20, 170, this.cityAnimPanel);
		walking.gui = pgui;
		people.add(walking);
		peopleGuis.add(walking.gui);
		cityAnimPanel.addGui(walking.gui);
		walking.setAnimationPanel(cityAnimPanel);
		
		// Create the person taking the bus
		PersonAgent busPerson = new PersonAgent("Person taking the Bus", cityMap, 900);
		PersonGui busPersonGui = new PersonGui(busPerson, 40, 170, this.cityAnimPanel);
		busPerson.gui = busPersonGui;
		people.add(busPerson);
		peopleGuis.add(busPersonGui);
		cityAnimPanel.addGui(busPersonGui);
		busPerson.setAnimationPanel(cityAnimPanel);
		
		// Create the person taking the car
		PersonAgent carPerson = new PersonAgent("Person taking a Car", cityMap, 1000000);
		PersonGui carPersonGui = new PersonGui(carPerson, 40, 170, this.cityAnimPanel);
		carPerson.gui = carPersonGui;
		people.add(carPerson);
		peopleGuis.add(carPersonGui);
		cityAnimPanel.addGui(carPersonGui);
		carPerson.setAnimationPanel(cityAnimPanel);
	
		for (int i = 1; i < 11; ++i) {
			if (i <= 4) {
				BankTellerRole temp = new BankTellerRole(people.get(i-1), "BANK TELLER");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Bank Teller");
			}
			else if (i >= 5 && i < 7) {
				BankHostRole temp = new BankHostRole(people.get(i-1), "BANK HOST");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Bank Host");
			}
			else if (i >= 7 && i < 9) {
				MarketCashierRole temp = new MarketCashierRole(people.get(i-1), "MARKET CASHIER");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Market Cashier");
			}
			else if (i >= 9 && i < 11) { 
				MarketEmployeeRole temp = new MarketEmployeeRole(people.get(i-1), "MARKET EMPLOYEE");
				roles.add(temp);
				people.get(i-1).addRole(temp, "Market Employee");
			}
		}
		
		/**
		 * BANK INITIALIZATION OF EMPLOYEES
		 */
		BankDatabaseAgent bankdatabase = new BankDatabaseAgent();
		bankdatabase.startThread();
		
		/**
		 * RESTAURANT EMPLOYEE INITIALIZATION
		 */
		// First restaurant's employees: FIRST SHIFT
		Restaurant1HostRole rest1Host = new Restaurant1HostRole("Host 1 Shift 1", people.get(10));
		people.get(10).addRole(rest1Host, "Rest 1 Host");
		Restaurant1CookRole rest1Cook = new Restaurant1CookRole("Cook 1 Shift 1", people.get(11));
		people.get(11).addRole(rest1Cook, "Rest 1 Cook");
		Restaurant1CashierRole rest1Cashier = new Restaurant1CashierRole("Cashier 1 Shift 1", people.get(12));
		people.get(12).addRole(rest1Cashier, "Rest 1 Cashier");
		Restaurant1SDWaiterRole rest1SDWaiter = new Restaurant1SDWaiterRole("Shared Data Waiter 1 Shift 1", people.get(13));
		people.get(13).addRole(rest1SDWaiter, "Rest 1 SDWaiter");
		Restaurant1WaiterRole rest1Waiter = new Restaurant1WaiterRole("Waiter 1 Shift 1", people.get(14));
		people.get(14).addRole(rest1Waiter, "Rest 1 Waiter");
		
		// Second restaurant's employees: FIRST SHIFT
		Restaurant2HostRole rest2Host = new Restaurant2HostRole("Host 2 Shift 1", people.get(15));
		people.get(15).addRole(rest2Host, "Rest 2 Host");
		Restaurant2CookRole rest2Cook = new Restaurant2CookRole("Cook 2 Shift 1", people.get(16));
		people.get(16).addRole(rest2Cook, "Rest 2 Cook");
		Restaurant2CashierRole rest2Cashier = new Restaurant2CashierRole("Cashier 2 Shift 1", people.get(17));
		people.get(17).addRole(rest2Cashier, "Rest 2 Cashier");
		Restaurant2SDWaiterRole rest2SDWaiter = new Restaurant2SDWaiterRole("Shared Data Waiter 2 Shift 1", people.get(18));
		people.get(18).addRole(rest2SDWaiter, "Rest 2 SDWaiter");
		Restaurant2WaiterRole rest2Waiter = new Restaurant2WaiterRole("Waiter 1 Shift 1", people.get(19));
		people.get(19).addRole(rest2Waiter, "Waiter 2 Shift 1");
		
		// Fourth restaurant's employees: FIRST SHIFT 
		Restaurant4HostRole rest4Host = new Restaurant4HostRole("Host 4 Shift 1", people.get(20));
		people.get(20).addRole(rest4Host, "Rest 4 Host");
		Restaurant4CookRole rest4Cook = new Restaurant4CookRole("Cook 4 Shift 1", people.get(21));
		people.get(21).addRole(rest4Cook, "Rest 4 Cook");
		Restaurant4CashierRole rest4Cashier = new Restaurant4CashierRole("Cashier 4 Shift 1", people.get(22));
		people.get(22).addRole(rest4Cashier, "Rest 4 Cashier");
		Restaurant4SDWaiterRole rest4SDWaiter = new Restaurant4SDWaiterRole("Shared Data Waiter 4 Shift 1", people.get(23));
		people.get(23).addRole(rest4SDWaiter, "Rest 4 SDWaiter");
		Restaurant4WaiterRole rest4Waiter = new Restaurant4WaiterRole("Waiter 4 Shift 1", people.get(24));
		people.get(24).addRole(rest4Waiter, "Rest 4 Waiter");
		
		// Fifth restaurant's employees: FIRST SHIFT
		Restaurant5HostAgent rest5Host = new Restaurant5HostAgent("Host 5 Shift 1", people.get(25));
		people.get(25).addRole(rest5Host, "Rest 5 Host");
		Restaurant5CookAgent rest5Cook = new Restaurant5CookAgent("Cook 5 Shift 1", people.get(26));
		people.get(26).addRole(rest5Cook, "Rest 5 Cook");
		Restaurant5Cashier rest5Cashier = new Restaurant5Cashier("Cashier 5 Shift 1", people.get(27));
		people.get(27).addRole(rest5Cashier, "Rest 5 Cashier");
		Restaurant5SDWaiterAgent rest5SDWaiter = new Restaurant5SDWaiterAgent("Shared Data Waiter 5 Shift 1", people.get(28));
		people.get(28).addRole(rest5SDWaiter, "Rest 5 SDWaiter");
		Restaurant5WaiterAgent rest5Waiter = new Restaurant5WaiterAgent("Waiter 5 Shift 1", people.get(29));
		people.get(29).addRole(rest5Waiter, "Rest 5 Waiter");
		
		// Sixth restaurant's employees: FIRST SHIFT
		Restaurant6HostRole rest6Host = new Restaurant6HostRole("Host 6 Shift 1", people.get(30));
		people.get(30).addRole(rest6Host, "Rest 6 Host");
		Restaurant6CookRole rest6Cook = new Restaurant6CookRole("Cook 6 Shift 1", people.get(31));
		people.get(31).addRole(rest6Cook, "Rest 6 Cook");
		Restaurant6CashierRole rest6Cashier = new Restaurant6CashierRole("Cashier 6 Shift 1", people.get(32));
		people.get(32).addRole(rest6Cashier, "Rest 6 Cashier");
		Restaurant6SDWaiterRole rest6SDWaiter = new Restaurant6SDWaiterRole("Shared Data Waiter 6 Shift 1", people.get(33));
		people.get(33).addRole(rest6SDWaiter, "Rest 6 SDWaiter");
		Restaurant6WaiterRole rest6Waiter = new Restaurant6WaiterRole("Waiter 6 Shift 1", people.get(34));
		people.get(34).addRole(rest6Waiter, "Rest 6 Waiter");
		
		/**
		 * CREATING HOME OWNER ROLE
		 */
		HomeOwnerRole homeOwner = new HomeOwnerRole(walking, "Home Owner", 1);
		HomeOwnerRole homeOwner2 = new HomeOwnerRole(busPerson, "Home Owner 2", 2);
		HomeOwnerRole homeOwner3 = new HomeOwnerRole(carPerson, "Home Owner 3", 3);
		
		/** 
		 * GUI CREATION AND INITIALIZATION
		 */
		int i = 0;
		walking.addRole(homeOwner, "Home Owner");
		walking.setHungerLevel(5);
		HomeOwnerGui hgGui = new HomeOwnerGui(homeOwner);
		cityAnimPanel.house1Panel.guis.add(hgGui);
		guis.add(hgGui);
		hgGui.isPresent = false;
		homeOwner.setGui(hgGui);
		
		busPerson.addRole(homeOwner2, "Home Owner");
		busPerson.setHungerLevel(5);
		HomeOwnerGui hgGuiBus = new HomeOwnerGui(homeOwner2);
		cityAnimPanel.house2Panel.guis.add(hgGuiBus);
		guis.add(hgGuiBus);
		hgGuiBus.isPresent = false;
		homeOwner2.setGui(hgGuiBus);
		
		carPerson.addRole(homeOwner3, "Home Owner");
		carPerson.setHungerLevel(5);
		HomeOwnerGui hgGuiCar = new HomeOwnerGui(homeOwner3);
		cityAnimPanel.house3Panel.guis.add(hgGuiCar);
		guis.add(hgGuiCar);
		hgGuiCar.isPresent = false;
		homeOwner3.setGui(hgGuiCar);
		
		for (Role r : roles) {
			if (r instanceof BankTellerRole) {
				BankTellerRole temp = (BankTellerRole)r;
				System.err.println("Bank teller gui created");
				BankTellerGui g = new BankTellerGui(temp);
				g.isPresent = false;
				guis.add(g);
				((BankTellerRole) r).setGui(g);
			}
			else if (r instanceof BankHostRole) {
				BankHostRole temp = (BankHostRole)r;
				System.err.println("Bank host gui created");
				BankHostGui g = new BankHostGui(temp);
				g.isPresent = false;
				guis.add(g);
				((BankHostRole) r).setGui(g);
			}
			else if (r instanceof MarketEmployeeRole) {
				MarketEmployeeRole temp = (MarketEmployeeRole)r;
				System.err.println("Market employee created");
				MarketEmployeeGui g = new MarketEmployeeGui(temp);
				g.isPresent = false;
				guis.add(g);
				((MarketEmployeeRole) r).setGui(g);
			}
			++i;
		}
		
		
		/**
		 * SETTING BANK GUIS AND MARKET GUIS TO CITY ANIMATION PANEL
		 */
		cityAnimPanel.bankPanel.addGui(((BankTellerRole)roles.get(0)).gui);
		cityAnimPanel.bankPanel.addGui(((BankTellerRole)roles.get(1)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankTellerRole)roles.get(2)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankTellerRole)roles.get(3)).gui);
		cityAnimPanel.bankPanel.addGui(((BankHostRole)roles.get(4)).gui);
		cityAnimPanel.bankPanel2.addGui(((BankHostRole)roles.get(5)).gui);
		cityAnimPanel.marketPanel.addGui(((MarketEmployeeRole)roles.get(8)).employeeGui);
		cityAnimPanel.marketPanel2.addGui(((MarketEmployeeRole)roles.get(9)).employeeGui);
		
		/**
		 * RESTAURANT GUI CREATION AND INITIALIZATION
		 */
		// First Restaurant: FIRST SHIFT
		WaiterGui r1sharedwg1 = new WaiterGui(rest1SDWaiter);
		r1sharedwg1.isPresent = false;
		rest1SDWaiter.setGui(r1sharedwg1);
		cityAnimPanel.rest1Panel.addGui(r1sharedwg1);
		
		WaiterGui r1wg1 = new WaiterGui(rest1Waiter);
		r1wg1.isPresent = false;
		rest1Waiter.setGui(r1wg1);
		cityAnimPanel.rest1Panel.addGui(r1wg1);
		
		CookGui r1cg1 = new CookGui(rest1Cook);
		r1cg1.isPresent = false;
		rest1Cook.setGui(r1cg1);
		cityAnimPanel.rest1Panel.addGui(r1cg1);
		
		// Second Restaurant: FIRST SHIFT
		Restaurant2WaiterGui r2sharedwg1 = new Restaurant2WaiterGui(rest2SDWaiter);
		rest2SDWaiter.setGui(r2sharedwg1);
		cityAnimPanel.rest2Panel.addGui(r2sharedwg1);
		
		Restaurant2WaiterGui r2wg1 = new Restaurant2WaiterGui(rest2Waiter);
		rest2Waiter.setGui(r2wg1);
		cityAnimPanel.rest2Panel.addGui(r2wg1);
		
		Restaurant2CookGui r2cg1 = new Restaurant2CookGui(rest2Cook);
		rest2Cook.setGui(r2cg1);
		cityAnimPanel.rest2Panel.addGui(r2cg1);
		
		// Fourth Restaurant: FIRST SHIFT
		Restaurant4WaiterGui r4sharedwg1 = new Restaurant4WaiterGui(rest4SDWaiter, 52, 112);
		rest4SDWaiter.setGui(r4sharedwg1);
		cityAnimPanel.rest4Panel.addGui(r4sharedwg1);
		
		Restaurant4WaiterGui r4wg1 = new Restaurant4WaiterGui(rest4Waiter, 52, 134);
		rest4Waiter.setGui(r4wg1);
		cityAnimPanel.rest4Panel.addGui(r4wg1);
		
		Restaurant4CookGui r4cg1 = new Restaurant4CookGui(rest4Cook);
		rest4Cook.setGui(r4cg1);
		cityAnimPanel.rest4Panel.addGui(r4cg1);
		
		// Fifth Restaurant: FIRST SHIFT
		Restaurant5WaiterGui r5sharedwg1 = new Restaurant5WaiterGui(rest5SDWaiter);
		rest5SDWaiter.setGui(r5sharedwg1);
		cityAnimPanel.rest5Panel.addGui(r5sharedwg1);
		
		Restaurant5WaiterGui r5wg1 = new Restaurant5WaiterGui(rest5Waiter);
		rest5Waiter.setGui(r5wg1);
		cityAnimPanel.rest5Panel.addGui(r5wg1);
		
		Restaurant5CookGui r5cg1 = new Restaurant5CookGui(rest5Cook);
		rest5Cook.setGui(r5cg1);
		cityAnimPanel.rest5Panel.addGui(r5cg1);
		
		// Sixth Restaurant: FIRST SHIFT
		Restaurant6WaiterGui r6sharedwg1 = new Restaurant6WaiterGui(rest6SDWaiter, -20, -20);
		rest6SDWaiter.setGui(r6sharedwg1);
		cityAnimPanel.rest6Panel.addGui(r6sharedwg1);
		
		Restaurant6WaiterGui r6wg1 = new Restaurant6WaiterGui(rest6Waiter, -20, -20);
		rest6Waiter.setGui(r6wg1);
		cityAnimPanel.rest6Panel.addGui(r6wg1);
		
		Restaurant6CookGui r6cg1 = new Restaurant6CookGui(rest6Cook);
		rest6Cook.setGui(r6cg1);
		cityAnimPanel.rest6Panel.addGui(r6cg1);
		
		/**
		 * SETTING LOCATIONS
		 */
		// First quadrant locations
        Bank bank = new Bank("Banco Popular", new TimeCard(), (BankHostRole)roles.get(4), 
                        new Position(60, 170), LocationType.Bank);
        Market market = new Market("Pokemart", (MarketCashierRole)roles.get(6), new TimeCard(), 
                        new Position(130, 170), LocationType.Market);
        Restaurant rest1 = new Restaurant("Rest 1", rest1Host, new TimeCard(), new Position(200, 170), LocationType.Restaurant1);
        Restaurant rest2 = new Restaurant("Rest 2", rest2Host, new TimeCard(), new Position(270, 170), LocationType.Restaurant2);
        //Restaurant rest3 = new Restaurant("Rest 3", rest3Host, new TimeCard(), new Position(330, 40), LocationType.Restaurant);
        
        // Second quadrant locations
        Bank bank2 = new Bank("Bank 2", new TimeCard(), (BankHostRole)roles.get(5), 
                        new Position(660, 170), LocationType.Bank);
        Market market2 = new Market("Market 2", (MarketCashierRole)roles.get(7), new TimeCard(), 
                        new Position(460, 170), LocationType.Market);
        Restaurant rest4 = new Restaurant("Rest 4", rest4Host, new TimeCard(), new Position(520, 170), LocationType.Restaurant4);
        Restaurant rest5 = new Restaurant("Rest 5", rest5Host, new TimeCard(), new Position(600, 170), LocationType.Restaurant5);
        Restaurant rest6 = new Restaurant("Rest 6", rest6Host, new TimeCard(), new Position(440, 100), LocationType.Restaurant6);                
        
       // Third quadrant locations
        Home home = new Home("Home 1", homeOwner, new Position(460, 280), 1, LocationType.Home);
        Home home2 = new Home("Home 2", homeOwner2, new Position(440, 380), 1, LocationType.Home);
        Home home3 = new Home("Home 3", homeOwner3, new Position(520, 280), 1, LocationType.Home);
        
        // SETTING FOR RESTAURANTS AND MARKETS AND BANKS 		
  		rest1.setCashier(rest1Cashier);
  		rest1.setCook(rest1Cook);
  		rest1Waiter.setcook(rest1Cook);
  		rest1Waiter.sethost(rest1Host);
  		rest1Waiter.setCashier(rest1Cashier);
  		rest1SDWaiter.setcook(rest1Cook);
  		rest1SDWaiter.sethost(rest1Host);
  		rest1SDWaiter.setRevolvingStand(rest1Cook.getRevStand());
  		rest1SDWaiter.setCashier(rest1Cashier);
  		rest1Host.msgaddwaiter(rest1Waiter);
  		rest1Host.msgaddwaiter(rest1SDWaiter);
  		rest1Cook.setMarketCashier((MarketCashierRole)roles.get(6));
  		rest1Cook.setCashier(rest1Cashier);
  		rest1Cashier.accountNumber = 1;
  		rest1Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest1Cashier, 5000.00, 1);
  
  		rest2.setCashier(rest2Cashier);
  		rest2.setCook(rest2Cook);
  		rest2Waiter.setCook(rest2Cook);
  		rest2Waiter.setHost(rest2Host);
  		rest2Waiter.setCashier(rest2Cashier);
  		rest2SDWaiter.setCook(rest2Cook);
  		rest2SDWaiter.setHost(rest2Host);
  		Restaurant2RevolvingStand rs2 = new Restaurant2RevolvingStand();
  		rest2Cook.setRevolvingStand(rs2);
  		rest2SDWaiter.revolver = rs2;//rest2Cook.revolver;
  		rest2SDWaiter.setCashier(rest2Cashier);
  		//rest2Host.addWaiter(rest2Waiter);
  		rest2Host.addWaiter(rest2SDWaiter);
  		rest2Cook.setMarketCashier((MarketCashierRole)roles.get(6));
  		rest2Cook.cashier = rest2Cashier;
  		rest2Cashier.accountNumber = 2;
  		rest2Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest1Cashier, 5000.00, 2);
  		
  		rest4.setCashier(rest4Cashier);
  		rest4.setCook(rest4Cook);
  		rest4Waiter.setCook(rest4Cook);
  		rest4Waiter.setHost(rest4Host);
  		rest4Waiter.setCashier(rest4Cashier);
  		rest4SDWaiter.setCook(rest4Cook);
  		rest4SDWaiter.setHost(rest4Host);
  		((Restaurant4SDWaiterRole)rest4SDWaiter).stand = rest4Cook.stand;
  		rest4SDWaiter.setCashier(rest4Cashier);
  		rest4Host.addWaiter(rest4Waiter);
  		rest4Host.addWaiter(rest4SDWaiter);
  		rest4Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest4Cook.rc = rest4Cashier;
  		rest4Cashier.accountNumber = 4;
  		rest4Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest4Cashier, 5000.00, 4);
  		
  		rest5.setCashier(rest5Cashier);
  		rest5.setCook(rest5Cook);
  		rest5Waiter.setCook(rest5Cook);
  		rest5Waiter.setHost(rest5Host);
  		rest5Waiter.setCashier(rest5Cashier);
  		rest5SDWaiter.setCook(rest5Cook);
  		rest5SDWaiter.setHost(rest5Host);
  		rest5SDWaiter.revolvingstand = rest5Cook.revolvingstand;
  		rest5SDWaiter.setCashier(rest5Cashier);
  		rest5Host.addWaiter(rest5Waiter);
  		rest5Host.addWaiter(rest5SDWaiter);
  		rest5Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest5Cook.cashier = rest5Cashier;
  		rest5Cashier.accountNumber = 5;
  		rest5Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest5Cashier, 5000.00, 5);
  		
  		rest6.setCashier(rest6Cashier);
  		rest6.setCook(rest6Cook);
  		rest6Waiter.setCook(rest6Cook);
  		rest6Waiter.setHost(rest6Host);
  		rest6Waiter.setCashier(rest6Cashier);
  		rest6SDWaiter.setCook(rest6Cook);
  		rest6SDWaiter.setHost(rest6Host);
  		rest6SDWaiter.revolvingStand = rest6Cook.revolvingStand;
  		rest6SDWaiter.setCashier(rest6Cashier);
  		rest6Host.msgSetWaiter(rest6Waiter);
  		rest6Host.msgSetWaiter(rest6SDWaiter);
  		rest6Cook.setMarketCashier((MarketCashierRole)roles.get(7));
  		rest6Cook.cashier = rest6Cashier;
  		rest6Cashier.accountNumber = 6;
  		rest6Cashier.bank = bankdatabase;
  		bankdatabase.addRestaurantAccount(rest6Cashier, 5000.00, 6);
  		
  		// Setting tellers for the first bank host & vice versa
  		((BankHostRole)roles.get(4)).addTeller((BankTellerRole)roles.get(0));
  		((BankHostRole)roles.get(4)).addTeller((BankTellerRole)roles.get(1));
  		((BankTellerRole)roles.get(0)).bh = ((BankHostRole)roles.get(4));
  		((BankTellerRole)roles.get(1)).bh = ((BankHostRole)roles.get(4));
  		
  		// Setting tellers for the second bank host
  		((BankHostRole)roles.get(5)).addTeller((BankTellerRole)roles.get(2));
  		((BankHostRole)roles.get(5)).addTeller((BankTellerRole)roles.get(3));
  		((BankTellerRole)roles.get(2)).bh = ((BankHostRole)roles.get(5));
  		((BankTellerRole)roles.get(3)).bh = ((BankHostRole)roles.get(5));
  		
  		// Setting bank database for all tellers
  		for (Role r : roles) {
  			if (r instanceof BankTellerRole) {
  				((BankTellerRole)r).bd = bankdatabase;
  			}
  		}
        
  		// Setting market employee to market cashier & vice versa
  		((MarketCashierRole)roles.get(6)).addEmployee((MarketEmployeeRole)roles.get(8));
  		((MarketEmployeeRole)roles.get(8)).setCashier((MarketCashierRole)roles.get(6));
  		((MarketCashierRole)roles.get(7)).addEmployee((MarketEmployeeRole)roles.get(9));
  		((MarketEmployeeRole)roles.get(9)).setCashier((MarketCashierRole)roles.get(7));
        
		locations.add(bank);
		locations.add(bank2);
		locations.add(market);
		locations.add(market2);
		locations.add(rest1);
		locations.add(rest2);
		locations.add(rest4);
		locations.add(rest5);
		locations.add(rest6);
		locations.add(home);
		locations.add(home2);
		locations.add(home3);
		
		for (Location l : locations) {
			cityAnimPanel.addLocation(l);
		}
		
		/**
		 * ADDING EVENTS TO EACH PERSON
		 */
		// For the bank tellers
		SimEvent tellerGoToBank = new SimEvent("Go to work", bank, EventType.TellerEvent);
		SimEvent teller2GoToBank = new SimEvent("Go to work", bank, EventType.TellerEvent);
		SimEvent tellerGoToBank2 = new SimEvent("Go to work", bank2, EventType.TellerEvent);
		SimEvent teller2GoToBank2 = new SimEvent("Go to work", bank2, EventType.TellerEvent);
		
		// For the bank hosts
		SimEvent hostGoToBank = new SimEvent("Go to work", bank, EventType.HostEvent);
		SimEvent hostGoToBank2 = new SimEvent("Go to work", bank2, EventType.HostEvent);
		
		// For the market cashiers
		SimEvent cashierGoToMarket = new SimEvent("Go to work", market, EventType.CashierEvent);
		SimEvent cashierGoToMarket2 = new SimEvent("Go to work", market2, EventType.CashierEvent);
		
		// For the market employees
		SimEvent employeeGoToMarket = new SimEvent("Go to work", market, EventType.EmployeeEvent);
		SimEvent employeeGoToMarket2 = new SimEvent("Go to work", market2, EventType.EmployeeEvent);
		
		// For the first restaurant
		SimEvent hostGoToRestaurant = new SimEvent("Go to work", rest1, EventType.HostEvent);
		SimEvent cookGoToRestaurant = new SimEvent("Go to work", rest1, EventType.CookEvent);
		SimEvent cashierGoToRestaurant = new SimEvent("Go to work", rest1,EventType.CashierEvent);
		SimEvent sdWaiterGoToRestaurant2 = new SimEvent("Go to work", rest1, EventType.SDWaiterEvent);
		SimEvent waiterGoToRestaurant = new SimEvent("Go to work", rest1, EventType.WaiterEvent);
		
		// For the second restaurant
		SimEvent host2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.HostEvent);
		SimEvent cook2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.CookEvent);
		SimEvent cashier2GoToRestaurant = new SimEvent("Go to work", rest2,EventType.CashierEvent);
		SimEvent sdWaiter2GoToRestaurant2 = new SimEvent("Go to work", rest2, EventType.SDWaiterEvent);
		SimEvent waiter2GoToRestaurant = new SimEvent("Go to work", rest2, EventType.WaiterEvent);
		
		// For the fourth restaurant
		SimEvent host4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.HostEvent);
		SimEvent cook4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.CookEvent);
		SimEvent cashier4GoToRestaurant = new SimEvent("Go to work", rest4,EventType.CashierEvent);
		SimEvent sdWaiter4GoToRestaurant2 = new SimEvent("Go to work", rest4, EventType.SDWaiterEvent);
		SimEvent waiter4GoToRestaurant = new SimEvent("Go to work", rest4, EventType.WaiterEvent);
		
		// For the fifth restaurant
		SimEvent host5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.HostEvent);
		SimEvent cook5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.CookEvent);
		SimEvent cashier5GoToRestaurant = new SimEvent("Go to work", rest5,EventType.CashierEvent);
		SimEvent sdWaiter5GoToRestaurant2 = new SimEvent("Go to work", rest5, EventType.SDWaiterEvent);
		SimEvent waiter5GoToRestaurant = new SimEvent("Go to work", rest5, EventType.WaiterEvent);
		
		// For the sixth restaurant
		SimEvent host6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.HostEvent);
		SimEvent cook6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.CookEvent);
		SimEvent cashier6GoToRestaurant = new SimEvent("Go to work", rest6,EventType.CashierEvent);
		SimEvent sdWaiter6GoToRestaurant2 = new SimEvent("Go to work", rest6, EventType.SDWaiterEvent);
		SimEvent waiter6GoToRestaurant = new SimEvent("Go to work", rest6, EventType.WaiterEvent);
		
		people.get(0).msgAddEvent(tellerGoToBank);
		people.get(1).msgAddEvent(teller2GoToBank);
		people.get(2).msgAddEvent(tellerGoToBank2);
		people.get(3).msgAddEvent(teller2GoToBank2);
		people.get(4).msgAddEvent(hostGoToBank);
		people.get(5).msgAddEvent(hostGoToBank2);
		people.get(6).msgAddEvent(cashierGoToMarket);
		people.get(7).msgAddEvent(cashierGoToMarket2);
		people.get(8).msgAddEvent(employeeGoToMarket);
		people.get(9).msgAddEvent(employeeGoToMarket2);
		people.get(10).msgAddEvent(hostGoToRestaurant);
		people.get(11).msgAddEvent(cookGoToRestaurant);
		people.get(12).msgAddEvent(cashierGoToRestaurant);
		people.get(13).msgAddEvent(sdWaiterGoToRestaurant2);
		people.get(14).msgAddEvent(waiterGoToRestaurant);
		people.get(15).msgAddEvent(host2GoToRestaurant);
		people.get(16).msgAddEvent(cook2GoToRestaurant);
		people.get(17).msgAddEvent(cashier2GoToRestaurant);
		people.get(18).msgAddEvent(sdWaiter2GoToRestaurant2);
		people.get(19).msgAddEvent(waiter2GoToRestaurant);
		people.get(20).msgAddEvent(host4GoToRestaurant);
		people.get(21).msgAddEvent(cook4GoToRestaurant);
		people.get(22).msgAddEvent(cashier4GoToRestaurant);
		people.get(23).msgAddEvent(sdWaiter4GoToRestaurant2);
		people.get(24).msgAddEvent(waiter4GoToRestaurant);
		people.get(25).msgAddEvent(host5GoToRestaurant);
		people.get(26).msgAddEvent(cook5GoToRestaurant);
		people.get(27).msgAddEvent(cashier5GoToRestaurant);
		people.get(28).msgAddEvent(sdWaiter5GoToRestaurant2);
		people.get(29).msgAddEvent(waiter5GoToRestaurant);
		people.get(30).msgAddEvent(host6GoToRestaurant);
		people.get(31).msgAddEvent(cook6GoToRestaurant);
		people.get(32).msgAddEvent(cashier6GoToRestaurant);
		people.get(33).msgAddEvent(sdWaiter6GoToRestaurant2);
		people.get(34).msgAddEvent(waiter6GoToRestaurant);
		
		// This is to start all the people's threads
//				for (int j = 0; j < 14; ++j) {
//					people.get(j).startThread();
//				}
		
//				BankCustomerRole bankCust = new BankCustomerRole(walking, "Bank Customer");
//				walking.addRole(bankCust, "Bank Customer");
		
		/**
		 * GIVE WALKING PERSON EVENTS
		 */
		// Go home to eat
		SimEvent eatAtHome = new SimEvent("Eat", home, EventType.HomeOwnerEvent);
		SimEvent eatAtHome2 = new SimEvent("Eat", home2, EventType.HomeOwnerEvent);
		SimEvent eatAtHome3 = new SimEvent("Eat", home3, EventType.HomeOwnerEvent);
		
		// Go to bank to withdraw money
		SimEvent withdrawFromBank1 = new SimEvent("deposit", bank, EventType.CustomerEvent);
		
		// Go to market to buy shopping list items
		SimEvent goToMarket1 = new SimEvent("Buy groceries", market, EventType.CustomerEvent);
		
		// Eat at each restaurant.. fatty.
		SimEvent goToRest1 = new SimEvent("Eat", rest1, EventType.CustomerEvent);
		SimEvent goToRest2 = new SimEvent("Eat", rest2, EventType.CustomerEvent);
		SimEvent goToRest4 = new SimEvent("Eat", rest4, EventType.CustomerEvent);
		SimEvent goToRest5 = new SimEvent("Eat", rest5, EventType.CustomerEvent);
		SimEvent goToRest6 = new SimEvent("Eat", rest6, EventType.CustomerEvent);
		
		// Go to market to buy more shopping list items
		SimEvent goToMarket2 = new SimEvent("Buy groceries", market2, EventType.CustomerEvent);
		
		// Go to bank to deposit money
		SimEvent depositBank2 = new SimEvent("deposit", bank2, EventType.CustomerEvent);
		
		walking.msgAddEvent(eatAtHome);
		walking.msgAddEvent(withdrawFromBank1);
		walking.msgAddEvent(goToMarket1);
		walking.msgAddEvent(goToRest1);
		walking.msgAddEvent(goToRest2);
		walking.msgAddEvent(goToRest4);
		walking.msgAddEvent(goToRest5);
		walking.msgAddEvent(goToRest6);
		walking.msgAddEvent(goToMarket2);
		walking.msgAddEvent(depositBank2);
		
		/**
		 * GIVE BUS PERSON EVENTS
		 */		
		busPerson.msgAddEvent(eatAtHome2);
		busPerson.msgAddEvent(withdrawFromBank1);
		busPerson.msgAddEvent(goToMarket2);
		busPerson.msgAddEvent(goToMarket1);
		busPerson.msgAddEvent(depositBank2);
		busPerson.msgAddEvent(goToRest1);
		busPerson.msgAddEvent(goToRest4);
		busPerson.msgAddEvent(goToRest2);
		busPerson.msgAddEvent(goToRest5);
		//busPerson.msgAddEvent(goToRest3); FIX
		busPerson.msgAddEvent(goToRest6);
		
		/**
		 * GIVE CAR PERSON EVENTS
		 */		
		carPerson.msgAddEvent(eatAtHome3);
		carPerson.msgAddEvent(withdrawFromBank1);
		carPerson.msgAddEvent(goToMarket1);
		carPerson.msgAddEvent(goToRest1);
		carPerson.msgAddEvent(goToRest2);
		carPerson.msgAddEvent(goToRest4);
		carPerson.msgAddEvent(goToRest5);
		carPerson.msgAddEvent(goToRest6);
		carPerson.msgAddEvent(goToMarket2);
		carPerson.msgAddEvent(depositBank2);
		
		for (PersonAgent p : people) {
			p.setcitygui(simCityGui);
			p.populateCityMap(locations);
		}
		
		//FIX.. FOR TESTING PURPOSES
		// This is to initialize all the city maps
//				for (int j = 0; j < 14; ++j) {
//					people.get(j).populateCityMap(locations);
//				}
		
		// This is to add all the bus stops to the city maps
//				for (int j = 0; j < 14; ++j) {
//					people.get(j).getMap().addBusStop(simCityGui.busstop1.name, simCityGui.busstop1);
//					people.get(j).getMap().addBusStop(simCityGui.busstop2.name, simCityGui.busstop2);
//					people.get(j).getMap().addBusStop(simCityGui.busstop3.name, simCityGui.busstop3);
//					people.get(j).getMap().addBusStop(simCityGui.busstop4.name, simCityGui.busstop4);
//					people.get(j).getMap().addBusStop(simCityGui.busstop5.name, simCityGui.busstop5);
//					people.get(j).getMap().addBusStop(simCityGui.busstop6.name, simCityGui.busstop6);
//					people.get(j).getMap().addBusStop(simCityGui.busstop7.name, simCityGui.busstop7);
//					people.get(j).getMap().addBusStop(simCityGui.busstop8.name, simCityGui.busstop8);
//					
//					people.get(j).getMap().addBus(simCityGui.busstop1, simCityGui.bus);
//					people.get(j).getMap().addBus(simCityGui.busstop2, simCityGui.bus);
//					people.get(j).getMap().addBus(simCityGui.busstop3, simCityGui.bus);
//					people.get(j).getMap().addBus(simCityGui.busstop4, simCityGui.bus);
//					people.get(j).getMap().addBus(simCityGui.busstop5, simCityGui.bus2);
//					people.get(j).getMap().addBus(simCityGui.busstop6, simCityGui.bus2);
//					people.get(j).getMap().addBus(simCityGui.busstop7, simCityGui.bus2);
//					people.get(j).getMap().addBus(simCityGui.busstop8, simCityGui.bus2);
//				}
		
		for (PersonAgent p : people) {
			p.getMap().addBusStop(simCityGui.busstop1.name, simCityGui.busstop1);
			p.getMap().addBusStop(simCityGui.busstop2.name, simCityGui.busstop2);
			p.getMap().addBusStop(simCityGui.busstop3.name, simCityGui.busstop3);
			p.getMap().addBusStop(simCityGui.busstop4.name, simCityGui.busstop4);
			p.getMap().addBusStop(simCityGui.busstop5.name, simCityGui.busstop5);
			p.getMap().addBusStop(simCityGui.busstop6.name, simCityGui.busstop6);
			p.getMap().addBusStop(simCityGui.busstop7.name, simCityGui.busstop7);
			p.getMap().addBusStop(simCityGui.busstop8.name, simCityGui.busstop8);
			
			p.getMap().addBus(simCityGui.busstop1, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop2, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop3, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop4, simCityGui.bus);
			p.getMap().addBus(simCityGui.busstop5, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop6, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop7, simCityGui.bus2);
			p.getMap().addBus(simCityGui.busstop8, simCityGui.bus2);
		}
		
//				for(int j = 0; j < 25; j++){
//					people.get(j).startThread();
//				}

		for (PersonAgent p : people) {
			p.startThread();
		}
		
		// Starts the thread of each timecard
		bank.getTimeCard().startThread();
		bank2.getTimeCard().startThread();
		market.getTimeCard().startThread();
		market2.getTimeCard().startThread();
		rest1.getTimeCard().startThread();
		rest2.getTimeCard().startThread();
		rest4.getTimeCard().startThread();
		rest5.getTimeCard().startThread();
		rest6.getTimeCard().startThread();
		
		clock.timeCards.add(bank.getTimeCard());
		clock.timeCards.add(market.getTimeCard());
		clock.timeCards.add(market2.getTimeCard());
		clock.timeCards.add(rest1.getTimeCard());
		clock.timeCards.add(rest2.getTimeCard());
		clock.timeCards.add(rest4.getTimeCard());
		clock.timeCards.add(rest5.getTimeCard());
		clock.timeCards.add(rest6.getTimeCard());
	}
////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////ROBBER SCENARIO///////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
	private void robberScenario(){
		// Create the robber
		PersonAgent robber = new PersonAgent("Robber", cityMap, 0);
		robber.walking = true;
		PersonGui pgui = new PersonGui(robber, 330, 40, this.cityAnimPanel);
		robber.gui = pgui;
		//people.add(robber);
		//peopleGuis.add(robber.gui);
		cityAnimPanel.addGui(robber.gui);
		robber.setAnimationPanel(cityAnimPanel);
		
		robber.populateCityMap(locations);
		SimEvent robBank = new SimEvent("robBank", robber.cityMap.pickABank(robber.gui.xPos, robber.gui.yPos), EventType.CustomerEvent);
		if(robBank.location == null){
			System.err.println("All banks closed - ending scenario");
			people.remove(robber);
			peopleGuis.remove(robber.gui);
			cityAnimPanel.removeGui((Gui)robber.gui);
			return;
		}
		else
			robber.msgAddEvent(robBank);
		robber.gui.setPresent(true);
		robber.startThread();
}
////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////WEEKEND SCENARIO//////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////
	/**
	 * Weekend behavior scenario : People will go to the casino 
	 * one person will win enough money to go buy a car after playing
	 */
	public void weekendBehaviorScenario() {
		
	}
}
