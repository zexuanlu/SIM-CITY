package MarketTest;


import java.util.*;

import market.EmployeeAgent;
import market.EmployeeAgent.state;
import market.EmployeeAgent.state1;
import markettestmock.EventLog;
import markettestmock.MockCashier;
import markettestmock.MockCook;
import markettestmock.MockCustomer;
import markettestmock.MockTruck;
import junit.framework.TestCase;

public class EmployeeTest extends TestCase{

	EventLog log = new EventLog();
	EmployeeAgent employee;
	MockCashier cashier;
	MockCustomer customer;
	MockCook cook;
	MockTruck truck;
	
	public void setUp() throws Exception{
		super.setUp();	
		employee = new EmployeeAgent();
		cashier = new MockCashier("casheir");
		customer = new MockCustomer("customer");
		cook = new MockCook("cook");
		truck = new MockTruck("truck");
	}
	
	public void testNormalEmployeeCashierCase(){
		
		assertEquals("employee should have 0 in the list", employee.mycustomer.size(), 0);
		employee.setCashier(cashier);
		List food = new ArrayList();
		employee.msgCollectOrer(customer, food);
		
		assertEquals("employee should have 1 customer in it. It doesn't.", employee.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", employee.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == collected. It doesn't.",
				employee.mycustomer.get(0).s == state.collected);
		
		assertTrue("cashier should return a string with word Got it, but the last event return "
		+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Got it"));
		
	}
	
	public void testMormalDeliveryCase(){
		assertEquals("employee should have 0 in the list", employee.myrest.size(), 0);
		employee.setCashier(cashier);
		List food = new ArrayList();
		employee.msgCollectTheDilivery(cook, food, truck);
		
		assertEquals("employee should have 1 order in it. It doesn't.", employee.myrest.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", employee.pickAndExecuteAnAction());
		
		assertTrue("employee should contain an order with state == sending. It doesn't.",
				employee.myrest.get(0).s1 == state1.sending);
		
		assertTrue("truck should return a string with word Got it, but the last event return "
		+ truck.log.getLastLoggedEvent().toString(), truck.log.containsString("Got it"));
	}
	
	
	
}
