package bank.interfaces;

import utilities.restaurant.RestaurantCashier;


/** 
 * A basic BankDatabase interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankDatabase {
	
	public void msgOpenAccount(BankCustomer bc, BankTeller bt);
	
	public void msgLoanPlease(BankCustomer bc, double money, int accountNumber, BankTeller bt);
	
	public void msgDepositMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt);

	public void msgWithdrawMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt);

	public void msgGiveAllMoney(BankTeller bt, double amount);

	public void msgWithdrawMoney(RestaurantCashier cashier, double money, int accountNumber);

	public void msgDepositMoney(RestaurantCashier cashier, double money, int accountNumber);

}

