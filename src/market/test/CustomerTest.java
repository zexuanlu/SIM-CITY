package market.test;

import market.CustomerAgent;
import market.CustomerAgent.state;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;
import junit.framework.TestCase;

import java.util.*;

public class CustomerTest extends TestCase {

	EventLog log = new EventLog();
	CustomerAgent customer;
	MockCashier cashier;
	
	public void setUp() throws Exception{
		super.setUp();	
		customer = new CustomerAgent();
		cashier = new MockCashier("cashier");
	}
	
	public void testNormalCustomerCashierCase(){
		customer.addFood("Steak", 2);
		customer.addFood("Chicken", 2);
		customer.setCashier(cashier);
		List food = new ArrayList();
		
		assertEquals("customer should have 2 in the list", customer.food.size(),2);
		
		customer. msgHello();
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == ordered. It doesn't.",
				customer.s == state.ordered);
		
		assertTrue("cashier should return a string with word Got it, but the last event return "
		+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Got Your Order"));
		
		customer.msgPleasePay(30);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == payed. It doesn't.",
				customer.s == state.payed);
		
		assertTrue("cashier should return a string with word receive 30, but the last event return "
		+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Receive 30"));
		
		customer.msgHereisYourChange(10);
		
		assertTrue("Customer should have money == 10", customer.money == 10);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == GetChange. It doesn't.",
				customer.s == state.GoWaiting);
		
		customer.msgYourFoodReady();
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == attable. It doesn't.",
				customer.s == state.attable);
		
		assertTrue("cashier should return a string with word You are at the table, but the last event return "
		+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("You are at the table"));
		
		customer.msgHereisYourOrder(food);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == collected. It doesn't.",
				customer.s == state.collected);
		
	}
	
}
