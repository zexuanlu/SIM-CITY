package market;

import agent.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import market.gui.EmployeeGui;
import market.interfaces.Cashier;
import market.interfaces.Cook;
import market.interfaces.Customer;
import market.interfaces.Employee;
import market.interfaces.Truck;

public class EmployeeAgent extends Role implements Employee{

	Cashier cashier;
	EmployeeGui employeeGui;
	public List<Mycustomer> mycustomer = new ArrayList<Mycustomer>();
	public List<Myrest> myrest = new ArrayList<Myrest>();
	//public List<Truck> truck = new ArrayList<Truck>();

	private Semaphore atTable = new Semaphore(0,true);

	public EmployeeAgent(){
		
	}
	
	public class Mycustomer{
		Customer c;
		List<Food> order;
		public state s = state.collecting;
		int listSize;
		Mycustomer(Customer c, List<Food> order){
			this.c = c;
			this.order = order;
		}
	}

	public class Myrest{
		Cook cook;
		Truck truck;
		List<Food> order;
		public state1 s1 = state1.collecting;
		int listSize;
		Myrest(Cook cook, List<Food> order, Truck truck){
			this.cook = cook;
			this.order = order;
			this.truck = truck;
		}
	}

	public enum state{ collecting, collected};
	public enum state1{collecting, sending};

	public void setCashier(Cashier cashier){
		this.cashier =  cashier;
	}

//	public void addTruck(Truck t){
//		truck.add(t);
//	}
	
	public void setGui(EmployeeGui gui){
		this.employeeGui = gui;
	}
	
	public void msgCollectOrer(Customer customer, List<Food> food){
		mycustomer.add(new Mycustomer(customer, food));
		stateChanged();
	}

	public void msgCollectTheDilivery(Cook cook, List<Food> food, Truck truck){
		myrest.add(new Myrest(cook, food, truck));
		stateChanged();
	}
	
	public void msgAtTable(){
		atTable.release();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub

		for(Mycustomer customer: mycustomer){
			if(customer.s == state.collecting){
				CollectFood(customer);
				return true;
			}
		}

		for(Myrest rest: myrest){
			if(rest.s1 == state1.collecting){
				CollectDilivery(rest);
				return true;
			}
		}

		return false;
	}

	void CollectFood(Mycustomer customer){
		customer.s = state.collected;
		DoCollectFood(customer.order);
		employeeGui.DoSendToCashier();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cashier.msgHereisProduct(customer.c, customer.order);
	}

	void CollectDilivery(Myrest rest){
		rest.s1 = state1.sending;
		DoCollectFood(rest.order);
		employeeGui.DoSendToTruck();
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		rest.truck.msgPleaseDiliver(rest.cook, rest.order);
	}

	void DoCollectFood(List<Food> order){
		for(int i = 0; i < order.size(); i++){
			employeeGui.DoCollectFood(order.get(i).choice);
			try {
				atTable.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
