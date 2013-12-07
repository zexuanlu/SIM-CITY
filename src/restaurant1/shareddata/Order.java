package restaurant1.shareddata;

import restaurant1.interfaces.Restaurant1Waiter;

public class Order {

	public Restaurant1Waiter w;
	public String choice;
	public int table;	
	public enum state 
	{ pending, cooking, cooked, readytotake};

	public state s = state.pending;

	public Order(Restaurant1Waiter w, String choice, int table){
		this.w = w;
		this.choice = choice;
		this.table = table;
	}
}
