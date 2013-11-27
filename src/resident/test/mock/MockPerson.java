package resident.test.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import market.Food;
import agent.Role;
import person.Event;
import person.Location;
import person.Position;
import person.SimEvent;
import person.interfaces.Person;
import simcity.CityMap;

public class MockPerson extends Mock implements Person {
	
	public EventLog log = new EventLog();
	public int hunger;
	public int currentTime;
	public Wallet wallet;
	public List<String> groceryList = new ArrayList<String>();
	public PriorityQueue<Event> toDo = new PriorityQueue<Event>();
	public List<Role> roles = new ArrayList<Role>();
	
	public MockPerson(String name) {
		super(name);
		currentTime = 7;
		hunger = 4;
	}
	public MockPerson(String name, String TypeOfScenario){ 
		super(name);
		currentTime = 7;//wakes up at 7am
		hunger = 4;
	}
	public void addRole(Role r){
		roles.add(r);
	}
	public void setHunger(int hunger){
		this.hunger = hunger;
	}
	public void setTime(int curr){
		currentTime = curr;
	}
	@Override
	public void msgNewHour(int hour) { //usually from sim world clock
		
		log.add(new LoggedEvent("the hour has updated"));
	}

	@Override
	public void msgAtDest(Position destination) { //from gui
		
		log.add(new LoggedEvent("you have arrived at your gui destination"));
	}

	public void msgFinishedEvent(Role r) { //default message to notify person that he can return to his duties
		
		log.add(new LoggedEvent("You've finished your role and have returned to your PersonAgent"));
		
	}
	public void msgReadyToWork(Role r) { //notify a person it can now send it's role to work (time card)
		
		
	}
	public void msgAddGroceries(List<String> gro){
		log.add(new LoggedEvent("The personagent will grab : "+gro.toString()+" next time it goes out"));
		for(String s : gro){
			groceryList.add(s);
		}
	}
	public class Wallet {
		
		private int onHand;
		private int inBank;
		private int balance; 

		Wallet(int oh, int ib){
			this.onHand = oh;
			this.inBank = ib;
			this.balance = oh + ib;
		}
		public int getOnHand(){
			return onHand;
		}
		public int getInBank(){
			return inBank;
		}
		public int getBalance(){
			return balance;
		}
		public void setOnHand(int newAmount){
			onHand += newAmount;
		}
		public void setInBank(int newAmount){
			inBank += newAmount;
		}
	}
	
	public void msgAtDest(int x, int y) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgFinishedEvent(Role r, List<Food> foodList, double change) {
		log.add(new LoggedEvent("You've finished your role and have returned to your PersonAgent"));
	}
	
	public void msgAddMoney(double money) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgNewBalance(double money) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgAddEvent(SimEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	public void msgGoOffWork(Role r, double pay) {
		// TODO Auto-generated method stub
		
	}
	
	public void setStateChanged() {
		// TODO Auto-generated method stub
		
	}
	
	public int getTime() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public CityMap getMap() {
		// TODO Auto-generated method stub
		return null;
	}

}
