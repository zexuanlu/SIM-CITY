package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import bank.*;
import bank.BankHostRole.state;

/**
 * This class is a J-Unit TestCase designed to test the basic functionality 
 * of the BankTellerRole in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankHostTest extends TestCase {

	MockBankTeller bt;
	MockBankCustomer bc;
	BankHostRole bh;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new MockBankTeller("BankTeller1");
		bc = new MockBankCustomer("BankCustomer1");
		bh = new BankHostRole("BankHost");
		bh.addTeller(bt);
	}
	
	public void testOneCustomerOneTeller(){
		assertEquals("Bank Host should contain 1 Teller. It doesn't.", bh.tellers.size(), 1);
		assertEquals("Bank Host should contain 0 Customers. It doesn't.", bh.waitingCustomers.size(), 0);
	
		bh.msgINeedTeller(bc);
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		assertTrue("Bank Host should contain 1 Customer. It doesn't.", bh.waitingCustomers.size() == 1);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);
		
		assertTrue("The scheduler should return true. It didn't.", bh.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgHereIsTeller\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgHereIsTeller"));
		assertEquals("Bank Host should contain 0 Customers. It doesn't.", bh.waitingCustomers.size(), 0);
		assertTrue("The state of the Teller should be withCustomer. It isn't.", bh.tellers.get(0).s == state.withCustomer);
		
		bh.msgBackToWork(bt);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);	
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
	}
}
