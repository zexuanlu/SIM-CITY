package bank;

import agent.Agent;
import bank.interfaces.*;
import java.util.*;

public class BankHostRole extends Agent implements BankHost {

	public BankHostRole(String name){
		this.name = name;
		waitingCustomers = new ArrayList<BankCustomer>();
		tellers = new ArrayList<MyTeller>();
	}
	//Data
	String name;
	List<BankCustomer> waitingCustomers;
	public List<MyTeller> tellers;
	
	//Messages
	public void msgINeedTeller(BankCustomer bc) {
		waitingCustomers.add(bc);
		stateChanged();
	}

	public void msgBackToWork(BankTeller bt){
		for(MyTeller mt : tellers){
			if(mt.bt == bt){
				mt.s = state.working;
				stateChanged();
				return;
			}
		}
	}
	//Scheduler
	protected boolean pickAndExecuteAnAction() {
		if(!waitingCustomers.isEmpty()){
			for(MyTeller mt : tellers){
				if(mt.s == state.working){
					assignCustomer(waitingCustomers.get(0), mt);
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
	
	class MyTeller{
		BankTeller bt;
		state s;
		MyTeller(BankTeller bt){
			this.bt = bt;
			s = state.working;
		}
	}
	enum state {working, offWork, withCustomer} 
}
