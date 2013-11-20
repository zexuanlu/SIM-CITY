package resident;

import java.util.*;

import resident.interfaces.HomeOwner;
import resident.interfaces.MaintenancePerson;
import resident.test.mock.EventLog;
import resident.test.mock.LoggedEvent;
import agent.Role;

public class MaintenancePersonAgent extends Role implements MaintenancePerson {
	/**
	 * Data for MaintenancePerson
	 *
	 */
	// For the purposes of JUnit testing
	public EventLog log = new EventLog();
	
	// Constructor
	public MaintenancePersonAgent(String n) {
		super();
		name = n;
	}
	
	public String getName() {
		return name;
	}
	
	private static class MyCustomer {
		private HomeOwner customer;
		private int houseNumber;
		public enum MyCustomerState {NeedsMaintenance, InMaintenance, Maintained, NeedsToPay, Paid}
		private MyCustomerState state;
		private double amountOwed;
		private double amountPaid;

		MyCustomer(HomeOwner h, int n) {
			customer = h;
			houseNumber = n;
			state = MyCustomerState.NeedsMaintenance;
		}
	}

	private List<MyCustomer> homesToBeMaintained = Collections.synchronizedList(new ArrayList<MyCustomer>());
	private double maintenanceCost;

	private String name;
	private double myMoney;
	
	/**
	 * Messages for MaintenancePerson
	 * 
	 */
	public void msgPleaseComeMaintain(HomeOwner cust, int houseNumber) {
		log.add(new LoggedEvent("Just received a call from " + cust.getName() + "to go maintain house " + houseNumber));
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

	public void msgFinishedMaintenance(MyCustomer c) {
		log.add(new LoggedEvent("Finished maintaining " + c.customer.getName() + "'s house."));
		print("Finished maintaining " + c.customer.getName() + "'s house.");
		c.state = MyCustomer.MyCustomerState.Maintained;
		stateChanged();
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

	/**
	 * Scheduler for MaintenancePerson
	 * 
	 */
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		for (MyCustomer c : homesToBeMaintained) {
			if (c.state == MyCustomer.MyCustomerState.NeedsMaintenance) {
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
	private void maintainHouse(MyCustomer c) {
		log.add(new LoggedEvent("Maintaining home.."));
		print("Maintaining home..");
//		DoGoToCustomerHouse(c);
		// Semaphore to indicate when at customer's house
		//c.state = MyCustomer.MyCustomerState.InMaintenance;
//		maintenanceTimer.start{msgFinishedMaintenance(c)};
	}

	private void letCustomerKnow(MyCustomer c) {
		log.add(new LoggedEvent("Finished maintaining " + c.customer.getName() + "'s home!"));
		print("Finished maintaining " + c.customer.getName() + "'s home!");
		c.customer.msgDoneMaintaining(maintenanceCost);
		c.state = MyCustomer.MyCustomerState.NeedsToPay;
		c.amountOwed = maintenanceCost;
	}

	private void tellCustomerReceivedPayment(MyCustomer c) {
		c.customer.msgReceivedPayment(maintenanceCost - c.amountPaid);
		log.add(new LoggedEvent("I received the customer's payment of " + (maintenanceCost - c.amountPaid) + "."));
		homesToBeMaintained.remove(c);
	}
}
