package restaurant5.test;

import restaurant5.CashierAgent5;
import restaurant5.test.mock.MockMarket5; 
import restaurant5.test.mock.MockCustomer5;
import restaurant5.test.mock.MockWaiter5; 
import junit.framework.*;

/**
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 */

public class CashierTest5 extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent5 cashier;
	MockWaiter5 waiter;
	MockCustomer5 customer;
	MockCustomer5 customer1; 
	MockCustomer5 customer2; 
	MockMarket5 market1; 
	MockMarket5 market2; 
	MockMarket5 market3; 


	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent5("cashier");	
		
		customer = new MockCustomer5("mockcustomer");	
		customer1 = new MockCustomer5("mockcustomer1");
		customer2 = new MockCustomer5("mockcustomer2");
		
		waiter = new MockWaiter5("mockwaiter");
		market1 = new MockMarket5("mockMarket1");
		market2 = new MockMarket5("mockMarket2");
		market3 = new MockMarket5("mockMarket3");

		customer.cashier = cashier;
		waiter.cashier = cashier; 
		market1.cashier = cashier; 
		market2.cashier = cashier; 
		market3.cashier = cashier; 
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!

		/////////////////////////////////////////////////////////
		//MARKET TEST 1
		//normative Test 
		//Test to see whether or not cashier handles one market bill properly 
		assertEquals("Market Log should be empty in the beginning ", 0, market1.log.size());

		cashier.msgmarketbill(market1, 150);
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgmarketbill)", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Market Log should now have received an action in the beginning ", 1, market1.log.size());
		assertEquals("Market should have received a payment of 150", 150, market1.receivedmoney);
		assertFalse("Cashier's scheduler should have return False because there's nothing to do", 
				cashier.pickAndExecuteAnAction());
		
		////////////////////////////////////////////////////////////////////
		//MARKET TEST 2
		//Test to see whether or not cashier can handle two market bills
		//preconditions 
		cashier.msgmarketbill(market1, 300);
		cashier.msgmarketbill(market2, 300);
		//have to call scheduler twice since two messages 
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgmarketbill)", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgmarketbill)", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Market1 should have received a payment of 300", 300, market1.receivedmoney);		
		assertEquals("Market2 should have received a payment of 300", 300, market1.receivedmoney);
		assertFalse("Cashier's scheduler should have return False because there's nothing to do", 
				cashier.pickAndExecuteAnAction());

		
		////////////////////////////////////////////////////////////////////////////////
		//TEST 3 (NON NORMATIVE)
		//Cashier does not handle his flaked amount, the markets will send him his flaked amount in his next bill
		//Test to see how well cashier works when he is unable to pay all of his marketBill 
		assertFalse("Cashier's scheduler should have returned False since there's nothing to do", 
				cashier.pickAndExecuteAnAction());
		cashier.msgDrainMoney(); //drains the money so that cashier does not have enough money to pay his bills (only 100)
		cashier.msgmarketbill(market1, 300); //cashier won't be able to pay 200 of his bill
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgmarketbill)", 
				cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned False since there's nothing to do", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Market should have logged \"Cashier Flaked\" but didn't. His log reads instead: " 
				+ market1.log.getLastLoggedEvent().toString(), market1.log.containsString("Cashier Flaked!"));
		assertFalse("Cashier's scheduler should have returned False since there's nothing to do", 
				cashier.pickAndExecuteAnAction());
		
		//now add more money to see if cashier will pay his money
		cashier.msgAddMoney(); //hack to add money so that cashier can now pay		
		cashier.msgmarketbill(market1, 500); //cashier can pay all 500
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgmarketbill)", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Cashier should have sent back 500 dollars", 500, market1.receivedmoney);

		
		
		
		//////////////////////////////////////////////////////////
		//TEST 4
		//normative Test
		//test to check whether or not the cashier adds a bill when waiter sends one to him
		//also test to check if cashier computes correct amount
		//also checks interleaving of market activities (Market also gives him bill)
		cashier.msgAddMoney(); //hack because previously we had drained all his money to run non norm scenario
		assertEquals("Market 3s log should be empty", market3.log.size(),0);
		assertEquals("Cashier should have 0 bills in it. It doesn't",cashier.bills.size(),0); 
		
		cashier.msgcomputeBill(waiter, customer, "Chicken"); //waiter sends him a bill
		assertEquals("Waiter Log should be empty in the beginning " + waiter.log.size(),0, waiter.log.size());
		assertEquals("Cashier should have 1 bill in it. It doesn't",cashier.bills.size(),1);
		cashier.msgmarketbill(market3, 150);

		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
			cashier.pickAndExecuteAnAction());
		
		assertTrue("Cashier's scheduler should have returned True (needs to react to market's msgmarketbill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned False since finished both actions", 
				cashier.pickAndExecuteAnAction());
		
		assertEquals("Waiter log should have 1 item " + waiter.log.size(), 1, waiter.log.size()); 
		assertEquals("Waiter should have a bill of 11 dollars since the choice was Chicken", 11, waiter.c.price);
		assertEquals("Market3 log should have 1 item now", market3.log.size(),1);
		assertEquals("Market 3 should have received 150 dollars", market3.receivedmoney, 150);
		
		//Test to check whether or not the Cashier sends the right amount back as change 
		//CUSTOMER IS NOT A FLAKE
		cashier.msgPayment(customer,20);
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Change should be 11 ", customer.myChange, 9); //paid 20 cost 11 so it's 9
		assertFalse("Cashier's scheduler should have returned False (There's nothing to do)", 
				cashier.pickAndExecuteAnAction());
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////
		//TEST 5 Non Norm
		//Test to check whether or not Cashier handles Flakes correctly 
		//CUSTOMER IS A FLAKE
		//Step 1, make a customer a Flake by have him give 0 as payment
		cashier.msgcomputeBill(waiter, customer, "Steak"); //waiter sends him a bill
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		cashier.msgPayment(customer, 0); //he didn't pay any of it. Cashier should add his amount of 16 to his check next time
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgPayment), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//Step 2, make sure that the waiter includes his previous flaked amount in new Check
		cashier.msgcomputeBill(waiter, customer, "Steak"); //16 + 16 = 32. His payment should be 32. 
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgPayment), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive bill for 32 ", waiter.c.price, 32);
		
		//Step 3, make sure that waiter removes him from the flaked list if he pays his amount, next Check should be normal amount
		cashier.msgPayment(customer, 32); //pay all of your dues to make sure that Cashier takes him off Flake list
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		cashier.msgcomputeBill(waiter, customer, "Steak");
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgPayment), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive a bill for 16 ", waiter.c.price, 16); //normal price of steak, without any flaked inclusion
		
		assertFalse("Cashier's scheduler should have returned False (nothing to do)", 
				cashier.pickAndExecuteAnAction());	
		
		/////////////////////////////////////////////////////////////////////////////////////////
		//TEST 6 (NON NORM)
		//Check if cashier can handle multiple checks well 
		//customer1 is not a flake and will receive change back
		//customer2 is a flake
		assertEquals("Customer1 Log should be empty in the beginning " + customer1.log.size(),0, customer1.log.size());
		assertEquals("Customer2 Log should be empty in the beginning " + customer2.log.size(),0, customer2.log.size());
		
		//first part where both customers order different things and checks to see if cashier sends right bills
		cashier.msgcomputeBill(waiter, customer1, "Salad"); //waiter sends him a bill
		cashier.msgcomputeBill(waiter, customer2, "Chicken"); //waiter sends him a bill
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive bill for 6 ", waiter.c.price, 6); //salad gets paid for first, since it was first added in the list
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill for customer2), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive bill for 11 ", waiter.c.price, 11); //then chicken gets paid for so the current bill is now 11
		assertFalse("Cashier's scheduler should have returned False (already took care of previous msgcomputeBill)", 
				cashier.pickAndExecuteAnAction());
		
		//2nd part to see if Cashier can handle the two payment messages of different amounts
		//customer 1 will pay more than his bill so he will receive change
		//customer 2 will pay less than his bill so he will be marked as flake
		cashier.msgPayment(customer1, 20); //since his bill was for 6, customer1 should receive 14 back in change 
		cashier.msgPayment(customer2, 2); //his bill was for 11, so he should be called out on being a flake and have 9 added to his tab next time
		assertTrue("Cashier's scheduler should have returned True (needs to react to msgPayment)", 
				cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's scheduler should have returned True (needs to react to msgPayment)", 
				cashier.pickAndExecuteAnAction());
		assertFalse("Cashier's scheduler should have returned False (already took care of previous msgPayments)", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Customer1 should have change of 14", customer1.myChange,14);
		assertEquals("Customer2 should not have any change", customer2.myChange,0);
		
		//3rd part of Test 6
		//send both customer1 and customer 2 back to restaurant
		//customer 1 should have just a regular bill
		//customer 2 should have his previous flaked amount of 9 added to his bill
		cashier.msgcomputeBill(waiter, customer1, "Salad"); //waiter sends him a bill
		cashier.msgcomputeBill(waiter, customer2, "Chicken"); //waiter sends him a bill
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive bill for 6 ", waiter.c.price, 6); //salad gets paid for first, since it was first added in the list
		assertTrue("Cashier's scheduler should have returned True (needs to react to waiter's msgcomputeBill for customer2), but didn't.", 
				cashier.pickAndExecuteAnAction());
		assertEquals("Waiter should receive bill for 20 ", waiter.c.price, 20); //bill is now for customer2's chicken (11) as well as his previous flaked amount of 9 
		assertFalse("Cashier's scheduler should have returned False (already took care of previous msgcomputeBill)", 
				cashier.pickAndExecuteAnAction());
	}
}
