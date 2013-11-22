package bank;

import agent.*;
import bank.interfaces.*;
import java.util.*;
import bank.test.mock.*;

public class BankHostRole extends Role implements BankHost {


	//Data
	String name;
	public EventLog log;
	public List<BankCustomer> waitingCustomers;
	public List<MyTeller> tellers;
	public BankHostRole(String name){
		this.name = name;
		waitingCustomers = new ArrayList<BankCustomer>();
		tellers = new ArrayList<MyTeller>();
		log = new EventLog();
	}
	
	//Messages
	public void msgINeedTeller(BankCustomer bc) {
		log.add(new LoggedEvent("Received msgINeedTeller from Bank Customer"));
		waitingCustomers.add(bc);
		stateChanged();
	}

	public void msgBackToWork(BankTeller bt){
		log.add(new LoggedEvent("Receieved msgBackToWork from Bank Teller"));
		for(MyTeller mt : tellers){
			if(mt.bt == bt){
				mt.s = state.working;
				stateChanged();
				return;
			}
		}		
	}
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		if(!waitingCustomers.isEmpty()){
			for(MyTeller mt : tellers){
				if(mt.s == state.working){
					assignCustomer(waitingCustomers.get(0), mt);
					return true;
				}
			}
		}
		return false;
	}
	
	//Actions
	private void assignCustomer(BankCustomer bc, MyTeller mt){
		Do("Assigning a teller");
		bc.msgHereIsTeller(mt.bt);
		mt.s = state.withCustomer;
		waitingCustomers.remove(bc);
	}
	
	//Utilities
	public void addTeller(BankTeller bt){
		tellers.add(new MyTeller(bt));
	}
	
	public String toString(){
		return name;
	}
	
	public class MyTeller{
		public BankTeller bt;
		public state s;
		MyTeller(BankTeller bt){
			this.bt = bt;
			s = state.working;
		}
	}
	public enum state {working, offWork, withCustomer} 
}
