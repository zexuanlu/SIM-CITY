package restaurant2.interfaces;

import restaurant2.Check;
import restaurant2.Table;

public interface Restaurant2Waiter {
	
	public abstract void msgBackHome();
	
	public abstract void msgLeavingTable(Restaurant2Customer cust);
	
	public abstract void msgLeavingTableNoOrder(Restaurant2Customer cust);
	
	public abstract void msgAtTable();
	
	public abstract void msgGoOffWork();
	
	public abstract void msgAtCook();
	
	public abstract void msgReadyToOrder(Restaurant2Customer customer, int table);
	
	public abstract void msgOrder(Restaurant2Customer customer, String order);
	
	public abstract void msgFoodReady(String order, Restaurant2Customer customer);
	
	public abstract void msgOutOfFood(String foodType, Restaurant2Customer customer);
	
	public abstract void msgSeatCustomer(Restaurant2Customer customer, Table table);
	
	public abstract void msgGoOnBreak();
	
	public abstract void msgHeresTheCheck(Restaurant2Customer c, Check check);
	
	public abstract void returnFromBreak();

}
