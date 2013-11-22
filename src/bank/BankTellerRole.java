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
public class BankTellerRole extends Agent implements BankTeller {

	//Data
	public EventLog log;
	String name;
	public List<Task> tasks;
	public BankDatabase bd;
	BankHost bh;
	public BankCustomer bc;
	state s;
	enum state {working, notWorking, backToWork}
	
	public BankTellerRole(String name){
		this.name = name;
		log = new EventLog();
		tasks = new ArrayList<Task>();
	}
	//Messages
	public void msgBackToWork(BankHost bh){
		s = state.backToWork;
		this.bh = bh;
		stateChanged();
	}
	
	public void msgINeedAccount(BankCustomer bc){
		this.bc = bc;
		tasks.add(new Task("openAccount"));
		stateChanged();
	}
	
	public void msgDepositMoney(BankCustomer bc, double amount, int accountNumber){
		Do("Received request for deposit");
		this.bc = bc;
		tasks.add(new Task("deposit", amount, accountNumber));
		stateChanged();
	}
	
	public void msgWithdrawMoney(BankCustomer bc, double amount, int accountNumber){
		this.bc = bc;
		tasks.add(new Task("withdraw", amount, accountNumber));
		stateChanged();
	}
	public void msgINeedLoan(BankCustomer bc, double amount, int accountNumber){
		this.bc = bc;
		tasks.add(new Task("getLoan", amount, accountNumber));
		stateChanged();
	}
	public void msgAccountCreated(int accountNumber, BankCustomer bc){
		log.add(new LoggedEvent("Received msgAccountCreated from BankDatabase"));
		for(Task t : tasks){
			if(t.type.equals("openAccount")){
				Do("Account Number " + accountNumber + " Created");
				t.accountNumber = accountNumber;
				t.ts = taskState.completed;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgDepositDone(double balance, BankCustomer bc){
		log.add(new LoggedEvent("Received msgDepositDone from BankDatabase"));
		for(Task t : tasks){
			if(t.type.equals("deposit")){
				t.balance = balance;
				t.ts = taskState.completed;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgWithdrawDone(double balance, double money, BankCustomer bc){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankDatabase"));
		for(Task t : tasks){
			if(t.type.equals("withdraw")){
				t.balance = balance;
				t.amount = money;
				t.ts = taskState.completed;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgLoanGranted(double money, double debt, BankCustomer bc){
		log.add(new LoggedEvent("Received msgLoanGranted from BankDatabase"));
		for(Task t : tasks){
			if(t.type.equals("getLoan")){
				t.amount = money;
				t.ts = taskState.completed;
				t.balance = debt;
				stateChanged();
				return;
			}
		}
	}
	
	public void msgLeavingBank(BankCustomer bc){
		log.add(new LoggedEvent("Received msgLeavingBank from BankCustomer"));
		s = state.backToWork;
		stateChanged();		
	}
	//Scheduler
	public boolean pickAndExecuteAnAction(){
		for(Task t : tasks){
			if(t.ts == taskState.completed){
				switch(t.type){
				case "openAccount": accountMade(t); return true;
				case "deposit": depositMade(t); return true;
				case "withdraw": withdrawMade(t); return true;
				case "getLoan": loanMade(t); return true;
				}
			}
		}
		
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
		if(s == state.backToWork){
			informHost();
			return true;
		}
		return false;
	}
	
	//Actions
	private void openAccount(Task t){
		Do("Requesting an account");
		bd.msgOpenAccount(bc, this);
		t.ts = taskState.waiting;
	}
	
	private void accountMade(Task t){
		Do("Informing Customer of account");
		bc.msgAccountMade(t.accountNumber);
		tasks.remove(t);
	}
	
	private void deposit(Task t){
		Do("Requesting a deposit");
		bd.msgDepositMoney(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	
	private void depositMade(Task t){
		Do("Telling customer of his deposit. Current balance is " + t.balance);
		bc.msgDepositDone(t.balance);
		tasks.remove(t);
	}
	
	private void withdraw(Task t){
		bd.msgWithdrawMoney(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	
	private void withdrawMade(Task t){
		bc.msgWithdrawDone(t.balance, t.amount);
		tasks.remove(t);
	}
	
	private void getLoan(Task t){
		bd.msgLoanPlease(bc, t.amount, t.accountNumber, this);
		t.ts = taskState.waiting;
	}
	private void loanMade(Task t){
		bc.msgLoanGranted(t.amount, t.balance);
		tasks.remove(t);
	}
	

	private void informHost(){
		Do("Telling host that I am working");
		bh.msgBackToWork(this);
		s = state.working;
	}
	//Utilities
	public String toString(){
		return name;
	}
	
	public void setHost(BankHost bh){
		this.bh = bh;
	}
	
	public class Task{
		public String type;
		public double amount;
		public double balance;
		public int accountNumber;
		public taskState ts;
		Task(String type, double amount, int accountNumber){
			this.type = type;
			this.amount = amount;
			this.accountNumber = accountNumber;
			ts = taskState.requested;
		}
		Task(String type){
			this.type = type;
			accountNumber = 0;
			ts = taskState.requested;
		}
	}
	public enum taskState {requested, waiting, completed}
}
