package bank.test.mock;

import bank.interfaces.*;

/**
 * A sample MockBankCustomer built to unit test the rest of the bank.
 *
 * @author Joseph Boman
 *
 */
public class MockBankCustomer extends Mock implements BankCustomer {


	public EventLog log;
	public MockBankCustomer(String name) {
		super(name);
		log = new EventLog();

	}
	
	public void msgAtDestination(){
		
	}

	@Override
	public void msgGoToBank(String type, double money){
		log.add(new LoggedEvent("Received msgGoToBank from person with type " + type + " and amount "+ money));
	}
	
	public void msgHereIsTeller(BankTeller bt, String location){
		log.add(new LoggedEvent("Received msgHereIsTeller from BankHost"));
	}
	
	public void msgAccountMade(int accountNumber){
		log.add(new LoggedEvent("Received msgAccountMade from BankTeller. My number is " + accountNumber));
	}
	
	public void msgDepositDone(double balance, double money){
		log.add(new LoggedEvent("Received msgDepositDone from BankTeller. My new balance is " + balance));
	}
	
	public void msgWithdrawDone(double balance, double money){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankTeller. My new balance is " + balance + " and I received " + money + " dollars"));
	}
	
	public void msgLoanGranted(double money, double debt){
		log.add(new LoggedEvent("Received msgLoanGranted from BankTeller"));
	}

	public void msgLoanFailed(){
		log.add(new LoggedEvent("Received msgLoanFailed from BankTeller"));
	}
	
}
