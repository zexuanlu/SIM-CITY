package restaurant;

import restaurant.interfaces.Customer;
import agent.Agent;

public abstract class Restaurant1AbstractWaiter extends Agent {

	public enum state 
	{available, waiting, seated, readytoorder, askedtoorder, attable, ordered, gotocook, orderready,outoffood, eating,atcook, checkingbill, bringbill, bringattable, starteating, done};
	
	
	public Restaurant1AbstractWaiter() {
		// TODO Auto-generated constructor stub
	}
	
	public static class mycustomer {
		Customer c;
		int table;
		int location;
		String choice;
		double price;
		
		state s = state.waiting;
		
		mycustomer(Customer c, int table, int location){
			this.location = location;
			this.c = c;
			this.table = table;
		}
	}
	
	

}
