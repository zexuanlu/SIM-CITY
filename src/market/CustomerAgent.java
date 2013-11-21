package market;

import agent.Agent;
import agent.Role;
import gui.CustomerGui;

import java.util.*;
import java.util.concurrent.Semaphore;

import marketinterface.Cashier;
import marketinterface.Customer;

public class CustomerAgent extends Agent implements Customer{

	Cashier cashier;
	CustomerGui customerGui;
	public List<Food> food = new ArrayList<Food>();
	public List<Food> Receivedfood = new ArrayList<Food>();
	double bill = 0;
	public double money = 30;
	
	private Semaphore atTable = new Semaphore(0,true);


	public enum state{none, ordering, ordered, paying, payed, GetChange, GoWaiting, GoToTable, attable, collecting, collected}
	public state s = state.none;
	
	public void setCashier(Cashier cashier){
		this.cashier = cashier;
	}

	public void addFood(String name, int amount){
		food.add(new Food(name, amount));
	}
	
	public void setGui(CustomerGui gui){
		this.customerGui = gui;
	}
	
	public void msgHello(){
		s = state.ordering;
		stateChanged();
	}

	public void msgPleasePay(int b){
		bill = b;
		s= state.paying;
		stateChanged();
	}

	public void msgHereisYourChange(double change){
		money = change;
		s = state.GetChange;
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
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Do("Yooooo");
		cashier.msgHereisOrder(this,  food);
		s = state.ordered;
	}

	void Dopay(){
		cashier.msgPayment(this, money);
		s = state.payed;
	}

	void Wait(){
		customerGui.DoGoToWaitingArea(0);/////!!!!!!!!!!!!!!!!!!!
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
	}


}
