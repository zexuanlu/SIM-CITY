package bank.interfaces;

/** 
 * A basic BankTeller interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankTeller {
	public void msgINeedAccount(BankCustomer bc);
	
	public void msgLeavingBank(BankCustomer bc);
	
	public void msgNewDestination(String location);
	
	public void msgDepositMoney(BankCustomer bc, double amount, int accountNumber);
	
	public void msgWithdrawMoney(BankCustomer bc, double amount, int accountNumber);
	
	public void msgINeedLoan(BankCustomer bc, double amount, int accountNumber);
	
	public void msgAccountCreated(int accountNumber, BankCustomer bc);
	
	public void msgDepositDone(double balance, BankCustomer bc);
	
	public void msgWithdrawDone(double balance, double money, BankCustomer bc);
	
	public void msgLoanGranted(double money, double debt, BankCustomer bc);
	
	public void msgRequestFailed(BankCustomer bc, String type);
	
	public void msgWorkDayOver();
		
	public void msgAtDestination();
}
