package bank;

import bank.interfaces.*;
import agent.*;
import bank.test.mock.*;
import java.util.*;

public class BankCustomerRole extends Agent implements BankCustomer {
	//Data
	String name;
	public EventLog log;
	List<Task> tasks;
	int accountNumber;
	double balance;
	public BankHost bh;
	public BankTeller bt;
	state s;
	
	public BankCustomerRole(String name){
		this.name = name;
		tasks = new ArrayList<Task>();
		log = new EventLog();
		accountNumber = -1;
	}
	
	//Messages
	public void msgGoToBank(String task, double amount){
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
	protected boolean pickAndExecuteAnAction(){
		if(s == state.needTeller){
			informHost();
			return true;
		}
		if(s == state.haveTeller){
			//goToLocation("Teller");
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
		//goToLocation("Host");
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
		Do("Leaving Bank");
		s = state.none;
	}
	//Utilities
	public String toString(){
		return name;
	}
	
	class Task{
		String type;
		double amount;
		Task(String type, double amount){
			this.type = type;
			this.amount = amount;
		}
	}
	
	enum state {needTeller, waiting, haveTeller, atTeller, runningAway, none}
}
