package bank.test.mock;

import bank.interfaces.*;

public class MockBankDatabase extends Mock implements BankDatabase {
	
	public EventLog log;
	public MockBankDatabase(String name){
		super(name);
		log = new EventLog();
	}
	
	public void msgLoanPlease(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		log.add(new LoggedEvent("Received msgLoanPlease from BankTeller"));
	}
	
	public void msgOpenAccount(BankCustomer bc, BankTeller bt){
		log.add(new LoggedEvent("Received msgOpenAccount from BankTeller"));
	}
	
	public void msgDepositMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		log.add(new LoggedEvent("Received msgDepositMoney from BankTeller. The account number is " + accountNumber + " and the amount is " + money));
	}

	public void msgWithdrawMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankTeller. The account number is " + accountNumber + " and the amount is " + money));
	}

	public void msgGiveAllMoney(BankTeller bt, double amount) {
		log.add(new LoggedEvent("Received msgGiveAllMoney from BankTeller"));
	}
}
