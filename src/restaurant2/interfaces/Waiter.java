package restaurant2.interfaces;

import restaurant2.Check;
import restaurant2.Table;

public interface Waiter {
	
	public abstract void msgBackHome();
	
	public abstract void msgLeavingTable(Customer cust);
	
	public abstract void msgLeavingTableNoOrder(Customer cust);
	
	public abstract void msgAtTable();
	
	public abstract void msgAtCook();
	
	public abstract void msgReadyToOrder(Customer customer, int table);
	
	public abstract void msgOrder(Customer customer, String order);
	
	public abstract void msgFoodReady(String order, Customer customer);
	
	public abstract void msgOutOfFood(String foodType, Customer customer);
	
	public abstract void msgSeatCustomer(Customer customer, Table table);
	
	public abstract void msgGoOnBreak();
	
	public abstract void msgHeresTheCheck(Customer c, Check check);
	
	public abstract void returnFromBreak();

}
