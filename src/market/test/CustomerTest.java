package market.test;

import market.MarketCustomerRole;
import person.Location;
import person.PersonAgent;
import person.Position;
import person.SimEvent;
import person.Location.LocationType;
import person.SimEvent.EventType;
import person.interfaces.*;
import person.test.mock.PersonMock;
import market.MarketCustomerRole.state;
import market.Food;
import market.gui.MarketCustomerGui;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;
import junit.framework.TestCase;

import java.util.*;

public class CustomerTest extends TestCase {

	EventLog log = new EventLog();
	MarketCustomerRole customer;
	PersonMock p;
	MockCashier cashier;
	MarketCustomerGui customerGui;
	List<Location> l;
	
	public void setUp() throws Exception{
		super.setUp();	
		p = new PersonMock("person");
		customer = new MarketCustomerRole(p, "customer");
		cashier = new MockCashier("cashier");
		customerGui = new MarketCustomerGui(customer);

	}
	
	public void testNormalCustomerCashierCase(){

		
		customer.setCashier(cashier);
		
		List<Food> food = new ArrayList<Food>();
		
		food.add(new Food("Steak", 2));
		food.add(new Food("Chicken", 2));
		
		customer.setGui(customerGui);
		
		customer.msgAtTable();
		
		customer. msgHello(30, food);
		
		assertEquals("customer should have 2 in the list", customer.food.size(),2);
		
		
		
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
		
		customer.msgHereisYourChange(10, 0);
		
		assertTrue("Customer should have money == 10", customer.money == 10);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", customer.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == GetChange. It doesn't.",
				customer.s == state.GoWaiting);
		
		customer.msgYourFoodReady();
		
		customer.msgAtTable();
		
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
