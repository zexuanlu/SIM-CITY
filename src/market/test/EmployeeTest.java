package market.test;


import java.util.*;

import person.interfaces.*;
import person.test.mock.PersonMock;
import market.MarketEmployeeRole;
import market.MarketEmployeeRole.state;
import market.MarketEmployeeRole.state1;
import market.Food;
import market.gui.MarketEmployeeGui;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;
import restaurant.*;
import restaurant.test.mock.MockRestaurantCook;
import market.test.mock.MockCustomer;
import market.test.mock.MockTruck;
import junit.framework.TestCase;

public class EmployeeTest extends TestCase{

	EventLog log = new EventLog();
	MarketEmployeeRole employee;
	MockCashier cashier;
	MockCustomer customer;
	PersonMock p;
	MockRestaurantCook cook;
	MockTruck truck;
	MarketEmployeeGui employeeGui;
	
	public void setUp() throws Exception{
		super.setUp();	
		p = new PersonMock("one");
		employee = new MarketEmployeeRole(p, "employee");
		cashier = new MockCashier("casheir");
		customer = new MockCustomer("customer");
		cook = new MockRestaurantCook("cook");
		truck = new MockTruck("truck");
		employeeGui = new MarketEmployeeGui(employee);
	}
	
	public void testNormalEmployeeCashierCase(){
		
		assertEquals("employee should have 0 in the list", employee.mycustomer.size(), 0);
		
		employee.setCashier(cashier);
		
		employee.setGui(employeeGui);
		
		List<Food> food = new ArrayList<Food>();
		
		employee.msgCollectOrer(customer, food);
		
		assertEquals("employee should have 1 customer in it. It doesn't.", employee.mycustomer.size(), 1);
		
		employee.msgAtTable();
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", employee.pickAndExecuteAnAction());
		
		assertEquals("employee should have 0 customer in it. It doesn't.", employee.mycustomer.size(), 0);
		
		assertTrue("cashier should return a string with word Got it, but the last event return "
		+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Got it"));
		
	}
	
	public void testMormalDeliveryCase(){
		assertEquals("employee should have 0 in the list", employee.myrest.size(), 0);
		
		employee.setCashier(cashier);
		
		employee.setGui(employeeGui);
		
		List<Food> food = new ArrayList<Food>();
		
		employee.msgCollectTheDilivery(cook, food, truck, 1);
		
		assertEquals("employee should have 1 order in it. It doesn't.", employee.myrest.size(), 1);
		
		employee.msgAtTable();
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", employee.pickAndExecuteAnAction());
		
		assertEquals("employee should have 0 order in it. It doesn't.", employee.myrest.size(), 0);
		
		assertTrue("truck should return a string with word Got it, but the last event return "
		+ truck.log.getLastLoggedEvent().toString(), truck.log.containsString("Got it"));
	}
	
	
	
}
