package person.test.mock;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import market.Food;
import agent.Role;
import person.SimEvent;
import person.Location;
import person.Position;
import person.interfaces.Person;

public class PersonMock extends Mock implements Person {
	
	public EventLog log = new EventLog();
	public int hunger;
	public int currentTime;
	public Wallet wallet;
	public List<String> groceryList = new ArrayList<String>();
	public PriorityQueue<SimEvent> toDo = new PriorityQueue<SimEvent>();
	public List<Role> roles = new ArrayList<Role>();
	
	public PersonMock(String name) {
		super(name);
		currentTime = 7;
		hunger = 4;
		wallet = new Wallet(100, 100);
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

	@Override
	public void msgFinishedEvent(Role r) { //default message to notify person that he can return to his duties
		
		log.add(new LoggedEvent("Youve finished your role and have returned to your PersonAgent"));
		
	}
	@Override
	public void msgAddMoney(double money) { //add money back to the person's wallet
		log.add(new LoggedEvent("Cash has been added to your personagent's wallet to the tune of "+money+" dollars"));
		wallet.setOnHand(money);
	}	
	@Override
	public void msgAddEvent(SimEvent e) { //add an event to the person's scheduler
		log.add(new LoggedEvent("An event has been added to the personagent's queue"));
		
	}
	@Override
	public void msgReadyToWork(Role r) { //notify a person it can now send it's role to work (time card)
		
		
	}
	@Override
	public void msgGoOffWork(Role r, double pay) {
		// TODO Auto-generated method stub
		
	}
	public void msgGoOffWork(Role r) { //notify a person he is off work (time card)
		// TODO Auto-generated method stub
		
	}
	public void msgAddGroceries(List<String> gro){
		log.add(new LoggedEvent("The personagent will grab : "+gro.toString()+" next time it goes out"));
		for(String s : gro){
			groceryList.add(s);
		}
	}
	public class Wallet {
		
		private double onHand;
		private double inBank;
		private double balance; 

		Wallet(double oh, double ib){
			this.onHand = oh;
			this.inBank = ib;
			this.balance = oh + ib;
		}
		public double getOnHand(){
			return onHand;
		}
		public double getInBank(){
			return inBank;
		}
		public double getBalance(){
			return balance;
		}
		public void setOnHand(double newAmount){
			onHand += newAmount;
		}
		public void setInBank(double newAmount){
			inBank += newAmount;
		}
	}
	@Override
	public void msgFinishedEvent(Role r, List<Food> foodList, double change) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgNewBalance(double money) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setStateChanged() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAtDest(int x, int y) {
		// TODO Auto-generated method stub
		
	}

}
