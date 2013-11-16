package bank;

import bank.interfaces.*;
import agent.*;
import java.util.*;

public class BankCustomerRole extends Role implements BankCustomer {
	//Data
	String name;
	List<Task> tasks;
	int accountNumber;
	double balance;
	BankTeller bt;
	state s;
	
	public BankCustomerRole(String name){
		this.name = name;
		tasks = new ArrayList<Task>();
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
	
	//Scheduler
	protected boolean pickAndExecuteAnAction(){
		if(s == state.needTeller){
			informHost();
			return true;
		}
		else if(s == state.haveTeller){
			//goToLocation("Teller");
			s = state.atTeller;
			return true;
		}
		return false;
	}
	//Actions
	private void informHost(){
		//goToLocation("Host");
		//bh.iNeedTeller();
		s = state.waiting;
	}
	
	
	//Utilities
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
