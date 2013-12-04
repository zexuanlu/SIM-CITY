package restaurant6.test;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import restaurant6.Restaurant6CashierRole;
import restaurant6.Restaurant6Invoice;
import restaurant6.Restaurant6Restock;
import restaurant6.Restaurant6Check.CheckState;
import restaurant6.interfaces.Restaurant6Cashier;
import restaurant6.test.mock.MockCustomer;
import restaurant6.test.mock.MockMarket;
import restaurant6.test.mock.MockWaiter;
import junit.framework.*;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 */
public class CashierTest extends TestCase
{
	// These are instantiated for each test separately via the setUp() method.
	Restaurant6CashierRole cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockMarket market;
	MockMarket market2;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new Restaurant6CashierRole("Cashier");		
		customer = new MockCustomer("Mock Customer");	
		customer2 = new MockCustomer("Mock Customer 2");
		waiter = new MockWaiter("Mock Waiter");
		market = new MockMarket("Mock Market 1");
		market2 = new MockMarket("Mock Market 2");
	}
	
	/**
	 * This tests the cashier's paying one market in full
	 */
	public void testPayOneMarketInFull() {
		System.out.println("Testing Paying One Market in Full");
		DecimalFormat df = new DecimalFormat("###.##");
		
		// Set restaurant money amount
		cashier.setRestaurantMoney(300);
		
		// Preconditions: cashier has $300 in the register
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Preconditions: Cashier shouldn't have any invoices from the markets
		assertEquals("Cashier should have 0 invoices. It doesn't.", 0, cashier.getMarkets().size());
		
		// Precondition: Market should have an empty event long
		assertEquals("Market should have 0 logs.", 0, market.log.size());
		
		// Adding an order from the cook to the market
		List<Restaurant6Restock> orders = new ArrayList<Restaurant6Restock>();
		Restaurant6Restock item = new Restaurant6Restock("Chicken", 1);
		orders.add(item);
		
		// Messages the market the ordered inventory
		market.msgOrderFood(orders);
		
		// Makes sure that the market has two logs now, which are printed upon receipt of the msgOrderFood message
		assertEquals("Market should have 2 logs.", 2, market.log.size());
		
		// Makes sure that the market's log is the correct one
		assertTrue(market.log.containsString("Received order from cook."));
		
		// Makes sure that the market's log is the correct one and can fulfill the full order
		assertTrue(market.log.containsString("Can fulfill the order of 1"));
		
		// Messages the cashier the invoice from the market
		cashier.msgInvoice(market, new Restaurant6Invoice(1, 10.99));
		
		// Makes sure that the cashier has one invoice after receiving the message from the market
		assertEquals("Cashier should have 1 invoice.", 1, cashier.getMarkets().size());
		
		// Checks that the cashier's markets list has the same invoice amount as what was just added
		assertEquals("Cashier should have an invoice of $10.99", 10.99, cashier.getMarkets().get(0).invoice.getTotal());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Checks to make sure that the cashier doesn't have any invoices after the action
		assertEquals("Cashier should have 0 invoices after executing the action", 0, cashier.getMarkets().size());	
		
		// Checks to make sure that the cashier's log contains the correct message - that it can pay the market in full
		assertTrue(cashier.log.containsString("Can pay the market in full. Paid $10.99"));
		
		// Check to make sure that the cashier's restaurant money amount has decreased by the amount of the order
		assertEquals("Cashier should have $10.99 less in the register.", df.format(300-10.99), df.format(cashier.getRestaurantMoney()));

		// Checks to make sure that the cashier's last log contains the right message
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("My money is now $289.01"));
		
		// Checks the total number of cashier event logs
		assertEquals("Cashier should have 2 event logs.", 2, cashier.log.size());
		
		// Market should have 3 logged events now. Last one says received payment.
		assertEquals("Market should have 3 logged events.", 3, market.log.size());
		
		// Market should have a logged event that says received payment from cashier
		assertTrue(market.log.getLastLoggedEvent().toString().contains("Received payment of $10.99 from cashier."));
		
	}
	
	/**
	 * This tests the cashier's paying two markets in full
	 */
	public void testPayTwoMarketsInFull() { 
		System.out.println("Testing Paying Two Markets in Full");
		DecimalFormat df = new DecimalFormat("###.##");
		
		cashier.setRestaurantMoney(300); 
		
		// Precondition: Cashier shouldn't have any invoices from the markets
		assertEquals("Cashier should have 0 invoices. It doesn't.", 0, cashier.getMarkets().size());
		
		// Precondition: Market should have 0 event logs
		assertEquals("Market 1 should have 0 event logs. It doesn't.", 0, market.log.size());
		
		// Adding an order from the cook to the market
		List<Restaurant6Restock> orders = new ArrayList<Restaurant6Restock>();
		Restaurant6Restock item = new Restaurant6Restock("Chicken", 7);
		orders.add(item);
		
		// Messages the markets the ordered inventory
		market.msgOrderFood(orders);
		
		// Makes sure that the first market has two logs now, which are printed upon receipt of the msgOrderFood message
		assertEquals("Market 1 should have 2 logs.", 2, market.log.size());
		
		// Other log event should say that market received order message
		assertTrue(market.log.containsString("Received order from cook."));
		
		// Market event log should say that the market is not able to fulfill the order
		assertTrue(market.log.getLastLoggedEvent().toString().contains("Cannot fulfill the order, but can fulfill 5"));
		
		// Messages the cashier the invoice from the market
		cashier.msgInvoice(market, new Restaurant6Invoice(1, (5*10.99)));
		
		// Cashier should have 1 invoice in markets list now
		assertEquals("Cashier should have 1 invoice from market 1.", 1, cashier.getMarkets().size());
		
		// Invoice should be from the first market
		assertEquals("Invoice is from the correct market (Market 1).", market, cashier.getMarkets().get(0).market);
		
		// Messages the second market to order the leftover inventory
		List<Restaurant6Restock> ordersToSecond = new ArrayList<Restaurant6Restock>();
		Restaurant6Restock itemForSecond = new Restaurant6Restock("Chicken", 2);
		ordersToSecond.add(itemForSecond);
		market2.msgOrderFood(ordersToSecond);
		
		// Makes sure that the first market has two logs now, which are printed upon receipt of the msgOrderFood message
		assertEquals("Market 2 should have 2 logs.", 2, market2.log.size());
		
		// Other log event should say that market received order message
		assertTrue(market2.log.containsString("Received order from cook."));
		
		// Market event log should say that the market is not able to fulfill the order
		assertTrue(market2.log.getLastLoggedEvent().toString().contains("Can fulfill the order of 2"));
		
		// Messages the cashier the second invoice from the market
		cashier.msgInvoice(market2, new Restaurant6Invoice(2, (2*10.99)));
		
		// Makes sure that the cashier has two invoices after receiving the messages from the market
		assertEquals("Cashier should have 2 invoices.", 2, cashier.getMarkets().size());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Checks to make sure that the cashier only has one invoice left after the action
		assertEquals("Cashier should have 1 invoices after executing the action.", 1, cashier.getMarkets().size());	
		
		// Checks to make sure that the cashier's log contains the correct message - that it can pay the market in full
		assertTrue(cashier.log.containsString("Can pay the market in full. Paid $54.95"));
		
		// Checks to make sure that the cashier's event log has the right message
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("My money is now $245.05"));
		
		// Checks cashier's restaurant money amount
		assertEquals("Cashier should have $54.95 less than original.", df.format((300-(5*10.99))), df.format(cashier.getRestaurantMoney()));
		
		// Market should have a logged event that says received payment from cashier
		assertTrue(market.log.getLastLoggedEvent().toString().contains("Received payment of $54.95 from cashier."));
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Checks to make sure that the cashier has no invoices after this action
		assertEquals("Cashier should have 0 invoices after executing the action.", 0, cashier.getMarkets().size());
		
		// Makes sure the cashier's log contains the message that it can pay the market in full
		assertTrue(cashier.log.containsString("Can pay the market in full. Paid $21.98"));
		
		// Makes sure that the cashier's event log has the right message
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("My money is now $223.07"));
		
		// Checks cashier's restaurant money amount
		assertEquals("Cashier should have $76.93 less than original.", df.format((300-((5*10.99)+(2*10.99)))), df.format(cashier.getRestaurantMoney()));
		
		// Market should have a logged event that says received payment from cashier
		assertTrue(market2.log.getLastLoggedEvent().toString().contains("Received payment of $21.98 from cashier."));
		
		// Makes sure cashier and markets have correct number of logs
		assertEquals("Cashier should have 4 logs.", 4, cashier.log.size());
		assertEquals("Market should have 3 logs.", 3, market.log.size());
		assertEquals("Market 2 should have 3 logs.", 3, market2.log.size());
	}
	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testCustomerPayExactAmount() {		
		System.out.println("Testing Customer Paying Exact Amount");
		
		customer.cashier = (Restaurant6Cashier) cashier;
		
		cashier.setRestaurantMoney(300);
		
		// Step 1: Waiter asks cashier to compute the check
		// Precondition: cashier should have $300
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Precondition: cashier shouldn't have any checks
		assertEquals("Cashier should have 0 checks. It doesn't.", 0, cashier.getChecks().size());
		
		// Precondition: cashier should have 0 logs in event log
		assertEquals("Cashier should have 0 logs. It doesn't.", 0, cashier.log.size());
		
		// Precondition: waiter and customer should both have 0 logs
		assertEquals("Waiter should have 0 logs. It doesn't.", 0, waiter.log.size());
		assertEquals("Customer should have 0 logs. It doesn't.", 0, customer.log.size());
		
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Chicken", waiter, customer);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 1 event log.", 1, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 10.99
		assertEquals("Check amount should be $10.99",  10.99, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 2 logged events now that says "Computing check.."
		assertEquals("Cashier's event log should have 2 logs.", 2, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Cashier's first check in list should now have the state of givenToWaiter
		assertEquals("Cashier's check should now have state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
		
		// Waiter has 1 event log now that should say received check from cashier
		assertEquals("Waiter should have 1 event log.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));

		// Step 2: Customer is ready to pay the cashier
		// Make sure customer event log has no entries
		assertEquals("Customer should have 0 in the event log.", 0, customer.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(10.99, customer, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 2 logs in event log
		assertEquals("Customer should have 2 event logs.", 2, customer.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer.log.containsString("Received change from cashier. Change: 0"));
		assertTrue(customer.log.containsString("Received debt amount from cashier. Debt: 0"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $10.99 more in the register
		assertEquals("Cashier should now have $310.99.", 310.99, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 4 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 4 event logs.", 4, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $310.99"));
		assertTrue(cashier.log.containsString("Your change is $0.00. Have a nice day!"));	
		
	}
	

	/**
	 * This tests the cashier under the terms of the customer having debt.
	 */
	public void testCustomerHasDebt() {	
		System.out.println("Testing Customer Not Having Enough Money to Pay Full Bill");
		
		customer.cashier = (Restaurant6Cashier) cashier;
		
		cashier.setRestaurantMoney(300);
		
		// Step 1: Waiter asks cashier to compute the check
		// Precondition: cashier should have $300
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Precondition: cashier shouldn't have any checks
		assertEquals("Cashier should have 0 checks. It doesn't.", 0, cashier.getChecks().size());
		
		// Precondition: cashier should have 0 logs in event log
		assertEquals("Cashier should have 0 logs. It doesn't.", 0, cashier.log.size());
		
		// Precondition: waiter and customer should both have 0 logs
		assertEquals("Waiter should have 0 logs. It doesn't.", 0, waiter.log.size());
		assertEquals("Customer should have 0 logs. It doesn't.", 0, customer.log.size());
		
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Chicken", waiter, customer);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 1 event log.", 1, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 10.99
		assertEquals("Check amount should be $10.99",  10.99, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 2 logged events now that says "Computing check.."
		assertEquals("Cashier's event log should have 2 logs.", 2, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Cashier's first check in list should now have the state of givenToWaiter
		assertEquals("Cashier's check should now have state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
		
		// Waiter has 1 event log now that should say received check from cashier
		assertEquals("Waiter should have 1 event log.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));

		// Step 2: Customer is ready to pay the cashier, but doesn't have enough meony
		// Make sure customer event log has no entries
		assertEquals("Customer should have 0 in the event log.", 0, customer.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(5.99, customer, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 2 logs in event log
		assertEquals("Customer should have 2 event logs.", 2, customer.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer.log.containsString("Received change from cashier. Change: 0"));
		assertTrue(customer.log.containsString("Received debt amount from cashier. Debt: 5"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $10.99 more in the register
		assertEquals("Cashier should now have $305.99.", 305.99, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 4 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 4 event logs.", 4, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $305.99"));
		assertTrue(cashier.log.containsString("Your payment is not enough. You have to pay next time."));	
		
		// Step 3: Customer with debt comes back to eat. Waiter asks cashier to compute check.
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Salad", waiter, customer);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 5 event logs.", 5, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 5.99 + debt
		assertEquals("Check amount should be $5.99 + Debt", 10.99, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
	
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 6 logged events. The latest one should indicate computing the second customer's check
		assertEquals("Cashier's event log should have 6 logs.", 6, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Waiter's log should now have 2 logs, indicating receipt of second customer's check
		assertEquals("Waiter's event log should have 2 logs.", 2, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));
		
		// Cashier's check should now have the state of givenToWaiter
		assertEquals("Cashier's Salad check should have the state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
	
		// Step 4: Customer with debt comes to pay
		// Make sure customer event log has 2 entries
		assertEquals("Customer should have 2 in the event log.", 2, customer.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(30, customer, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 4 logs in event log
		assertEquals("Customer should have 4 event logs.", 4, customer.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer.log.containsString("Received change from cashier. Change: 19"));
		assertTrue(customer.log.containsString("Received debt amount from cashier. Debt: 0"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $10.99 more in the register
		assertEquals("Cashier should now have $316.98.", 316.98, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 8 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 8 event logs.", 8, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $316.98"));
		assertTrue(cashier.log.containsString("Your change is $19.01. Have a nice day!"));
	}
	
	/**
	 * This tests the cashier under the terms of two customers paying. One has enough to pay and the other doesn't.
	 */
	public void testTwoCustomersPaying() {		
		System.out.println("Testing Two Customer Paying. One Does Not Have Enough Money. The Other Does.");
		
		customer.cashier = (Restaurant6Cashier) cashier;
		
		cashier.setRestaurantMoney(300);
		
		// Step 1: Waiter asks cashier to compute the check
		// Precondition: cashier should have $300
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Precondition: cashier shouldn't have any checks
		assertEquals("Cashier should have 0 checks. It doesn't.", 0, cashier.getChecks().size());
		
		// Precondition: cashier should have 0 logs in event log
		assertEquals("Cashier should have 0 logs. It doesn't.", 0, cashier.log.size());
		
		// Precondition: waiter and customer should both have 0 logs
		assertEquals("Waiter should have 0 logs. It doesn't.", 0, waiter.log.size());
		assertEquals("Customer should have 0 logs. It doesn't.", 0, customer.log.size());
		
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Chicken", waiter, customer);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 1 event log.", 1, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 10.99
		assertEquals("Check amount should be $10.99",  10.99, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
		
		// Step 2: Waiter asks cashier to compute the check for the second customer
		// Precondition: cashier should have $300
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
	
		// Precondition: waiter and customer should both have 0 logs
		assertEquals("Waiter should have 0 logs. It doesn't.", 0, waiter.log.size());
		assertEquals("Customer should have 0 logs. It doesn't.", 0, customer.log.size());
		
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Steak", waiter, customer2);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 2 event logs.", 2, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 2 checks
		assertEquals("Cashier should have 2 checks.", 2, cashier.getChecks().size());
		
		// Check was added to the end of the list
		assertEquals("Check for steak should be at end of the cashier's checks list.", "Steak", cashier.getChecks().get(1).getChoice());
		
		// Check's amount should be equal to 10.99
		assertEquals("Check amount should be $15.99",  15.99, cashier.getChecks().get(1).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(1).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 3 logged events. The latest one should indicate the cashier is computing checks.
		assertEquals("Cashier's event log should have 3 logs.", 3, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Waiter's log should now have 1 log, indicating receipt of check
		assertEquals("Waiter's event log should have 1 log.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));
		
		// Cashier's check should now have the state of givenToWaiter
		assertEquals("Cashier's Chicken check should have the state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 4 logged events. The latest one should indicate computing the second customer's check
		assertEquals("Cashier's event log should have 4 logs.", 4, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Waiter's log should now have 2 logs, indicating receipt of second customer's check
		assertEquals("Waiter's event log should have 2 logs.", 2, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));
		
		// Cashier's check should now have the state of givenToWaiter
		assertEquals("Cashier's Steak check should have the state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(1).getCheckState());

		// Step 3: Customer 1 goes to pay the check
		// Make sure customer event log has no entries
		assertEquals("Customer should have 0 in the event log.", 0, customer.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(12.99, customer, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 2 logs in event log
		assertEquals("Customer should have 2 event logs.", 2, customer.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer.log.containsString("Received change from cashier. Change: 2"));
		assertTrue(customer.log.containsString("Received debt amount from cashier. Debt: 0"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 1 checks.", 1, cashier.getChecks().size());
		
		// Cashier should now have $10.99 more in the register
		assertEquals("Cashier should now have $310.99.", 310.99, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 6 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 6 event logs.", 6, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $310.99"));
		assertTrue(cashier.log.containsString("Your change is $2. Have a nice day!"));
		
		// Step 4: Customer 2 goes to pay the check
		// Make sure customer event log has no entries
		assertEquals("Customer 2 should have 0 in the event log.", 0, customer2.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(3, customer2, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 2 logs in event log
		assertEquals("Customer 2 should have 2 event logs.", 2, customer2.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer2.log.containsString("Received change from cashier. Change: 0"));
		assertTrue(customer2.log.containsString("Received debt amount from cashier. Debt: 12.99"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $3 more in the register
		assertEquals("Cashier should now have $313.99.", 313.99, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 8 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 8 event logs.", 8, cashier.log.size());
		assertTrue(cashier.log.containsString("Your payment is not enough. You have to pay next time."));
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("My money is now $313.99"));
		
		// Step 5: Customer with debt comes back to eat. Waiter asks cashier to compute check.
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Pizza", waiter, customer2);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 9 event logs.", 9, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 8.99 + customer's debt
		assertEquals("Check amount should be $8.99 + Debt", 21.98, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
	
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 10 logged events. The latest one should indicate computing the second customer's check
		assertEquals("Cashier's event log should have 10 logs.", 10, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Waiter's log should now have 3 logs, indicating receipt of second customer's check
		assertEquals("Waiter's event log should have 3 logs.", 3, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));
		
		// Cashier's check should now have the state of givenToWaiter
		assertEquals("Cashier's Pizza check should have the state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
	
		// Step 6: Customer with debt comes to pay
		// Make sure customer event log has 2 entries
		assertEquals("Customer should have 2 in the event log.", 2, customer2.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(30, customer2, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		// Invokes the cashier's scheduler and executes the action and checks to make sure action was executed
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 4 logs in event log
		assertEquals("Customer should have 4 event logs.", 4, customer2.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer2.log.containsString("Received change from cashier. Change: 8.02"));
		assertTrue(customer2.log.containsString("Received debt amount from cashier. Debt: 0"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $21.98 more in the register
		assertEquals("Cashier should now have $335.97.", 335.97, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 12 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 12 event logs.", 12, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $335.97"));
		assertTrue(cashier.log.containsString("Your change is $8.02. Have a nice day!"));
	}	
	
	/**
	 * This tests the cashier's paying one market in full and having a customer pay
	 */
	public void testPayMarketAndGetCustomerPayment() {
		System.out.println("Testing Paying One Market in Full & Accepting Customer Payment");
		DecimalFormat df = new DecimalFormat("###.##");
		
		// Set restaurant money amount
		cashier.setRestaurantMoney(300);
		
		// Preconditions: cashier has $300 in the register
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Preconditions: Cashier shouldn't have any invoices from the markets
		assertEquals("Cashier should have 0 invoices. It doesn't.", 0, cashier.getMarkets().size());
		
		// Precondition: cashier shouldn't have any checks
		assertEquals("Cashier should have 0 checks. It doesn't.", 0, cashier.getChecks().size());
		
		// Precondition: cashier should have 0 logs in event log
		assertEquals("Cashier should have 0 logs. It doesn't.", 0, cashier.log.size());
		
		// Precondition: waiter and customer should both have 0 logs
		assertEquals("Waiter should have 0 logs. It doesn't.", 0, waiter.log.size());
		assertEquals("Customer should have 0 logs. It doesn't.", 0, customer.log.size());
		
		// Precondition: Market should have an empty event long
		assertEquals("Market should have 0 logs.", 0, market.log.size());		
		
		// Adding an order from the cook to the market
		List<Restaurant6Restock> orders = new ArrayList<Restaurant6Restock>();
		Restaurant6Restock item = new Restaurant6Restock("Pizza", 1);
		orders.add(item);
		
		// Step 1: Market gets an order and then bills the cashier
		// Messages the market the ordered inventory
		market.msgOrderFood(orders);
		
		// Makes sure that the market has two logs now, which are printed upon receipt of the msgOrderFood message
		assertEquals("Market should have 2 logs.", 2, market.log.size());
		
		// Makes sure that the market's log is the correct one
		assertTrue(market.log.containsString("Received order from cook."));
		
		// Makes sure that the market's log is the correct one and can fulfill the full order
		assertTrue(market.log.containsString("Can fulfill the order of 1"));
		
		// Messages the cashier the invoice from the market
		cashier.msgInvoice(market, new Restaurant6Invoice(1, 8.99));
		
		// Makes sure that the cashier has one invoice after receiving the message from the market
		assertEquals("Cashier should have 1 invoice.", 1, cashier.getMarkets().size());
		
		// Checks that the cashier's markets list has the same invoice amount as what was just added
		assertEquals("Cashier should have an invoice of $8.99", 8.99, cashier.getMarkets().get(0).invoice.getTotal());
	
		// Step 2: Waiter asks cashier to compute the check
		// Precondition: cashier should have $300
		assertEquals("Cashier should have $300.", 300, (int)cashier.getRestaurantMoney());
		
		// Precondition: cashier shouldn't have any checks
		assertEquals("Cashier should have 0 checks. It doesn't.", 0, cashier.getChecks().size());
		
		// Precondition: cashier should have 0 logs in event log
		assertEquals("Cashier should have 0 logs. It doesn't.", 0, cashier.log.size());
		
		// Adds an order for the cashier to compute
		cashier.pleaseComputeCheck("Chicken", waiter, customer);	

		// Checks the cashier's event log
		assertEquals("Cashier should have 1 event log.", 1, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Received order from waiter Mock Waiter"));
		
		// Cashier should now have 1 check
		assertEquals("Cashier should have 1 check.", 1, cashier.getChecks().size());
		
		// Check's amount should be equal to 10.99
		assertEquals("Check amount should be $10.99",  10.99, cashier.getChecks().get(0).getBillAmount());
		
		// Check should have state of toBeComputed
		assertEquals("Cashier's check should have state of toBeComputed.", CheckState.toBeComputed, cashier.getChecks().get(0).getCheckState());
		
		/*
		 *  Invokes the cashier's scheduler and executes the action. Action that should be executed is 
		 *  customer payment because of scheduler priority.
		 */
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Cashier's log should have 2 logged events now. Latest one says "Computing check.."
		assertEquals("Cashier's event log should have 2 logs.", 2, cashier.log.size());
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("Computing check.."));
		
		// Cashier's first check in list should now have the state of givenToWaiter
		assertEquals("Cashier's check should now have state of givenToWaiter.", CheckState.givenToWaiter, cashier.getChecks().get(0).getCheckState());
		
		// Waiter has 1 event log now that should say received check from cashier
		assertEquals("Waiter should have 1 event log.", 1, waiter.log.size());
		assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Received check from cashier."));

		// Step 2: Customer is ready to pay the cashier
		// Make sure customer event log has no entries
		assertEquals("Customer should have 0 in the event log.", 0, customer.log.size());
		
		// Sets the customer's payment amount to price of chicken
		cashier.iWouldLikeToPayPlease(10.99, customer, cashier.getChecks().get(0));
		
		// Makes sure that the cashier has set the state of the check to customerIsPaying
		assertEquals("Check that cashier keeps track of should now have state customerIsPaying.", CheckState.customerIsPaying, cashier.getChecks().get(0).getCheckState());
		
		/*
		 *  Invokes cashier's scheduler. This will execute the customer payment action because of scheduler
		 *  priority.
		 */
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Customer should have 2 logs in event log
		assertEquals("Customer should have 2 event logs.", 2, customer.log.size());
		
		// Check the customer's log. One should have debt and another should have change
		assertTrue(customer.log.containsString("Received change from cashier. Change: 0"));
		assertTrue(customer.log.containsString("Received debt amount from cashier. Debt: 0"));
		
		// Makes sure that the cashier has no checks after the customer has fully paid
		assertEquals("Cashier should have 0 checks.", 0, cashier.getChecks().size());
		
		// Cashier should now have $10.99 more in the register
		assertEquals("Cashier should now have $310.99.", 310.99, cashier.getRestaurantMoney());
		
		// Cashier's event log should have 4 logs now - one with changed restaurant money and one that tells customer change
		assertEquals("Cashier should have 4 event logs.", 4, cashier.log.size());
		assertTrue(cashier.log.containsString("My money is now $310.99"));
		assertTrue(cashier.log.containsString("Your change is $0.00. Have a nice day!"));
		
		/*
		 * Invokes cashier's scheduler. This will execute the pay market action last because it is last
		 * in the scheduler priority.
		 */
		assertTrue(cashier.pickAndExecuteAnAction());
		
		// Checks to make sure that the cashier doesn't have any invoices after the action
		assertEquals("Cashier should have 0 invoices after executing the action", 0, cashier.getMarkets().size());	
		
		// Checks to make sure that the cashier's log contains the correct message - that it can pay the market in full
		assertTrue(cashier.log.containsString("Can pay the market in full. Paid $8.99"));
		
		// Check to make sure that the cashier's restaurant money amount has decreased by the amount of the order
		assertEquals("Cashier should have $8.99 less in the register.", df.format(310.99-8.99), df.format(cashier.getRestaurantMoney()));

		// Checks to make sure that the cashier's last log contains the right message
		assertTrue(cashier.log.getLastLoggedEvent().toString().contains("My money is now $302"));
		
		// Checks the total number of cashier event logs
		assertEquals("Cashier should have 6 event logs.", 6, cashier.log.size());
		
		// Market should have 3 logged events now. Last one says received payment.
		assertEquals("Market should have 3 logged events.", 3, market.log.size());
		
		// Market should have a logged event that says received payment from cashier
		assertTrue(market.log.getLastLoggedEvent().toString().contains("Received payment of $8.99 from cashier."));
		
	}
}
