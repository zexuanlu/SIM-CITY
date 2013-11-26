package restaurant.shareddata;

import restaurant.interfaces.Waiter;

public class Order {

	public Waiter w;
	public String choice;
	public int table;	
	public enum state 
	{ pending, cooking, cooked, readytotake};

	public state s = state.pending;

	public Order(Waiter w, String choice, int table){
		this.w = w;
		this.choice = choice;
		this.table = table;
	}
}
