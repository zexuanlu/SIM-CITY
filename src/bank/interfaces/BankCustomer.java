package bank.interfaces;

/** 
 * A basic BankCustomer interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankCustomer {	
	public void msgHereIsTeller(BankTeller bt);
	
	public void msgAccountMade(int accountNumber);
	
	public void msgDepositDone(double balance);
}
