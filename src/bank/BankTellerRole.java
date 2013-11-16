package bank;

import java.util.*;
import bank.test.mock.*;
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
	public EventLog log;
	String name;
	public List<Task> tasks;
	public BankDatabase bd;
	//BankHost bh;
	public BankCustomer bc;
	
	public BankTellerRole(String name){
		this.name = name;
		log = new EventLog();
		tasks = new ArrayList<Task>();
	}
	//Messages
	public void msgINeedAccount(BankCustomer bc, int amount){
		//Adds a task to the list of tasks
		this.bc = bc;
		tasks.add(new Task("openAccount", amount));
		stateChanged();
	}
	
	public void msgDepositMoney(BankCustomer bc, int amount, int accountNumber){
		this.bc = bc;
		tasks.add(new Task("deposit", amount, accountNumber));
		stateChanged();
	}
	
	public void msgWithdrawMoney(BankCustomer bc, int amount, int accountNumber){
		this.bc = bc;
		tasks.add(new Task("withdraw", amount, accountNumber));
		stateChanged();
	}
	
	public void msgAccountCreated(int accountNumber, BankCustomer bc){
		log.add(new LoggedEvent("Received msgAccountCreated from BankDatabase"));
		for(Task t : tasks){
			if(t.type.equals("openAccount")){
				t.accountNumber = accountNumber;
				t.ts = taskState.completed;
				stateChanged();
				return;
			}
		}
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction(){
		for(Task t : tasks){
			if(t.ts == taskState.requested){
				switch(t.type){
				case "openAccount": openAccount(t); return true;
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
		bd.msgOpenAccount(bc, t.amount, this);
		t.ts = taskState.waiting;
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
	public class Task{
		public String type;
		public int amount;
		public int balance;
		public int accountNumber;
		public taskState ts;
		Task(String type, int amount, int accountNumber){
			this.type = type;
			this.amount = amount;
			this.accountNumber = accountNumber;
			ts = taskState.requested;
		}
		Task(String type, int amount){
			this.type = type;
			this.amount = amount;
			accountNumber = 0;
			ts = taskState.requested;
		}
	}
	public enum taskState {requested, waiting, completed}
}
