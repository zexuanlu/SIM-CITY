package resident.test;

import resident.ApartmentLandlordAgent;
import resident.ApartmentLandlordAgent.MyTenant;
import resident.test.mock.MockApartmentTenant;
import resident.test.mock.MockPerson;
import junit.framework.TestCase;

public class ApartmentLandlordTest extends TestCase {
	// These are instantiated for each test separately via the setUp() method.
	ApartmentLandlordAgent landlord;
	MockApartmentTenant tenant;
	MockPerson person;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		
		person = new MockPerson("Mock Person");
		landlord = new ApartmentLandlordAgent("Landlord", 1, person);		
		tenant = new MockApartmentTenant("Mock Apartment Tenant", 2);	
	}
	
	/**
	 * Tests the tenant paying the landlord enough money for rent
	 */
	public void testTenantPaymentNormative() {
		System.out.println("Tests the landlord receiving payment from the tenant");
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Landlord has no logged events. It doesn't.", 0, landlord.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Landlord has no tasks in to do list. It doesn't.", 0, landlord.tenants.size());
		
		// Preconditions: tenant has no logs
		assertEquals("Tenant has no logged entries.", 0, tenant.log.size());
		
		// Messages landlord a new tenant
		landlord.msgNewTenant(tenant, tenant.aptNum);
		
		// Check that landlord's list now contains a new tenant and the correct information
		assertEquals("Landlord now has 1 tenant.", 1, landlord.tenants.size());
		assertEquals("Landlord has tenant's correct information.", "Mock Apartment Tenant", landlord.tenants.get(0).aptRes.getName());
		assertEquals("Landlord has tenant's correct information.", 2, landlord.tenants.get(0).apartmentNumber);
		
		// Messages the landlord the rent from tenant
		landlord.msgHereIsTheRent(tenant, 100);
		
		// Checks that the tenant's state is now set to paying rent
		assertEquals("Tenant's state is now paying rent.", MyTenant.TenantState.PayingRent, landlord.tenants.get(0).state);
		
		// Checks that the tenant's amount paying is the full amount
		assertEquals("Tenant's amount paying is $100.", 100.0, landlord.tenants.get(0).amountPaying);
		
		// Checks that landlord has one logged entry
		assertEquals("Landlord has 1 logged entry now.", 1, landlord.log.size());
		assertTrue(landlord.log.getLastLoggedEvent().toString().contains("Received payment of $100"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(landlord.pickAndExecuteAnAction());
		
		// Checks that the tenant's status is now paid
		assertEquals("Tenant's state is now paid.", MyTenant.TenantState.Paid, landlord.tenants.get(0).state);
		assertEquals("Tenant doesn't owe any money.", 0.0, landlord.tenants.get(0).amountOwed);
		
		// Checks that tenant received message from landlord
		assertEquals("Tenant has one logged entry.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("The landlord received my rent. I owe $0"));
		
		// Checks that the person agent has received finish role message
		assertEquals("Person agent has one logged entry.", 1, person.log.size());
		assertTrue(person.log.getLastLoggedEvent().toString().contains("You've finished your role and have returned to your PersonAgent"));
		
		// Invokes scheduler and makes sure that it returns true
		assertFalse(landlord.pickAndExecuteAnAction());	
	}
	
	/**
	 * Tests the tenant not having enough money to pay the landlord for rent
	 */
	public void testTenantNotPayingEnoughRent() {
		System.out.println("Tests the landlord receiving payment from the tenant");
		
		// Preconditions: before tenant gets pay rent message
		assertEquals("Landlord has no logged events. It doesn't.", 0, landlord.log.size());
		
		// Preconditions: check to make sure home owner has no tasks
		assertEquals("Landlord has no tasks in to do list. It doesn't.", 0, landlord.tenants.size());
		
		// Preconditions: tenant has no logs
		assertEquals("Tenant has no logged entries.", 0, tenant.log.size());
		
		// Messages landlord a new tenant
		landlord.msgNewTenant(tenant, tenant.aptNum);
		
		// Check that landlord's list now contains a new tenant and the correct information
		assertEquals("Landlord now has 1 tenant.", 1, landlord.tenants.size());
		assertEquals("Landlord has tenant's correct information.", "Mock Apartment Tenant", landlord.tenants.get(0).aptRes.getName());
		assertEquals("Landlord has tenant's correct information.", 2, landlord.tenants.get(0).apartmentNumber);
		
		// Messages the landlord the rent from tenant
		landlord.msgHereIsTheRent(tenant, 10);
		
		// Checks that the tenant's state is now set to paying rent
		assertEquals("Tenant's state is now paying rent.", MyTenant.TenantState.PayingRent, landlord.tenants.get(0).state);
		
		// Checks that the tenant's amount paying is the full amount
		assertEquals("Tenant's amount paying is $10.", 10.0, landlord.tenants.get(0).amountPaying);
		
		// Checks that landlord has one logged entry
		assertEquals("Landlord has 1 logged entry now.", 1, landlord.log.size());
		assertTrue(landlord.log.getLastLoggedEvent().toString().contains("Received payment of $10"));
		
		// Invokes scheduler and makes sure that it returns true
		assertTrue(landlord.pickAndExecuteAnAction());
		
		// Checks that the tenant's status is now paid
		assertEquals("Tenant's state is now paid.", MyTenant.TenantState.Paid, landlord.tenants.get(0).state);
		assertEquals("Tenant owes $90.", 90.0, landlord.tenants.get(0).amountOwed);
		
		// Checks that tenant received message from landlord
		assertEquals("Tenant has one logged entry.", 1, tenant.log.size());
		assertTrue(tenant.log.getLastLoggedEvent().toString().contains("The landlord received my rent. I owe $90"));
		
		// Checks that the person agent has received finish role message
		assertEquals("Person agent has one logged entry.", 1, person.log.size());
		assertTrue(person.log.getLastLoggedEvent().toString().contains("You've finished your role and have returned to your PersonAgent"));
		
		// Invokes scheduler and makes sure that it returns true
		assertFalse(landlord.pickAndExecuteAnAction());	
	}
}
