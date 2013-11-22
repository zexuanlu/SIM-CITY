package bank;

import bank.interfaces.*;
import agent.*;
import bank.test.mock.*;
import bank.gui.*;

import java.util.*;
import java.util.concurrent.*;

public class BankCustomerRole extends Agent implements BankCustomer {
	//Data
	String name;
	public EventLog log;
	public List<Task> tasks;
	public int accountNumber;
	public double balance;
	public BankHost bh;
	public BankTeller bt;
	public BankCustomerGui gui;
	public state s;
	Semaphore movement = new Semaphore(0, true);
	
	public BankCustomerRole(String name){
		this.name = name;
		s = state.none;
		tasks = new ArrayList<Task>();
		log = new EventLog();
		accountNumber = -1;
	}
	
	//Messages
	public void msgAtDestination(){
		movement.release();
		stateChanged();
	}
	
	public void msgGoToBank(String task, double amount){
		log.add(new LoggedEvent("Received msgGoToBank from Person"));
		tasks.add(new Task(task, amount));
		s = state.needTeller;
		stateChanged();
	}
	
	public void msgHereIsTeller(BankTeller bt){
		this.bt = bt;
		s = state.haveTeller;
		stateChanged();
	}
	
	public void msgAccountMade(int accountNumber){
		log.add(new LoggedEvent("Received msgAccountMade from BankTeller"));
		this.accountNumber = accountNumber;
		this.balance = 0;
		this.s = state.atTeller;
		stateChanged();
	}
	
	public void msgDepositDone(double balance){
		log.add(new LoggedEvent("Received msgDepositDone from BankTeller"));
		this.balance = balance;
		this.s = state.atTeller;
		stateChanged();
	}
	
	public void msgWithdrawDone(double balance, double money){
		log.add(new LoggedEvent("Received msgWithdrawDone from BankTeller"));
		this.balance = balance;
		//Person.addMoney(money);
		this.s = state.atTeller;
		stateChanged();
	}
	//Scheduler
	public boolean pickAndExecuteAnAction(){
		if(s == state.needTeller){
			informHost();
			return true;
		}
		if(s == state.haveTeller){
			goToLocation("Teller");
			s = state.atTeller;
			return true;
		}
		if(s == state.atTeller && accountNumber == -1){
			openAccount();
			return true;
		}
		if(s == state.atTeller && !tasks.isEmpty()){
			bankingAction(tasks.get(0));
			return true;
		}
		if(s == state.atTeller && tasks.isEmpty()){
			leaveBank();
			return true;
		}
		return false;
	}
	//Actions
	private void informHost(){
		goToLocation("Host");
		Do("Requesting a Teller");
		bh.msgINeedTeller(this);
		s = state.waiting;
	}
	
	private void openAccount(){
		Do("Requesting account");
		bt.msgINeedAccount(this);
		s = state.waiting;
	}
	
	private void bankingAction(Task t){
		if(t.type.equals("deposit")){
			Do("Requesting deposit");
			bt.msgDepositMoney(this, t.amount, accountNumber);
		}
		if(t.type.equals("withdraw")){
			Do("Requesting withdrawal");
			bt.msgWithdrawMoney(this, t.amount, accountNumber);
		}
		tasks.remove(t);
		s = state.waiting;
	}
	
	private void leaveBank(){
		bt.msgLeavingBank(this);
		goToLocation("Outside");
		s = state.none;
	}
	
	private void goToLocation(String location){
		gui.DoGoToLocation(location);
		Do("Moving to " + location);
		try{
			movement.acquire();
		}
		catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	//Utilities
	public String toString(){
		return name;
	}
	
	public class Task{
		public String type;
		public double amount;
		Task(String type, double amount){
			this.type = type;
			this.amount = amount;
		}
	}
	
	public enum state {needTeller, waiting, haveTeller, atTeller, none}
}
