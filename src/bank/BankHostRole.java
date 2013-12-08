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
 * 
 * @author Joseph Boman
 */
public class BankHostRole extends Role implements BankHost {

	//Data
	String name;
	public EventLog log;//Used for testing
	public List<BankCustomer> waitingCustomers;//A list of customers who are waiting
	private int waitCount = 0;
	public List<MyTeller> tellers;//A list of all the tellers in the bank
	public BankHostGui gui = null;//Used for animation
	public boolean atDesk;//Used for animation
	public boolean endOfDay = false;
	Semaphore movement = new Semaphore(0,true);//Used for animation
	/**
	 * The constructor for Bank Host
	 * 
	 * @param person the person who is the bank host
	 * @param name the name of the bank host
	 */
	public BankHostRole(Person person, String name){
		super(person);
		roleName = "Bank Host";
		this.name = name;
		waitingCustomers = new ArrayList<BankCustomer>();
		tellers = new ArrayList<MyTeller>();
		log = new EventLog();
		atDesk = false;
	}
	
	//Messages
	/**
	 * Received from the gui when it arrives at a destination
	 */
	public void msgAtDestination(){
		movement.release();
		stateChanged();
	}
	
	/**
	 * Received from a bank customer when they need a teller
	 * 
	 * @param bc the bank customer who needs a teller
	 */
	public void msgINeedTeller(BankCustomer bc) {
		log.add(new LoggedEvent("Received msgINeedTeller from Bank Customer"));
		waitingCustomers.add(bc);
		waitCount++;
		stateChanged();
	}

	public void msgEndOfDay(){
		log.add(new LoggedEvent("Received msgEndOfDay from Time Card"));
		endOfDay = true;
		stateChanged();
	}
	/**
	 * Received from the bank teller when they finish with a customer
	 * 
	 * @param bt the bank teller who is going back to work
	 */
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
	/**
	 * The scheduler for the bank host
	 * 
	 * @return true if it picks an action
	 * @return false if it doesn't pick an action
	 */
	public boolean pickAndExecuteAnAction() {
		//If the host isn't at his desk
		if(!atDesk){
			goToLocation("Host");
			atDesk = true;
			return true;
		}
		//If there is a waiting customer and an open teller
		if(!waitingCustomers.isEmpty()){
			for(MyTeller mt : tellers){
				if(mt.s == state.working){
					assignCustomer(waitingCustomers.get(0), mt);
					return true;
				}
			}
			//Used to track people arriving and tell them
			if(waitCount > 0){
				waitHere();
				waitCount--;
				return false;
			}
		}
		//If it's the end of the day and you're another day older
		if(waitingCustomers.isEmpty() && endOfDay){
			workDayOver();
			return true;
		}
		//If there are no actions
		return false;
	}
	
	//Actions
	/**
	 * Sends a message to a bank customer assigning him a teller
	 * 
	 * @param bc the bank customer who is being assigned
	 * @param mt the teller who is being assigned
	 */
	private void assignCustomer(BankCustomer bc, MyTeller mt){
		Do("Assigning a teller");
		bc.msgHereIsTeller(mt.bt, mt.location);
		mt.s = state.withCustomer;
		waitingCustomers.remove(bc);
		if(waitingCustomers.size() != 0)
			waitHere();
	}
	
	/**
	 * Sends messages to all the tellers telling them to leave
	 */
	private void workDayOver(){
		for(MyTeller mt : tellers){
			mt.bt.msgWorkDayOver();
		}
		endOfDay = false;
		goToLocation("Outside");
		gui.setPresent(false);
		getPerson().msgGoOffWork(this, 0.00);
		atDesk = false;
	}
	
	/**
	 * Sends a message to the customers telling them where to go.
	 */
	private void waitHere(){
		for(int i = 0; i < waitingCustomers.size(); i++){
			waitingCustomers.get(i).msgNewLocation("waitArea"+i);
		}
	}
	
	/**
	 * Used for animation. Sends the gui a location to go to.
	 * 
	 * @param location the location to go to
	 */
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
	/**
	 * Adds a teller to the list of tellers and gives them their location
	 * 
	 * @param bt the bank teller being added
	 */
	public void addTeller(BankTeller bt){
		String location = "Teller" + (tellers.size()+1);
		tellers.add(new MyTeller(bt, location));
		bt.msgNewDestination(location);
	}
	
	/**
	 * Returns the name of the bank host
	 */
	public String toString(){
		return name;
	}
	
	//Grant's addition
	public void setGui(BankHostGui bhg){
		this.gui = bhg;
	}
	
	/**
	 * The MyTeller class. Contains a bank teller, 
	 * a state, and a location
	 * 
	 * @author Joseph Boman
	 *
	 */
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
	
	public String getRoleName(){
		return roleName;
	}
}
