package bank;

import java.util.*;
import bank.interfaces.*;
import agent.*;

/**
 * class BankTellerRole
 * This role is the bank teller, who manages requests from people to access and 
 * change their current bank account info
 * @author Joseph
 *
 */
public class BankTellerRole extends Role implements BankTeller {

	//Data
	List<Task> tasks;
	//BankDatabase bd;
	//BankHost bh;
	//BankCustomer bc;
	
	//Scheduler
	protected boolean pickAndExecuteAnAction(){
		for(Task t : tasks){
			if(t.ts == taskState.requested){
				switch(t.type){
				case "makeAccount": openAccount(t); return true;
				case "deposit": deposit(t); return true;
				case "withdraw": withdraw(t); return true;
				case "getLoan": getLoan(t); return true;
				}
			}
		}
		return false;
	}
	
	//Actions
	private void openAccount(Task t){
		//Requests a new account from the BankDatabase
	}
	
	private void deposit(Task t){
		//Requests a deposit to an account in the BankDatabase
	}
	
	private void withdraw(Task t){
		//Requests a withdrawal from an account in the BankDatabase
	}
	
	private void getLoan(Task t){
		//Requests a loan for an account in the BankDatabase
	}
	//Utilities
	class Task{
		String type;
		int amount;
		int balance;
		int accountNumber;
		taskState ts;
		Task(String type, int amount, int accountNumber){
			this.type = type;
			this.amount = amount;
			this.accountNumber = accountNumber;
		}
	}
	enum taskState {requested, waiting, completed}
}
