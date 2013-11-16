package bank;

import java.util.*;

import bank.test.mock.*;
import agent.*;
import bank.interfaces.*;

/**
 * This class is an agent that behaves as a computer system, handling
 * requests from bank tellers to manipulate bank accounts and sends
 * the info back to the tellers
 * 
 * @author Joseph
 *
 */
public class BankDatabaseAgent extends Agent implements BankDatabase {
	
	//Data\
	public EventLog log;
	String name;
	Map<Integer,Account> accounts;
	List<Request> requests = new ArrayList<Request>();
	
	public BankDatabaseAgent(String name){
		this.name = name;
		log = new EventLog();
	}
	
	//Messages
	public void msgOpenAccount(BankCustomer bc, double money, BankTeller bt){
		requests.add(new Request(money, bt, bc));
		log.add(new LoggedEvent("Received msgOpenAccount from BankTeller"));
		stateChanged();
	}
	//Scheduler
	protected boolean pickAndExecuteAnAction(){
		if(!requests.isEmpty()){
			performBankAction(requests.get(0));
			return true;
		}
		return false;
	}
	
	//Actions
	private void performBankAction(Request r){
		if(r.type.equals("openAccount")){
			int AccountNumber = ((int)Math.random());
			accounts.put(AccountNumber, new Account(r.bc, r.amount, AccountNumber));
			r.bt.msgAccountCreated(AccountNumber, r.bc);
		}	
}
	
	//Utilities
	class Request{
		String type;
		double amount;
		Account a;
		BankCustomer bc;
		BankTeller bt;
		Request(String type, double amount, Account a, BankTeller bt, BankCustomer bc){
			this.type = type;
			this.amount = amount;
			this.a = a;
			this.bt = bt;
			this.bc = bc;
		}
		public Request(double money, BankTeller bt2, BankCustomer bc2) {
			type = "openAccount";
			bt = bt2;
			bc = bc2;
			amount = money;
		}
	}
	
	class Account{
		double balance;
		double accountNumber;
		BankCustomer owner;
		double debt;
		Account(BankCustomer owner, double balance, int accountNumber){
			this.owner = owner;
			this.balance = balance;
			this.accountNumber = accountNumber;
		}
	}
}
