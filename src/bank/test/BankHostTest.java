package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import person.test.mock.*;
import bank.*;
import bank.BankHostRole.state;

/**
 * This class is a J-Unit TestCase designed to test the basic functionality 
 * of the BankHostRole in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankHostTest extends TestCase {

	MockBankTeller bt;
	MockBankCustomer bc;
	MockBankCustomer bc2;
	PersonMock p;
	BankHostRole bh;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		p = new PersonMock("Person");
		bt = new MockBankTeller("BankTeller1");
		bc = new MockBankCustomer("BankCustomer1");
		bc2 = new MockBankCustomer("BankCustomer2");
		bh = new BankHostRole(p, "BankHost");
	}
	
	public void testOneCustomerOneTeller(){
		assertEquals("Bank Host should contain 0 Tellers. It doesn't.", bh.tellers.size(), 0);

		bh.addTeller(bt);
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
		
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
		
		bh.msgBackToWork(bt);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);
		assertTrue("BankHost should have logged \"Received msgBackToWork\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgBackToWork"));
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
	}
	
	public void testTwoCustomersOneTeller(){
		assertEquals("Bank Host should contain 0 Tellers. It doesn't.", bh.tellers.size(), 0);
		
		bh.addTeller(bt);
		assertEquals("Bank Host should contain 1 Teller. It doesn't.", bh.tellers.size(), 1);
		assertEquals("Bank Host should contain 0 Customers. It doesn't.", bh.waitingCustomers.size(), 0);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);

		bh.msgINeedTeller(bc);
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		assertTrue("Bank Host should contain 1 Customer. It doesn't.", bh.waitingCustomers.size() == 1);
		assertEquals("The Customer should be the right one. It isn't.", bh.waitingCustomers.get(0), bc);
		
		bh.msgINeedTeller(bc2);
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		assertTrue("Bank Host should contain 2 Customers. It doesn't.", bh.waitingCustomers.size() == 2);
		assertEquals("The Customer should be the right one. It isn't.", bh.waitingCustomers.get(1), bc2);
		
		assertTrue("The scheduler should return true. It didn't.", bh.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgHereIsTeller\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgHereIsTeller"));
		assertEquals("Bank Host should contain 1 Customer. It doesn't.", bh.waitingCustomers.size(), 1);
		assertTrue("The state of the Teller should be withCustomer. It isn't.", bh.tellers.get(0).s == state.withCustomer);
		
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
		
		bh.msgBackToWork(bt);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);	
		assertTrue("BankHost should have logged \"Received msgBackToWork\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgBackToWork"));
		
		assertTrue("The scheduler should return true. It didn't.", bh.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgHereIsTeller\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgHereIsTeller"));
		assertEquals("Bank Host should contain 0 Customers. It doesn't.", bh.waitingCustomers.size(), 0);
		assertTrue("The state of the Teller should be withCustomer. It isn't.", bh.tellers.get(0).s == state.withCustomer);
		
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
		
		bh.msgBackToWork(bt);
		assertTrue("The state of the Teller should be working. It isn't.", bh.tellers.get(0).s == state.working);	
		assertTrue("BankHost should have logged \"Received msgBackToWork\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgBackToWork"));
		assertFalse("The scheduler should return false. It didn't.", bh.pickAndExecuteAnAction());
	}
}
