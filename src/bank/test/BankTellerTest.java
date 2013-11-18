package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import bank.*;
import bank.BankTellerRole.taskState;

/**
 * This class is a J-Unit TestCase designed to test the basic functionality 
 * of the BankTellerRole in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankTellerTest extends TestCase {

	BankTellerRole bt;
	MockBankCustomer bc;
	MockBankDatabase bd;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new BankTellerRole("BankTeller1");
		bc = new MockBankCustomer("BankCustomer1");
		bd = new MockBankDatabase("BankDatabase");
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
	
	public void testAccountDeposit(){
		assertEquals("Bank Teller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);

		bt.msgDepositMoney(bc, 100.00, 123);
		assertEquals("Bank Teller should have 1 task in it. It doesn't", bt.tasks.size(), 1);
		assertEquals("The type of the task should be deposit. It isn't", bt.tasks.get(0).type, "deposit");
		assertEquals("The account number of the task should be 123. It isn't", bt.tasks.get(0).accountNumber, 123);
		assertEquals("The amount of the task should be 100.00. It isn't", bt.tasks.get(0).amount, 100.00);
		assertEquals("The customer in the Bank Teller should be the same as bc. It isn't", bt.bc, bc);
	
		assertTrue("The scheduler should return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankDatabase should have logged \"Received msgDepositMoney\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgDepositMoney"));
		assertTrue("The state of the first task in Bank Teller should be waiting. It isn't.", bt.tasks.get(0).ts == taskState.waiting);
		
		bt.msgDepositDone(200.00, bc);
		assertTrue("BankTeller should have logged \"Received msgDepositDone\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgDepositDone"));
		assertTrue("The state of the first task in Bank Teller should be completed. It isn't", bt.tasks.get(0).ts == taskState.completed);
		assertEquals("The account number of the first task should be 123. It isn't", bt.tasks.get(0).accountNumber, 123);
		assertEquals("The balance of the first task should be 200.00. It isn't", bt.tasks.get(0).balance, 200.00);
	
		assertTrue("The scheduler should  return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgDepositDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgDepositDone"));
		assertEquals("BankTeller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
	}
	
	public void testAccountWithdraw(){
		assertEquals("Bank Teller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);

		bt.msgWithdrawMoney(bc, 50.00, 123);
		assertEquals("Bank Teller should have 1 task in it. It doesn't", bt.tasks.size(), 1);
		assertEquals("The type of the task should be withdraw. It isn't", bt.tasks.get(0).type, "withdraw");
		assertEquals("The account number of the task should be 123. It isn't", bt.tasks.get(0).accountNumber, 123);
		assertEquals("The amount of the task should be 50.00. It isn't", bt.tasks.get(0).amount, 50.00);
		assertEquals("The customer in the Bank Teller should be the same as bc. It isn't", bt.bc, bc);
	
		assertTrue("The scheduler should return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankDatabase should have logged \"Received msgWithdrawMoney\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgWithdrawMoney"));
		assertTrue("The state of the first task in Bank Teller should be waiting. It isn't.", bt.tasks.get(0).ts == taskState.waiting);
		
		bt.msgWithdrawDone(150.00, 50.00, bc);
		assertTrue("BankTeller should have logged \"Received msgWithdrawDone\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgWithdrawDone"));
		assertTrue("The state of the first task in Bank Teller should be completed. It isn't", bt.tasks.get(0).ts == taskState.completed);
		assertEquals("The account number of the first task should be 123. It isn't", bt.tasks.get(0).accountNumber, 123);
		assertEquals("The balance of the first task should be 200.00. It isn't", bt.tasks.get(0).balance, 150.00);
		assertEquals("The amount of the first task should be 50.00. It isn't", bt.tasks.get(0).amount, 50.00);
	
		assertTrue("The scheduler should  return true. It didn't", bt.pickAndExecuteAnAction());
		assertTrue("BankCustomer should have logged \"Received msgWithdrawDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgWithdrawDone"));
		assertEquals("BankTeller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
	}
}
