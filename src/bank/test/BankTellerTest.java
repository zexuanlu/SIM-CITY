package bank.test;

import junit.framework.*;
import bank.interfaces.*;
import bank.BankTellerRole;

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

	/**
	 * Sets up the basic agents being used in all of the following tests
	 */
	public void setUp() throws Exception{
		super.setUp();		
		bt = new BankTellerRole("BankTeller1");
	}	
	
	public void testOne(){
		assertEquals("Bank Teller should have 0 tasks in it. It doesn't", bt.tasks.size(), 0);
	}
}
