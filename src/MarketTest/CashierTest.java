package MarketTest;

import java.util.ArrayList;
import java.util.List;

import market.CashAgent;
import market.CashierAgent;
import market.CashierAgent.state;
import market.CashierAgent.state1;
import marketinterface.Cook;
import marketinterface.Customer;
import markettestmock.MockCook;
import markettestmock.MockCustomer;
import markettestmock.MockEmployee;
import junit.framework.TestCase;
import market.Food;

public class CashierTest extends TestCase{

	CashierAgent cashier;
	MockCustomer customer;
	MockEmployee employee;
	MockCook cook;
	CashAgent cash;
	
	public void setUp() throws Exception{
		super.setUp();	
		cashier = new CashierAgent();
		customer = new MockCustomer("customer");
		employee = new MockEmployee("employee");
		cook = new MockCook("cook");
		cash = new CashAgent();
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
		
		assertTrue("cashier has a customer who has bill = 4", cashier.mycustomer.get(0).bill == 4);
		
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
		
		assertTrue("employee should contain a customer with payed == taken. It doesn't.",
				cashier.mycustomer.get(0).s == state.taken);
		
		assertTrue("employee should return a string with word Comming to the table, but the last event return "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Receive order"));
		
	}
	
	public void testDeliveryCase(){
		List food = new ArrayList();
		cashier.addEmployee(employee);
		
		assertEquals("cashier should have 1 employee in the list", cashier.employee.size(), 1);
		
		cashier.MsgIwantFood(cook, cash, food);
		
		assertEquals("cashier should have 1 restaruant in it. It doesn't.", cashier.myrest.size(), 1);
		
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("employee should contain a customer with payed == taken. It doesn't.",
				cashier.myrest.get(0).s1 == state1.ordered);
		
		assertTrue("employee should return a string with word Got Your Order, but the last event return "
				+ employee.log.getLastLoggedEvent().toString(), employee.log.containsString("Got Your Order"));
	}
}
