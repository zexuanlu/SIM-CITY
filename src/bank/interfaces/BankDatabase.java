package bank.interfaces;


/** 
 * A basic BankDatabase interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankDatabase {
	
	public void msgOpenAccount(BankCustomer bc, double money, BankTeller bt);
}

