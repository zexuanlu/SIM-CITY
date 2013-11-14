package bank;

import java.util.*;
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
	
	//Data
	Map<Integer,Account> accounts;
	List<Request> requests;
	
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
		//This function selects the correct action, and does it
	}
	
	//Utilities
	class Request{
		String type;
		int amount;
		Account a;
		BankCustomer bc;
		BankTeller bt;
		Request(String type, int amount, Account a, BankTeller bt, BankCustomer bc){
			this.type = type;
			this.amount = amount;
			this.a = a;
			this.bt = bt;
			this.bc = bc;
		}
	}
	
	class Account{
		int balance;
		int accountNumber;
		BankCustomer owner;
		int debt;
		Account(BankCustomer owner, int balance, int accountNumber){
			this.owner = owner;
			this.balance = balance;
			this.accountNumber = accountNumber;
		}
	}
}
