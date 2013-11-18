package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import bank.interfaces.*;
import bank.*;
import bank.BankTellerRole.taskState;

/**
 * This class is a junit TestCase designed to test the basic functionality 
 * of the BankTellerRole in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankTellerTest extends TestCase {

	BankTellerRole bt;
	BankCustomerRole bc;
	BankDatabaseAgent bd;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new BankTellerRole("BankTeller1");
		bc = new BankCustomerRole("BankCustomer1");
		bd = new BankDatabaseAgent("BankDatabase");
		bt.bd = bd;
	}	
	
	public void testAccountCreation(){
		assertEquals("Bank Teller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
		
		bt.msgINeedAccount(bc);
		assertEquals("Bank Teller should have 1 task in it. It doesn't", bt.tasks.size(), 1);
		assertEquals("The type of the task should be openAccount. It isn't", bt.tasks.get(0).type, "openAccount");
		assertEquals("The account number of the task should be 0. It isn't", bt.tasks.get(0).accountNumber, 0);
		assertEquals("The customer in the Bank Teller should be the same as bc. It isn't", bt.bc, bc);
	
		assertTrue("The scheduler should return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankDatabase should have logged \"Received msgOpenAccount\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgOpenAccount"));
		assertTrue("The state of the first task in Bank Teller should be waiting. It isn't.", bt.tasks.get(0).ts == taskState.waiting);		
		
		bt.msgAccountCreated(789789, bc);
		assertTrue("BankTeller should have logged \"Received msgAccountCreated\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgAccountCreated"));
		assertTrue("The state of the first task in Bank Teller should be completed. It isn't", bt.tasks.get(0).ts == taskState.completed);
		assertEquals("The account number of the first task should be 789789. It isn't", bt.tasks.get(0).accountNumber, 789789);
	
		assertTrue("The scheduler should  return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgAccountMade\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgAccountMade"));
		assertEquals("BankTeller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
	}
}
