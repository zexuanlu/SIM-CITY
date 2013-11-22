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

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new MockBankTeller("BankTeller1");
		bd = new BankDatabaseAgent("Bank Database");
	}
	
	public void testOneTeller(){

	}
}
