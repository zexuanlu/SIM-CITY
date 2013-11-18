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
		accounts = new HashMap<Integer, Account>();
		log = new EventLog();
	}
	
	//Messages
	public void msgOpenAccount(BankCustomer bc, BankTeller bt){
		requests.add(new Request(bt, bc));
		log.add(new LoggedEvent("Received msgOpenAccount from BankTeller"));
		stateChanged();
	}
	
	public void msgDepositMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		requests.add(new Request("deposit", accounts.get(accountNumber), money, bt, bc));
		log.add(new LoggedEvent("Received msgDepositMoney from BankTeller"));
		stateChanged();
	}
	
	public void msgWithdrawMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		requests.add(new Request("withdraw", accounts.get(accountNumber), money, bt, bc));
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankTeller"));
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
		//Open a new account
		if(r.type.equals("openAccount")){
			int AccountNumber = (int)(Math.random()*1000);
			while(accounts.containsKey(AccountNumber)){
				AccountNumber = (int)(Math.random()*1000);
			}
			Do("Creating Account Number " + AccountNumber);
			accounts.put(AccountNumber, new Account(r.bc, 0, AccountNumber));
			r.bt.msgAccountCreated(AccountNumber, r.bc);
			requests.remove(r);
			return;
		}
		//Deposit Money
		if(r.type.equals("deposit")){
			if(r.a.debt == 0){
				Do("Completed deposit of " + r.amount);
				r.a.balance += r.amount;
			}
			else{
				r.a.balance += (r.amount/2.0);
				r.a.debt -= (r.amount/2.0);
				if(r.a.debt < 0){
					r.a.balance -= r.a.debt;
					r.a.debt = 0;
				}
			}
			r.bt.msgDepositDone(r.a.balance, r.bc);
			requests.remove(r);
			return;
		}
		//Withdraw Money
		if(r.type.equals("withdraw")){
			if(r.a.balance > r.amount){
				r.a.balance -= r.amount;
			}
			else{
				r.amount = r.a.balance;
				r.a.balance = 0;
			}
			Do("Completed withdrawal of " + r.amount);
			r.bt.msgWithdrawDone(r.a.balance, r.amount, r.bc);
			requests.remove(r);
		}
}
	
	//Utilities
	public String toString(){
		return "Bank Database";
	}
	
	class Request{
		String type;
		double amount;
		Account a;
		BankCustomer bc;
		BankTeller bt;
		Request(String type, Account a, double amount, BankTeller bt, BankCustomer bc){
			this.type = type;
			this.amount = amount;
			this.a = a;
			this.bt = bt;
			this.bc = bc;
		}
		public Request(BankTeller bt2, BankCustomer bc2) {
			type = "openAccount";
			bt = bt2;
			bc = bc2;
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
