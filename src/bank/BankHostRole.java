package bank;

import agent.*;
import bank.gui.BankHostGui;
import bank.interfaces.*;
import person.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import bank.test.mock.*;

/**
 * This class is the Bank Host. It handles customers coming and assigns them to
 * tellers.
 * @author Joseph
 *
 */
public class BankHostRole extends Role implements BankHost {


	//Data
	String name;
	public EventLog log;
	public List<BankCustomer> waitingCustomers;
	public List<MyTeller> tellers;
	public BankHostGui gui = null;
	public boolean atDesk;
	Semaphore movement = new Semaphore(0,true);
	public BankHostRole(Person person, String name){
		super(person);
		this.name = name;
		waitingCustomers = new ArrayList<BankCustomer>();
		tellers = new ArrayList<MyTeller>();
		log = new EventLog();
		atDesk = false;
	}
	
	//Messages
	public void msgAtDestination(){
		movement.release();
		stateChanged();
	}
	
	public void msgINeedTeller(BankCustomer bc) {
		log.add(new LoggedEvent("Received msgINeedTeller from Bank Customer"));
		waitingCustomers.add(bc);
		stateChanged();
	}

	public void msgBackToWork(BankTeller bt){
		log.add(new LoggedEvent("Received msgBackToWork from Bank Teller"));
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
		if(!atDesk){
			goToLocation("Host");
			atDesk = true;
			return true;
		}
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
		bc.msgHereIsTeller(mt.bt, mt.location);
		mt.s = state.withCustomer;
		waitingCustomers.remove(bc);
	}
	
	private void goToLocation(String location){
		if(gui != null){
			gui.DoGoToLocation(location);
			Do("Moving to " + location);
			try{
				movement.acquire();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	//Utilities
	public void addTeller(BankTeller bt){
		String location = "Teller" + (tellers.size()+1);
		tellers.add(new MyTeller(bt, location));
		bt.msgNewDestination(location);
	}
	
	public String toString(){
		return name;
	}
	
	public class MyTeller{
		public BankTeller bt;
		public state s;
		public String location;
		MyTeller(BankTeller bt, String location){
			this.bt = bt;
			this.location = location;
			s = state.working;
		}
	}
	public enum state {working, offWork, withCustomer} 
}
