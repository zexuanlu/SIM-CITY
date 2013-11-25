package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import bank.*;


/**
 * This class is a J-Unit TestCase designed to test the basic functionality 
 * of the BankDatabaseAgent in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankDatabaseTest extends TestCase {

	MockBankTeller bt;
	BankDatabaseAgent bd;
	MockBankCustomer bc;

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new MockBankTeller("BankTeller1");
		bd = new BankDatabaseAgent();
		bc = new MockBankCustomer("BankCustomer1");
	}
	
	public void testAccountCreation(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		bd.msgOpenAccount(bc, bt);
		assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
		assertTrue("Bank Database should have logged \"Received msgOpenAccount\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgOpenAccount"));
		assertEquals("The type of the request should be openAccount. It isn't.", bd.requests.get(0).type, "openAccount");
		assertEquals("The amount of the request should be 0.0. It isn't.", bd.requests.get(0).amount, 0.0);
		assertEquals("The account of the request should be null. It isn't.", bd.requests.get(0).a, null);
		assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
		assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
		
		assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
		assertTrue("Bank Teller should have logged \"Received msgAccountCreated\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgAccountCreated"));
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
	
		assertFalse("The scheduler should return false. It didn't", bd.pickAndExecuteAnAction());
	}
	
	public void testDeposit(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		bd.addAccount(bc, 100.00, 123);
		assertEquals("Bank Database should have 1 account in it. It doesn't.", bd.accounts.size(), 1);
		
		bd.msgDepositMoney(bc, 50.00, 123, bt);
		assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
		assertTrue("Bank Database should have logged \"Received msgDepositMoney\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgDepositMoney"));
		assertEquals("The type of the request should be deposit. It isn't.", bd.requests.get(0).type, "deposit");
		assertEquals("The amount of the request should be 50.0. It isn't.", bd.requests.get(0).amount, 50.0);
		assertEquals("The accountNumber of the request should be 123. It isn't.", bd.requests.get(0).a.accountNumber, 123);
		assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
		assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
		
		assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
		assertTrue("Bank Teller should have logged \"Received msgDepositDone\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgDepositDone"));
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		assertEquals("Bank Database should have 1 account in it. It doesn't.", bd.accounts.size(), 1);
		
		assertFalse("The scheduler should return false. It didn't", bd.pickAndExecuteAnAction());
	}
	
	public void testWithdraw(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		bd.addAccount(bc, 100.00, 123);
		assertEquals("Bank Database should have 1 account in it. It doesn't.", bd.accounts.size(), 1);
		
		bd.msgWithdrawMoney(bc, 50.00, 123, bt);
		assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
		assertTrue("Bank Database should have logged \"Received msgWithdrawMoney\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgWithdrawMoney"));
		assertEquals("The type of the request should be withdraw. It isn't.", bd.requests.get(0).type, "withdraw");
		assertEquals("The amount of the request should be 50.0. It isn't.", bd.requests.get(0).amount, 50.0);
		assertEquals("The accountNumber of the request should be 123. It isn't.", bd.requests.get(0).a.accountNumber, 123);
		assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
		assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
		
		assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
		assertTrue("Bank Teller should have logged \"Received msgWithdrawDone\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgWithdrawDone"));
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
	
		assertFalse("The scheduler should return false. It didn't", bd.pickAndExecuteAnAction());
	}
	
	public void testGetLoanSuccess(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		bd.addAccount(bc, 100.00, 123);
		assertEquals("Bank Database should have 1 account in it. It doesn't.", bd.accounts.size(), 1);
		
		bd.msgLoanPlease(bc, 50.00, 123, bt);
		assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
		assertTrue("Bank Database should have logged \"Received msgLoanPlease\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgLoanPlease"));
		assertEquals("The type of the request should be getLoan. It isn't.", bd.requests.get(0).type, "getLoan");
		assertEquals("The amount of the request should be 50.0. It isn't.", bd.requests.get(0).amount, 50.0);
		assertEquals("The accountNumber of the request should be 123. It isn't.", bd.requests.get(0).a.accountNumber, 123);
		assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
		assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
		
		assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
		assertTrue("Bank Teller should have logged \"Received msgLoanGranted\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLoanGranted"));
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
	
		assertFalse("The scheduler should return false. It didn't", bd.pickAndExecuteAnAction());
	}
	
	public void getLoanFailure(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		bd.addAccount(bc, 100.00, 123);
		assertEquals("Bank Database should have 1 account in it. It doesn't.", bd.accounts.size(), 1);
		
		bd.msgLoanPlease(bc, 500.00, 123, bt);
		assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
		assertTrue("Bank Database should have logged \"Received msgLoanPlease\" but didn't. His log reads instead: " 
				+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgLoanPlease"));
		assertEquals("The type of the request should be getLoan. It isn't.", bd.requests.get(0).type, "getLoan");
		assertEquals("The amount of the request should be 50.0. It isn't.", bd.requests.get(0).amount, 50.0);
		assertEquals("The accountNumber of the request should be 123. It isn't.", bd.requests.get(0).a.accountNumber, 123);
		assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
		assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
		
		assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
		assertTrue("Bank Teller should have logged \"Received msgLoanFailed\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLoanFailed"));
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
	
		assertFalse("The scheduler should return false. It didn't", bd.pickAndExecuteAnAction());
	}
	
	public void testMassAccount(){
		assertEquals("Bank Database should have 0 accounts in it. It doesn't.", bd.accounts.size(), 0);
		assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		
		for(int i = 0; i < 1000; i++){
			bd.msgOpenAccount(bc, bt);
			assertEquals("Bank Database should have 1 request in it. It doesn't.", bd.requests.size(),1);
			assertTrue("Bank Database should have logged \"Received msgOpenAccount\" but didn't. His log reads instead: " 
					+ bd.log.getLastLoggedEvent().toString(), bd.log.containsString("Received msgOpenAccount"));
			assertEquals("The type of the request should be openAccount. It isn't.", bd.requests.get(0).type, "openAccount");
			assertEquals("The amount of the request should be 0.0. It isn't.", bd.requests.get(0).amount, 0.0);
			assertEquals("The account of the request should be null. It isn't.", bd.requests.get(0).a, null);
			assertEquals("The bank teller of the request should be right. It isn't.", bd.requests.get(0).bt, bt);
			assertEquals("The bank customer of the request should be right. It isn't.", bd.requests.get(0).bc, bc);
			
			assertTrue("The scheduler should return true. It didn't.", bd.pickAndExecuteAnAction());
			assertTrue("Bank Teller should have logged \"Received msgAccountCreated\" but didn't. His log reads instead: " 
					+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgAccountCreated"));
			assertEquals("Bank Database should have 0 requests in it. It doesn't.", bd.requests.size(), 0);
		}
		assertEquals("Bank Database should have 1000 accounts in it. It doesn't.", bd.accounts.size(), 1000);
		
		assertFalse("The scheduler should return false. It didn't.", bd.pickAndExecuteAnAction());
	}
}
