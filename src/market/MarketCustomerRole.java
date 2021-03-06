package market;

import agent.*;
import person.interfaces.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.gui.MarketCustomerGui;
import market.interfaces.*;

public class MarketCustomerRole extends Role implements MarketCustomer{

	MarketCashier cashier;
	public MarketCustomerGui customerGui;
	public List<Food> food = new ArrayList<Food>();
	public List<Food> Receivedfood = new ArrayList<Food>();
	double bill = 0;
	int seatNumber;
	public double money = 30;
	Person p;
	
	private Semaphore leaving = new Semaphore(0, true);
	
	public MarketCustomerRole(Person person, String name){
		super(person);
		roleName = "Market Customer";
		this.p = person;
	}
	
	private Semaphore atTable = new Semaphore(0,true);


	public enum state{none, ordering, ordered, paying, payed, GetChange, GoWaiting, GoToTable, attable, collecting, collected}
	public state s = state.none;
	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}

	public void addFood(String name, int amount){
		food.add(new Food(name, amount));
	}
    

	public void setGui(MarketCustomerGui gui){
		this.customerGui = gui;
	}
	
	public void msgHello(double m, List<Food> f){

		food = f;
		money = m;
		s = state.ordering;
		stateChanged();
	}

	public void msgPleasePay(int b){
		bill = b;
		s= state.paying;
		stateChanged();
	}

	public void msgHereisYourChange(double change, int num){
		money = change;
		s = state.GetChange;
		seatNumber = num;
		stateChanged();
	}

	public void msgYourFoodReady(){
		s = state.GoToTable;
		stateChanged();
	}

	public void msgHereisYourOrder(List<Food> order){ //List food
		s = state.collecting;
		Receivedfood = order;
		stateChanged();
	}

	public void msgAtTable(){
		atTable.release();
	}
	
	public void msgDoneLeaving() {
		leaving.release();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		if (s == state.ordering){
			GiveOrder();
			return true;
		}
		if (s == state.paying){
			Dopay();
			return true;
		}
		if (s == state.GetChange){
			Wait();
			return true;
		}
		if (s == state.GoToTable){
			GoToTable();
			return true;
		}
		if (s == state.collecting){
			CollectandLeave();
			return true;
		}

		return false;
	}

	void GiveOrder(){
		customerGui.DoGoToTable();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cashier.msgHereisOrder(this,  food);
		s = state.ordered;
	}

	void Dopay(){
		cashier.msgPayment(this, money);
		s = state.payed;
	}

	void Wait(){
		customerGui.DoGoToWaitingArea(seatNumber);
		s = state.GoWaiting;
	}

	void GoToTable(){
		s= state.attable;
		customerGui.DoGoToTable();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cashier.msgGoToTable(this);
	}

	void CollectandLeave(){
		s = state.collected;
		customerGui.DoLeave();
		leaving.drainPermits();
		try {
			leaving.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        customerGui.setPresent(false);
		p.msgFinishedEvent(this, Receivedfood, money );
	}

	public String getRoleName(){
		return roleName;
	}
	
	public utilities.Gui getGui(){
		return customerGui; 
	}
	
}
