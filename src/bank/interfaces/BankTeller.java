package bank.interfaces;

/** 
 * A basic BankTeller interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankTeller {
	public void msgINeedAccount(BankCustomer bc, int amount);
	
	public void msgDepositMoney(BankCustomer bc, int amount, int accountNumber);
	
	public void msgWithdrawMoney(BankCustomer bc, int amount, int accountNumber);	
}
