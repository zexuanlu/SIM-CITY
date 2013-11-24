package resident.test;

import java.text.DecimalFormat; 

import resident.MaintenancePersonRole;
import resident.MaintenancePersonRole.MyCustomer;
import resident.test.mock.MockHomeOwner;
import resident.test.mock.MockPerson;
import junit.framework.TestCase;

public class MaintenancePersonTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	MaintenancePersonRole housekeeper;
	MockHomeOwner homeOwner;
	MockPerson person;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new MockPerson("Mock Person");
		housekeeper = new MaintenancePersonRole("Maintenance Person", person);	
		homeOwner = new MockHomeOwner("Mock HomeOwner", 1);		
	} 
	
	/**
	 * This tests the home owner's normative interaction with the maintenance person
	 */
	public void testNormativeMaintenance() {
		DecimalFormat df = new DecimalFormat("###.##");
		
		double money = 300;
		
		System.out.println("Testing Housekeeper Maintaining Home - Normative Scenario");
		
		housekeeper.setMaintenanceCost(50);
		
		homeOwner.myMoney = money;
		
		// Preconditions: before home owner gets hungry message
		assertEquals("Housekeeper has no logged events. It doesn't.", 0, housekeeper.log.size());
		
		// Preconditions: housekeeper doesn't have any customers
		assertEquals("Housekeeper has no customers yet.", 0, housekeeper.homesToBeMaintained.size());
		
		// Preconditions: home owner doesn't have any logs
		assertEquals("Home owner has no logged events.", 0, homeOwner.log.size());
		
		// Messages the housekeeper to come maintain a home
		housekeeper.msgPleaseComeMaintain(homeOwner, 1);
		
		// Checks to make sure housekeeper has one log
		assertEquals("Housekeeper has one logged event now.", 1, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Just received a call from Mock HomeOwner to go maintain house 1"));
		
		// Checks that the housekeeper now has one customer in list
		assertEquals("Housekeeper's customer list size is now 1.", 1, housekeeper.homesToBeMaintained.size());
		assertEquals("Housekeeper's customer is the mock home owner.", homeOwner, housekeeper.homesToBeMaintained.get(0).customer);
		assertEquals("Housekeeper's customer's state is now needs maintenance.", housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.NeedsMaintenance);
		
		// Invokes the scheduler and makes sure that it returns true because there is an action
		assertTrue(housekeeper.pickAndExecuteAnAction());
		
		// Checks that customer has state of in maintenance
		assertEquals("Housekeeper's customer's state is now going to maintain.", housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.GoingToMaintain);
		
		// Checks that the housekeeper has 2 logged entries now
		assertEquals("Housekeeper has 2 logged entries now.", 2, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Going to maintain home.."));

		// Checks that the home owner has logged housekeeper's arrival
		assertEquals("Home owner has 1 logged event.", 1, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Received message that the housekeeper is ready to maintain home."));
		
		// Message from the home owner saying come in
		housekeeper.msgPleaseComeIn(homeOwner, 1);
		
		// Checks that the housekeeper now has 3 logs
		assertEquals("Housekeeper has 3 logged entries now.", 3, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Going into customer Mock HomeOwner's house to maintain."));
		
		// Checks that the housekeeper's customer's state is InMaintenance
		assertEquals("Housekeeper's customer state is In Maintenance.", housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.InMaintenance);
		
		// Invokes the scheduler and makes sure that it returns true because there is an action
		assertTrue(housekeeper.pickAndExecuteAnAction());
		
		// Checks that the customer's state is now Maintained
		assertEquals(housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.Maintained);
		
		// Checks that the home owner's log has one more entry
		assertEquals("Housekeeper has 4 logged entries now.", 4, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Maintaining home!"));
					
		// Invokes the scheduler and makes sure that it returns true because there is an action
		assertTrue(housekeeper.pickAndExecuteAnAction());
		
		// Checks that the home owner's log has one more entry
		assertEquals("Housekeeper has 5 logged entries now.", 5, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Finished maintaining Mock HomeOwner's home!"));
	
		// Checks that the customer's state is NeedsToPay
		assertEquals("Customer's state should be NeedsToPay.", housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.NeedsToPay);
		
		// Checks that the customer's amount owed is 50
		assertEquals("Customer's amount owed should be $50.", df.format(housekeeper.homesToBeMaintained.get(0).amountOwed), df.format(50));
		
		// Checks that the home owner has received message from housekeeper 
		assertEquals("Home owner has 2 logged events.", 2, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("Received message that housekeeper is done maintaining my home. I now have to pay 50."));
				
		// Messages the housekeeper the payment
		housekeeper.msgHereIsThePayment(homeOwner, 50);
		
		// Checks that the housekeeper has one more logged entry
		assertEquals("Housekeeper has 6 logged entries now.", 6, housekeeper.log.size());
		assertEquals("Home owner should have 0 amount owed now.", df.format(housekeeper.homesToBeMaintained.get(0).amountOwed), df.format(0));
		assertEquals("Home owner's customer should have state of paid.", housekeeper.homesToBeMaintained.get(0).state, MyCustomer.MyCustomerState.Paid);
		assertEquals("Home owner has amount 50 paid.", df.format(housekeeper.homesToBeMaintained.get(0).amountPaid), df.format(50));
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("Just received payment of 50.0 from customer Mock HomeOwner"));
		
		// Invokes the scheduler and makes sure that it returns true because there is an action
		assertTrue(housekeeper.pickAndExecuteAnAction());
		
		// Checks that the housekeeper has one more logged entry
		assertEquals("Housekeeper has 7 logged entries now.", 7, housekeeper.log.size());
		assertTrue(housekeeper.log.getLastLoggedEvent().toString().contains("I received the customer's payment of 50.0."));
		
		// Checks that the home owner has one more logged entry
		assertEquals("Home owner has 3 logged events.", 3, homeOwner.log.size());
		assertTrue(homeOwner.log.getLastLoggedEvent().toString().contains("My debt is now 0."));
	}
}
