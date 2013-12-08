package bank.test;

import junit.framework.*;
import bank.test.mock.*;
import person.test.mock.*;
import bank.*;
import bank.BankCustomerRole.state;

/**
 * This class is a J-Unit TestCase designed to test the basic functionality 
 * of the BankCustomerRole in its interactions with the other agents
 * 
 * @author Joseph
 *
 */
public class BankCustomerTest extends TestCase {
	
	BankCustomerRole bc;
	PersonMock p;
	MockBankTeller bt;
	MockBankHost bh;
	
	public void setUp() throws Exception{
		super.setUp();		
		p = new PersonMock("Person");
		bt = new MockBankTeller("BankTeller1");
		bc = new BankCustomerRole(p,"BankCustomer1");
		bh = new MockBankHost("BankDatabase");
		bc.bh = bh;
	}
	
	public void testAccountCreationAndDeposit(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		
		bc.msgGoToBank("deposit", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be deposit. It isn't.", bc.tasks.get(0).type, "deposit");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedAccount\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedAccount"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		bc.msgAccountMade(123);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgAccountMade\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgAccountMade"));
		assertTrue("The account number of Bank Customer should be 123. It isn't.", bc.accountNumber == 123);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgDepositMoney\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgDepositMoney"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgDepositDone(200.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgDepositDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgDepositDone"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testAccountCreationAndWithdrawal(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		
		bc.msgGoToBank("withdraw", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be withdraw. It isn't.", bc.tasks.get(0).type, "withdraw");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedAccount\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedAccount"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		bc.msgAccountMade(123);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgAccountMade\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgAccountMade"));
		assertTrue("The account number of Bank Customer should be 123. It isn't.", bc.accountNumber == 123);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgWithdrawMoney\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgWithdrawMoney"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgWithdrawDone(200.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgWithdrawDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgWithdrawDone"));
		assertTrue("Person should have logged \"Cash has been added\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Cash has been added"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testDepositWithAccount(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		bc.accountNumber = 123;
		
		bc.msgGoToBank("deposit", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be deposit. It isn't.", bc.tasks.get(0).type, "deposit");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgDepositMoney\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgDepositMoney"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		bc.msgDepositDone(200.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgDepositDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgDepositDone"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testWithdrawWithAccount(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		bc.accountNumber = 123;
		
		bc.msgGoToBank("withdraw", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be withdraw. It isn't.", bc.tasks.get(0).type, "withdraw");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgWithdrawMoney\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgWithdrawMoney"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgWithdrawDone(200.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgWithdrawDone\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgWithdrawDone"));
		assertTrue("Person should have logged \"Cash has been added\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Cash has been added"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testAccountCreationAndLoanGranted(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		
		bc.msgGoToBank("getLoan", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be getLoan. It isn't.", bc.tasks.get(0).type, "getLoan");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedAccount\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedAccount"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		bc.msgAccountMade(123);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgAccountMade\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgAccountMade"));
		assertTrue("The account number of Bank Customer should be 123. It isn't.", bc.accountNumber == 123);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedLoan\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedLoan"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgLoanGranted(100.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgLoanGranted\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgLoanGranted"));
		assertTrue("Person should have logged \"Cash has been added\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Cash has been added"));
		
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testAccountCreationAndLoanFailed(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		
		bc.msgGoToBank("getLoan", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be getLoan. It isn't.", bc.tasks.get(0).type, "getLoan");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedAccount\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedAccount"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		bc.msgAccountMade(123);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgAccountMade\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgAccountMade"));
		assertTrue("The account number of Bank Customer should be 123. It isn't.", bc.accountNumber == 123);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedLoan\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedLoan"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgRequestFailed("getLoan");
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgRequestFailed\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgRequestFailed"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testLoanGrantedWithAccount(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		bc.accountNumber = 123;
		
		bc.msgGoToBank("getLoan", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be getLoan. It isn't.", bc.tasks.get(0).type, "getLoan");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedLoan\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedLoan"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgLoanGranted(100.00, 100.00);
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgLoanGranted\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgLoanGranted"));
		assertTrue("Person should have logged \"Cash has been added\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Cash has been added"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testLoanFailedWithAccount(){
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
		bc.accountNumber = 123;
		
		bc.msgGoToBank("getLoan", 100.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be getLoan. It isn't.", bc.tasks.get(0).type, "getLoan");
		assertEquals("The amount of the first task should be 100.00. It isn't.", bc.tasks.get(0).amount, 100.00);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankHost should have logged \"Received msgINeedTeller\" but didn't. His log reads instead: " 
				+ bh.log.getLastLoggedEvent().toString(), bh.log.containsString("Received msgINeedTeller"));
		
		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgINeedLoan\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgINeedLoan"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgRequestFailed("getLoan");
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		assertTrue("BankCustomer should have logged \"Received msgRequestFailed\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgRequestFailed"));
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testRobberySuccess(){
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
	
		bc.msgGoToBank("robBank", 10000.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be robBank. It isn't.", bc.tasks.get(0).type, "robBank");
		assertEquals("The amount of the first task should be 10000.00. It isn't.", bc.tasks.get(0).amount, 10000.00);
	
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		assertFalse("The scheduler of the bank customer should return false. It didn't.", bc.pickAndExecuteAnAction());

		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgThisIsAHoldup\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgThisIsAHoldup"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgHereIsMoney(10000.00);
		assertTrue("The state of the bank customer should be runAway. It isn't.", bc.s == state.runAway);
		assertTrue("BankCustomer should have logged \"Received msgHereIsMoney\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgHereIsMoney"));
		assertTrue("Person should have logged \"Cash has been added\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Cash has been added"));
	
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("Person should have logged \"Received msgBanished\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Received msgBanished"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
	
	public void testRobberyFailure(){
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		assertTrue("The state of the bank customer should be none. It isn't", bc.s == state.none);
	
		bc.msgGoToBank("robBank", 10000.00);
		assertEquals("The bank customer should have 1 task on it. It doesn't.", bc.tasks.size(), 1);
		assertTrue("BankCustomer should have logged \"Received msgGoToBank\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgGoToBank"));
		assertTrue("The state of the bank customer should be needTeller. It isn't", bc.s == state.needTeller);
		assertEquals("The type of the first task should be robBank. It isn't.", bc.tasks.get(0).type, "robBank");
		assertEquals("The amount of the first task should be 10000.00. It isn't.", bc.tasks.get(0).amount, 10000.00);
	
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		
		assertFalse("The scheduler of the bank customer should return false. It didn't.", bc.pickAndExecuteAnAction());

		bc.msgHereIsTeller(bt, "Teller1");
		assertEquals("The Bank Teller should match the bank teller. It doesn't", bc.bt, bt);
		assertEquals("The destination of Bank Customer should be Teller1. It isn't", bc.destination, "Teller1");
		assertTrue("The state of the bank customer should be haveTeller. It isn't.", bc.s == state.haveTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("The state of the bank customer should be atTeller. It isn't.", bc.s == state.atTeller);
		
		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgThisIsAHoldup\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgThisIsAHoldup"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.waiting);
		assertEquals("The bank customer should have 0 tasks in it. It doesn't.", bc.tasks.size(), 0);
		
		bc.msgCallingCops();
		assertTrue("The state of the bank customer should be runAway. It isn't.", bc.s == state.runAway);
		assertTrue("BankCustomer should have logged \"Received msgCallingCops\" but didn't. His log reads instead: " 
				+ bc.log.getLastLoggedEvent().toString(), bc.log.containsString("Received msgCallingCops"));

		assertTrue("The scheduler of the bank customer should return true. It didn't.", bc.pickAndExecuteAnAction());
		assertTrue("BankTeller should have logged \"Received msgLeavingBank\" but didn't. His log reads instead: " 
				+ bt.log.getLastLoggedEvent().toString(), bt.log.containsString("Received msgLeavingBank"));
		assertTrue("Person should have logged \"Received msgBanished\" but didn't. His log reads instead: "
				+ p.log.getLastLoggedEvent().toString(), p.log.containsString("Received msgBanished"));
		assertTrue("The state of the bank customer should be waiting. It isn't.", bc.s == state.none);
		assertFalse("The scheduler should return false. It didn't.", bc.pickAndExecuteAnAction());
	}
}
