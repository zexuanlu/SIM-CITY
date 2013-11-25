package resident;

import gui.MaintenanceGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.Location;
import person.Position;
import person.interfaces.Person;
import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Agent;
import agent.Role;

public class MaintenancePersonRole extends Agent implements MaintenancePerson {
	/**
	 * Data for MaintenancePerson
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public MaintenancePersonRole(String n, Person p) {
		super();
		name = n;
		person = p;
	}
	
	public String getName() {
		return name;
	}
	
	private Person person;
	
	public static class MyCustomer {
		public HomeOwner customer;
		private int houseNumber;
		public enum MyCustomerState {NeedsMaintenance, GoingToMaintain, InMaintenance, Maintaining, Maintained, NeedsToPay, Paid}
		public MyCustomerState state;
		public double amountOwed;
		public double amountPaid;

		MyCustomer(HomeOwner h, int n) {
			customer = h;
			houseNumber = n;
			state = MyCustomerState.NeedsMaintenance;
		}
	}

	public List<MyCustomer> homesToBeMaintained = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private double maintenanceCost;
	private Timer maintenanceTimer = new Timer();
	
	private MaintenanceGui maintainGui;
	
	public void setGui(MaintenanceGui m) {
		maintainGui = m;
	}
	
	// Getter for maintenance cost
	public double getMaintenanceCost() {
		return maintenanceCost;
	}
	
	// Setter for maintenance cost
	public void setMaintenanceCost(double n) {
		maintenanceCost = n;
	}
	
	private String name;
	private double myMoney;
	
	// Semaphores for housekeeper
	private Semaphore atFridge = new Semaphore(0, true);
	private Semaphore atFrontDoor = new Semaphore(0, true);
	private Semaphore atTable = new Semaphore(0, true);
	private Semaphore atSink = new Semaphore(0, true);
	private Semaphore atStove = new Semaphore(0, true);
	private Semaphore atBed = new Semaphore(0, true);
	
	/**
	 * Messages for MaintenancePerson
	 * 
	 */
	public void msgPleaseComeMaintain(HomeOwner cust, int houseNumber) {
		log.add(new LoggedEvent("Just received a call from " + cust.getName() + " to go maintain house " + houseNumber));
		print("Just received a call from " + cust.getName() + "to go maintain house " + houseNumber);
		homesToBeMaintained.add(new MyCustomer(cust, houseNumber));
		stateChanged();
	}
	
	public void msgPleaseComeIn(HomeOwner homeOwnerAgent, int houseNumber) {
		for (MyCustomer c : homesToBeMaintained) {
			if (c.customer == homeOwnerAgent) {
				c.state = MyCustomer.MyCustomerState.InMaintenance;
				log.add(new LoggedEvent("Going into customer " + c.customer.getName() + "'s house to maintain."));
				print("Going into customer " + c.customer.getName() + "'s house to maintain.");
				stateChanged();
			}
		}
	}

	public void msgHereIsThePayment(HomeOwner cust, double amount) {
		for (MyCustomer c : homesToBeMaintained) {
			if (c.customer == cust) {
				c.state = MyCustomer.MyCustomerState.Paid;
				c.amountPaid = amount;
				c.amountOwed = maintenanceCost - amount;
				log.add(new LoggedEvent("Just received payment of " + c.amountPaid + " from customer " + c.customer.getName()));
				print("Just received payment of " + c.amountPaid + " from customer " + c.customer.getName());
				stateChanged();
			}
		}
	}
	
	// For when the housekeeper is at the fridge
	public void msgAtFridge() {
		atFridge.release();
	}
	
	// For when housekeeper is at the door
	public void msgAtDoor() {
		atFrontDoor.release();
	}
	
	// For when the housekeeper is at the stove
	public void msgAtStove() {
		atStove.release();
	}
	
	// For when the housekeeper has reached the dining table
	public void msgAtTable() {
		atTable.release();
	}
	
	// For when the housekeeper has reached the sink
	public void msgAtSink() {
		atSink.release();
	}
	
	// For when the housekeeper has reached the bed
	public void msgAtBed() {
		atBed.release();
	}


	/**
	 * Scheduler for MaintenancePerson
	 * 
	 */
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.NeedsMaintenance) {
				goToHouse(c);
				return true;
			}
		}
		
		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.InMaintenance) {
				maintainHouse(c);
				return true;
			}
		}

		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.Maintained) {
				letCustomerKnow(c);
				return true;
			}
		}

		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.Paid) {
				tellCustomerReceivedPayment(c);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Actions for MaintenancePerson
	 */
	private void goToHouse(MyCustomer c) {
		log.add(new LoggedEvent("Going to maintain home.."));
		print("Going to maintain home..");
		
		Location location = new Location("Customer's Home", Location.LocationType.Home, new Position(20,20));
		
		// GUI goes to customer's home, lets person agent know that no longer going to be a resident role
		//person.msgAddEvent(new Event("Go to customer's home", location, 2, EventType.MaintenanceEvent));
		
		maintainGui.DoGoToFrontDoor(); // This is temporary. Will change once we have the full GUI up
		
		c.state = MyCustomer.MyCustomerState.GoingToMaintain;
		
		c.customer.msgReadyToMaintain();
	}
	
	private void maintainHouse(final MyCustomer c) {
		log.add(new LoggedEvent("Maintaining home!"));
		print("Maintaining home!");
		
		c.state = MyCustomer.MyCustomerState.Maintaining;
		
		maintainGui.DoGoToBed();
		try {
			atBed.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		maintainGui.DoGoToFridge();
		try {
			atFridge.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		maintainGui.DoGoToSink();
		try {
			atSink.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		maintainGui.DoGoToStove();
		try {
			atStove.acquire(); 
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		maintainGui.DoGoToTable();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Timer to maintain home
        maintenanceTimer.schedule(new TimerTask() 
        {
            public void run() 
            {
            	c.state = MyCustomer.MyCustomerState.Maintained;
            	maintainGui.DoGoToFrontDoor();
            	try {
					atFrontDoor.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	print("Done maintaining home!");
            	stateChanged();
            }
        }, 5000);	
		
	}

	private void letCustomerKnow(MyCustomer c) {		
		log.add(new LoggedEvent("Finished maintaining " + c.customer.getName() + "'s home!"));
		print("Finished maintaining " + c.customer.getName() + "'s home!");
		c.customer.msgDoneMaintaining(maintenanceCost);
		c.state = MyCustomer.MyCustomerState.NeedsToPay;
		c.amountOwed = maintenanceCost;
	}

	private void tellCustomerReceivedPayment(MyCustomer c) {
		c.customer.msgReceivedPayment(c.amountOwed);
		log.add(new LoggedEvent("I received the customer's payment of " + c.amountPaid + "."));
		print("I received the customer's payment of " + c.amountPaid + ".");
		homesToBeMaintained.remove(c);
	}
}
