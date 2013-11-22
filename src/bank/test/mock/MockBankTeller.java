package bank.test.mock;

import bank.interfaces.*;

public class MockBankTeller extends Mock implements BankTeller {
	
	public EventLog log;
	public MockBankTeller(String name){
		super(name);
		log = new EventLog();
	}

	public void msgINeedLoan(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgINeedLoan from BankCustomer"));
	}
	public void msgINeedAccount(BankCustomer bc){
		log.add(new LoggedEvent("Received msgINeedAccount from BankCustomer"));
	}

	public void msgLeavingBank(BankCustomer bc){
		log.add(new LoggedEvent("Received msgLeavingBank from BankCustomer"));
	}
	
	public void msgDepositMoney(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgDepositMoney from BankCustomer. The amount is " + amount + " and the account number is " + accountNumber));
	}
	
	public void msgWithdrawMoney(BankCustomer bc, double amount, int accountNumber){
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankCustomer. The amount is " + amount + " and the account number is " + accountNumber));
	}
	
	public void msgAccountCreated(int accountNumber, BankCustomer bc){
		log.add(new LoggedEvent("Received msgAccountCreated from BankDatabase. The account number is " + accountNumber));
	}
	
	public void msgDepositDone(double balance, BankCustomer bc){
		log.add(new LoggedEvent("Received msgDepositDone from BankDatabase. The new balance is " + balance));
	}
	
	public void msgWithdrawDone(double balance, double money, BankCustomer bc){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankDatabase. The new balance is " + balance + " and the amount withdrawn is " + money));
	}

	public void msgLoanGranted(double money, double debt, BankCustomer bc) {
		log.add(new LoggedEvent("Received msgLoanGranted from BankDatabase"));
	}
	
	public void msgDoneWithWork(double pay){
		log.add(new LoggedEvent("Received msgDoneWithWork from BankTimeCard"));
	}
}
