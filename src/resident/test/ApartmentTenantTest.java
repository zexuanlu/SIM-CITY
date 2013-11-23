package resident.test;

import resident.ApartmentTenantAgent;
import resident.ApartmentTenantAgent.MyPriority;
import resident.test.mock.MockApartmentLandlord;
import resident.test.mock.MockPerson;
import junit.framework.TestCase;

public class ApartmentTenantTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	MockPerson person;
	ApartmentTenantAgent tenant;
	MockApartmentLandlord landlord;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		person = new MockPerson("Mock Person");
		tenant = new ApartmentTenantAgent("Tenant", 1, person);		
		landlord = new MockApartmentLandlord("Mock Apartment Landlord");	
	}
	
	/**
	 * Tests the apartment tenant paying rent in the normative scenario (isolated, no other tasks)
	 */
	public void testPayingRentNormative() {
		System.out.println("Testing the tenant paying the landlord with no other tasks to complete");
		
		tenant.setMoney(300);
		
		tenant.setLandlord(landlord);
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has one logged entry
		assertEquals("Tenant has 1 logged entry now.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 1 task now.", 1, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has no tasks
		assertEquals("Tenant has 0 tasks now.", 0, tenant.toDoList.size());
		
		// Checks that the tenant has one more logged entry
		assertEquals("Tenant has 2 logged entries now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Paying the landlord $100"));
		
		// Check that the landlord has received the rent
		assertEquals("Mock landlord should have 1 logged entry.", 1, landlord.log.size());
		assertTrue(landlord.log.getLastLoggedEvent().toString().contains("Received rent of $100.0 from Tenant"));
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(0);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 3 logged entries now.", 3, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $0"));
		
		// Tenant still has no tasks to do
		assertEquals("Tenant has no tasks.", 0, tenant.toDoList.size());
		
		// Scheduler should return false
		assertFalse(tenant.pickAndExecuteAnAction());
	}
	/**
	 * Tests the apartment tenant not having enough money to pay rent
	 */
	public void testNotEnoughToPayRent() {
		System.out.println("Testing the tenant paying the landlord with no other tasks to complete");
		
		tenant.setMoney(10);
		
		tenant.setLandlord(landlord);
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Tenant has no logged events. It doesn't.", 0, tenant.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Tenant has no tasks in to do list. It doesn't.", 0, tenant.toDoList.size());
		
		// Sends a message to the apartment tenant telling it to pay rent
		tenant.msgPayRent();
		
		// Check that tenant has one logged entry
		assertEquals("Tenant has 1 logged entry now.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("It's been a day. Time to pay rent."));
		
		// Check that to do list now has pay rent as a task
		assertEquals("Tenant has 1 task now.", 1, tenant.toDoList.size());
		assertEquals("Tenant has task of paying rent.", tenant.toDoList.get(0).task, MyPriority.Task.PayRent);
		
		// Invokes the tenant's scheduler and makes sure that it returns true
		assertTrue(tenant.pickAndExecuteAnAction());
		
		// Check that to do list has no tasks
		assertEquals("Tenant has 0 tasks now.", 0, tenant.toDoList.size());
		
		// Checks that the tenant has one more logged entry
		assertEquals("Tenant has 2 logged entries now.", 2, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("Paying the landlord $10.0, because I don't have enough."));
		
		// Check that the landlord has received the rent
		assertEquals("Mock landlord should have 1 logged entry.", 1, landlord.log.size());
		assertTrue(landlord.log.getLastLoggedEvent().toString().contains("Received rent of $10.0 from Tenant"));
		
		// Message from the landlord telling the tenant that rent was received, debt is 0 so landlord passes 0 to message
		tenant.msgReceivedRent(10);
		
		// Tenant should have 1 more logged entry
		assertEquals("Tenant has 3 logged entries now.", 3, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("I now have debt of $10"));
		
		// Tenant still has no tasks to do
		assertEquals("Tenant has no tasks.", 0, tenant.toDoList.size());
		
		// Makes sure that the scheduler returns false, since the tenant has nothing to do
		assertFalse(tenant.pickAndExecuteAnAction());		
	}
}
