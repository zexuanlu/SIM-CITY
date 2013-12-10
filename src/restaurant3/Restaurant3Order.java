package restaurant3;

import restaurant3.interfaces.Restaurant3Waiter;

public class Restaurant3Order {

	String choice;
	Restaurant3Waiter wtr;
	int tableNum;	
	public enum oState {pending, cooking, cooked};
	public oState state;


	public Restaurant3Order(Restaurant3Waiter w, String choice, int table){
		this.wtr = w;
		this.choice = choice;
		this.tableNum = table;
		state = oState.pending;
	}
}
