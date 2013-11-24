package bank.test.mock;

import bank.interfaces.*;

public class MockBankHost extends Mock implements BankHost {
	
	public EventLog log;
	public MockBankHost(String name){
		super(name);
		log = new EventLog();
	}
	
	public void msgINeedTeller(BankCustomer bc) {
		log.add(new LoggedEvent("Received msgINeedTeller from BankCustomer"));
	}

	public void msgBackToWork(BankTeller bt) {
		log.add(new LoggedEvent("Received msgBackToWork from BankTeller"));
	}
	
	public void msgAtDestination(){
		log.add(new LoggedEvent("Received msgAtDestination from BankHostGui"));
	}

}
