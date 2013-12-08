package restaurant4.shareddata;

import restaurant4.Restaurant4AbstractWaiter;

public class Order {

	public Restaurant4AbstractWaiter w;
	public String choice;
	public int table;	
	public enum state 
	{ pending, cooking, cooked, readytotake};

	public state s = state.pending;

	public Order(Restaurant4AbstractWaiter w, String choice, int table){
		this.w = w;
		this.choice = choice;
		this.table = table;
	}
}
