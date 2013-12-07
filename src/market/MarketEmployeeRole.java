package market;

import agent.*;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.interfaces.*;
import restaurant1.Restaurant1CookRole;
import utilities.restaurant.RestaurantCook;
import market.gui.MarketEmployeeGui;
import market.interfaces.*;

public class MarketEmployeeRole extends Role implements MarketEmployee{

	MarketCashier cashier;
	public MarketEmployeeGui employeeGui;
	public List<Mycustomer> mycustomer = new ArrayList<Mycustomer>();
	public List<Myrest> myrest = new ArrayList<Myrest>();
	public Map<Integer, RestDes> CityMap = new HashMap<Integer, RestDes>();
	private boolean endOfDay = false;
	private Semaphore atTable = new Semaphore(0,true);

	public MarketEmployeeRole(Person person, String name){
		super(person);
		roleName = "Market Employee";
		CityMap.put(1, new RestDes(100, 120));
		
	}
	
	public class RestDes{
		int x;
		int y;
		
		RestDes(int x, int y){
			this.x = x;
			this.y =y;
		}
	}
	
	public class Mycustomer{
		MarketCustomer c;
		List<Food> order;
		public state s = state.collecting;
		int listSize;
		Mycustomer(MarketCustomer c, List<Food> order){
			this.c = c;
			this.order = order;
		}
	}

	public class Myrest{
		RestaurantCook cook;
		MarketTruck truck;
		List<Food> order;
		public state1 s1 = state1.collecting;
		int restNum;
		Myrest(RestaurantCook cook, List<Food> order, MarketTruck truck, int number){
			this.cook = cook;
			this.order = order;
			this.truck = truck;
			this.restNum = number;
		}
	}

	public enum state{ collecting, collected};
	public enum state1{collecting, sending};

	public void setCashier(MarketCashier cashier){
		this.cashier =  cashier;
	}

//	public void addTruck(Truck t){
//		truck.add(t);
//	}
	
	public void setGui(MarketEmployeeGui gui){
		this.employeeGui = gui;
	}
	
	public void msgWorkDayOver(){
		endOfDay = true;
		stateChanged();
	}
	
	public void msgCollectOrer(MarketCustomer customer, List<Food> food){
		mycustomer.add(new Mycustomer(customer, food));
		stateChanged();
	}

	public void msgCollectTheDilivery(RestaurantCook cook, List<Food> food, MarketTruck truck, int number){
		myrest.add(new Myrest(cook, food, truck, number));
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
		
		if(endOfDay && myrest.isEmpty() && mycustomer.isEmpty()){
			goOffWork();
			return true;
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
		mycustomer.remove(customer);
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
		rest.truck.gotoPosition(rest.cook, rest.order, CityMap.get(rest.restNum).x, CityMap.get(rest.restNum).y);
		myrest.remove(rest);
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
	
	void goOffWork(){
		endOfDay = false;
		getPerson().msgGoOffWork(this, 500.00);
	}

	public String getRoleName(){
		return roleName;
	}
}
