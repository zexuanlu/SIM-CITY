package bank.interfaces;


/** 
 * A basic BankDatabase interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankDatabase {
	
	public void msgOpenAccount(BankCustomer bc, BankTeller bt);
	
	public void msgDepositMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt);

	public void msgWithdrawMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt);
}

