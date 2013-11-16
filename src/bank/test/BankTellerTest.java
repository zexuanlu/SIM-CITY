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
	BankCustomer bc;
	BankDatabaseAgent bd;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new BankTellerRole("BankTeller1");
		bd = new BankDatabaseAgent("BankDatabase");
		bt.bd = bd;
	}	
	
	public void testOne(){
		assertEquals("Bank Teller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
		
		bt.msgINeedAccount(bc, 100);
		assertEquals("Bank Teller should have 1 task in it. It doesn't", bt.tasks.size(), 1);
		assertEquals("The type of the task should be openAccount. It isn't", bt.tasks.get(0).type, "openAccount");
		assertEquals("The amount of the task should be $100. It isn't", bt.tasks.get(0).amount, 100);
		assertEquals("The account number of the task should be 0. It isn't", bt.tasks.get(0).accountNumber, 0);
		assertEquals("The customer in the Bank Teller should be the same as bc. It isn't", bt.bc, bc);
		
		bt.msgDepositMoney(bc, 100, 324324);
		assertEquals("Bank Teller should have 2 tasks in it. It doesn't", bt.tasks.size(), 2);
		assertEquals("The type of the second task should be deposit. It isn't", bt.tasks.get(1).type, "deposit");
		assertEquals("The amount of the second task should be $100. It isn't", bt.tasks.get(1).amount, 100);
		assertEquals("The account number of the second task should be 324324. It isn't", bt.tasks.get(1).accountNumber, 324324);
		assertEquals("The customer in the Bank Teller should be the same as bc. It isn't", bt.bc, bc);
		
		bt.msgWithdrawMoney(bc, 50, 123456);
		assertEquals("Bank Teller should have 3 tasks in it. It doesn't", bt.tasks.size(), 3);
		assertEquals("The type of the third task should be deposit. It isn't", bt.tasks.get(2).type, "withdraw");
		assertEquals("The amount of the third task should be $50. It isn't", bt.tasks.get(2).amount, 50);
		assertEquals("The account number of the third task should be 123456. It isn't", bt.tasks.get(2).accountNumber, 123456);
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
	}
}