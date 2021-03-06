package market.test;

import java.util.ArrayList;

import person.test.mock.PersonMock;

import java.util.List;

import market.MarketCashierRole;
import market.MarketCashierRole.state;
import market.test.mock.*;
import junit.framework.TestCase;
import market.Food;
import restaurant1.test.mock.MockRestaurantCashier;
import restaurant1.test.mock.MockRestaurantCook;

public class CashierTest extends TestCase{

	MarketCashierRole cashier;
	PersonMock p;
	MockCustomer customer;
	MockEmployee employee;
	MockRestaurantCook cook;
	MockRestaurantCashier cash;
	MockTruck truck;
	
	public void setUp() throws Exception{
		super.setUp();	
		p = new PersonMock("one");
		cashier = new MarketCashierRole(p, "cashier");
		customer = new MockCustomer("customer");
		employee = new MockEmployee("employee");
		cook = new MockRestaurantCook("cook");
		cash = new MockRestaurantCashier("ca");
		truck = new MockTruck("truck");
	}
	
	public void testCashierCustomerEmployeeCase(){
		List<Food> food = new ArrayList<Food>();
		
		food.add(new Food("Steak", 4));
		
		cashier.addEmployee(employee);
		
		assertEquals("cashier should have 1 employee in the list", cashier.employee.size(), 1);
		
		assertEquals("cashier should have 0 in the list", cashier.mycustomer.size(), 0);
		
		cashier.msgHereisOrder(customer, food);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == collected. It doesn't.",
				cashier.mycustomer.get(0).s == state.ordered);
		
		assertEquals("customer list should have 1 collectedOrder", cashier.mycustomer.get(0).collectedOrder.size(), 1);
		
		assertTrue("cashier has a customer who has bill = 8", cashier.mycustomer.get(0).bill == 8);
		
		assertTrue("employee should return a string with word Got Your Order, but the last event return "
				+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Got Your Order"));
		
		cashier.msgPayment(customer,30);
		
		assertTrue("cashier has a customer who has payed 30", cashier.mycustomer.get(0).pay == 30);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with payed == collected. It doesn't.",
				cashier.mycustomer.get(0).s == state.payed);
		
		cashier.msgHereisProduct(customer, food);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == calling. It doesn't.",
				cashier.mycustomer.get(0).s == state.calling);
		
		assertTrue("employee should return a string with word Comming to the table, but the last event return "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Comming to the table"));
		
		cashier.msgGoToTable(customer);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("cashier should have 0 customer in it. It doesn't.", cashier.mycustomer.size(), 0);
		
		assertTrue("employee should return a string with word Comming to the table, but the last event return "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Receive order"));
		
	}
	
	public void testDeliveryCase(){
		List<Food> food = new ArrayList<Food>();
		
		food.add(new Food("Steak", 1));
		
		cashier.addEmployee(employee);
		
		cashier.addTruck(truck);
		
		assertEquals("cashier should have 1 employee in the list", cashier.employee.size(), 1);
		
		cashier.MsgIwantFood(cook, cash, food, 1);
		
		assertEquals("cashier should have 1 restaruant in it. It doesn't.", cashier.myrest.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("cashier should have 0 restaruant in it. It doesn't.", cashier.myrest.size(), 0);
		
		assertTrue("employee should return a string with word Got Your Order, but the last event return "
				+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Got Your Order"));
	}
	
	public void testRestaurantCustomerCashierInterleavingCase(){
		
		List<Food> food = new ArrayList<Food>();
		
		food.add(new Food("Steak", 1));
		
		cashier.addEmployee(employee);
		
		cashier.addTruck(truck);
		
		assertEquals("cashier should have 1 employee in the list", cashier.employee.size(), 1);
		
		assertEquals("cashier should have 0 in the list", cashier.mycustomer.size(), 0);
		
		cashier.msgHereisOrder(customer, food);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		cashier.MsgIwantFood(cook, cash, food, 1);
		
		assertEquals("cashier should have 1 restaruant in it. It doesn't.", cashier.myrest.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("cashier should have 0 restaruant in it. It doesn't.", cashier.myrest.size(), 0);
		
		assertTrue("employee should return a string with word Got Your Order, but the last event return "
				+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Got Your Order"));
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == collected. It doesn't.",
				cashier.mycustomer.get(0).s == state.ordered);
		
		assertEquals("customer list should have 1 collectedOrder", cashier.mycustomer.get(0).collectedOrder.size(), 1);
		
		assertTrue("cashier has a customer who has bill = 2", cashier.mycustomer.get(0).bill == 2);
		
		assertTrue("employee should return a string with word Got Your Order, but the last event return "
				+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Got Your Order"));
		
		cashier.msgPayment(customer,30);
		
		assertTrue("cashier has a customer who has payed 30", cashier.mycustomer.get(0).pay == 30);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with payed == collected. It doesn't.",
				cashier.mycustomer.get(0).s == state.payed);
		
		cashier.msgHereisProduct(customer, food);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with state == calling. It doesn't.",
				cashier.mycustomer.get(0).s == state.calling);
		
		assertTrue("employee should return a string with word Comming to the table, but the last event return "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Comming to the table"));
		
		cashier.msgGoToTable(customer);
		
		assertEquals("cashier should have 1 customer in it. It doesn't.", cashier.mycustomer.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals("cashier should have 0 customer in it. It doesn't.", cashier.mycustomer.size(), 0);
		
		assertTrue("employee should return a string with word Comming to the table, but the last event return "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Receive order"));
	}
}
