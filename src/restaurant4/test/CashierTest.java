package restaurant4.test;

import resident.test.mock.MockPerson;
import restaurant4.Restaurant4CashierRole;
import restaurant4.Restaurant4CashierRole.state;
import restaurant4.test.mock.MockCustomer;
import restaurant4.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Joseph Boman
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	Restaurant4CashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockCustomer customer3;
	MockPerson person;
	market.test.mock.MockCashier cashier2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		person = new MockPerson("Person");
		cashier = new Restaurant4CashierRole("cashier", person);
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");
		customer3 = new MockCustomer("mockcustomer3");
		waiter = new MockWaiter("mockwaiter");
	}	
	
	/**
	 * This tests the first non-specific scenario. 1 customer and 1 waiter interacting with the cashier
	 */
	public void testOneCustomerOneWaiterScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		cashier.msgAddMoney(1000.00);
		//Check Preconditions
		assertEquals("Cashier should have 0 Checks in it. It doesn't", cashier.checks.size(), 0);
		assertEquals("Cashier should have an empty event log. Instead it reads "+ cashier.log.toString(), 0, cashier.log.size());
		
		cashier.msgINeedCheck("Lobster", customer, waiter);
		assertTrue("Cashier should have logged \"msgINeedCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgINeedCheck"));
		assertEquals("Cashier should have 1 check in it. It doesn't", cashier.checks.size(), 1);
		assertTrue("The state of the single check should be requested. It isn't.", cashier.checks.get(0).s == state.requested);
		assertTrue("The bill contains the right customer", cashier.checks.get(0).cust == customer);
		assertEquals("The type of the bill should be Lobster. It isn't.", cashier.checks.get(0).type, "Lobster");
		
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Waiter should have logged \"msgHereIsCheck\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck"));
		assertTrue("The state of the check should be owed. It isn't.", cashier.checks.get(0).s == state.owed);
		assertTrue("The price of the bill should be 15.99 dollars. It isn't.", cashier.checks.get(0).price == 14.99);
		
		cashier.msgPayingForFood(customer, 40.00);
		assertEquals("The check should have a money value of 40.00. It doesn't.", cashier.checks.get(0).money, 40.00);
		assertTrue("Cashier should have logged \"msgPayingForFood\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayingForFood"));
		assertTrue("The state of the check should now be paid. It isn't.", cashier.checks.get(0).s == state.paid);
		
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have logged \"msgHereIsChange\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsChange"));
		assertTrue("The check should have the state done. It doesn't.", cashier.checks.get(0).s == state.done);
		
		assertFalse("Cashier's scheduler should have returned false. It didn't.", cashier.pickAndExecuteAnAction());
	}//End Scenario
	
	/**
	 * This tests the third non-specific scenario. 1 Waiter and 3 Customers.
	 */
	public void testThreeCustomersOneWaiter(){
		customer.cashier = cashier;
		customer2.cashier = cashier;
		customer3.cashier = cashier;
		cashier.msgAddMoney(1000.00);
		
		assertEquals("Cashier should have 0 Bills in it. It doesn't", cashier.bills.size(), 0);
		assertEquals("Cashier should have 0 Checks in it. It doesn't", cashier.checks.size(), 0);		
		assertEquals("Cashier should have an empty event log. Instead it reads "+ cashier.log.toString(), 0, cashier.log.size());
	
		cashier.msgINeedCheck("Crab", customer, waiter);
		assertTrue("Cashier should have logged \"msgINeedCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgINeedCheck"));
		assertEquals("Cashier should have 1 check in it. It doesn't", cashier.checks.size(), 1);
		assertTrue("The state of the single check should be requested. It isn't.", cashier.checks.get(0).s == state.requested);
		assertTrue("The bill contains the right customer", cashier.checks.get(0).cust == customer);
		assertTrue("The type of the bill should be Salad. It isn't.", cashier.checks.get(0).type == "Crab");
		
		cashier.msgINeedCheck("Scallops", customer2, waiter);
		assertTrue("Cashier should have logged \"msgINeedCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgINeedCheck"));
		assertEquals("Cashier should have 2 checks in it. It doesn't", cashier.checks.size(), 2);
		assertTrue("The state of the single check should be requested. It isn't.", cashier.checks.get(1).s == state.requested);
		assertTrue("The bill contains the right customer", cashier.checks.get(1).cust == customer2);
		assertTrue("The type of the bill should be Chicken. It isn't.", cashier.checks.get(1).type == "Scallops");
	
		assertTrue("Cashier's scheduler should return true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Waiter should have logged \"msgHereIsCheck\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck"));
		assertTrue("The state of the check should be owed. It isn't.", cashier.checks.get(0).s == state.owed);
		assertTrue("The price of the bill should be 5.99 dollars. It isn't.", cashier.checks.get(0).price == 13.99);
	
		assertTrue("Cashier's scheduler should return true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Waiter should have logged \"msgHereIsCheck\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck"));
		assertTrue("The state of the check should be owed. It isn't.", cashier.checks.get(1).s == state.owed);
		assertTrue("The price of the bill should be 10.99 dollars. It isn't.", cashier.checks.get(1).price == 7.99);
	
		cashier.msgPayingForFood(customer2, 30.00);
		assertEquals("The check should have a money value of 30.00. It doesn't.", cashier.checks.get(1).money, 30.00);
		assertTrue("Cashier should have logged \"msgPayingForFood\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayingForFood"));
		assertTrue("The state of the check should now be paid. It isn't.", cashier.checks.get(1).s == state.paid);
	
		cashier.msgINeedCheck("Shrimp", customer3, waiter);
		assertTrue("Cashier should have logged \"msgINeedCheck\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgINeedCheck"));
		assertEquals("Cashier should have 1 check in it. It doesn't", cashier.checks.size(), 3);
		assertTrue("The state of the single check should be requested. It isn't.", cashier.checks.get(2).s == state.requested);
		assertTrue("The bill contains the right customer", cashier.checks.get(2).cust == customer3);
		assertTrue("The type of the bill should be Steak. It isn't.", cashier.checks.get(2).type == "Shrimp");
	
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have logged \"msgHereIsChange\" but didn't. His log reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHereIsChange"));
		assertTrue("The check should have the state done. It doesn't.", cashier.checks.get(1).s == state.done);
	
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Waiter should have logged \"msgHereIsCheck\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received msgHereIsCheck"));
		assertTrue("The state of the check should be owed. It isn't.", cashier.checks.get(2).s == state.owed);
		assertTrue("The price of the bill should be 8.99 dollars. It isn't.", cashier.checks.get(2).price == 8.99);
	
		cashier.msgPayingForFood(customer3, 20.00);
		assertEquals("The check should have a money value of 20.00. It doesn't.", cashier.checks.get(2).money, 20.00);
		assertTrue("Cashier should have logged \"msgPayingForFood\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayingForFood"));
		assertTrue("The state of the check should now be paid. It isn't.", cashier.checks.get(2).s == state.paid);
		
		cashier.msgPayingForFood(customer, 12.00);
		assertEquals("The check should have a money value of 12.00. It doesn't.", cashier.checks.get(0).money, 12.00);
		assertTrue("Cashier should have logged \"msgPayingForFood\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayingForFood"));
		assertTrue("The state of the check should now be paid. It isn't.", cashier.checks.get(0).s == state.paid);
		
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have logged \"msgHereIsChange\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsChange"));
		assertTrue("The check should have the state done. It doesn't.", cashier.checks.get(0).s == state.done);
	
		assertTrue("Cashier's scheduler should have returned true. It didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Customer should have logged \"msgHereIsChange\" but didn't. His log reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHereIsChange"));
		assertTrue("The check should have the state done. It doesn't.", cashier.checks.get(2).s == state.done);
		
		assertFalse("Cashier's scheduler should have returned false. It didn't.", cashier.pickAndExecuteAnAction());
	}//End of Scenario
}
