package restaurant3;

import agent.Role;
import person.interfaces.Person;
import restaurant3.interfaces.Restaurant3Customer;

public abstract class Restaurant3AbstractWaiter extends Role{

	
	//Enums to keep track of customer state
	public enum cState {idle, beingSeated, seated, orderPlaced, 
		orderDelivered, awaitingBill, billDelivered, awaitingReceipt, receiptDelivered, leaving};
			
	public enum wEvent {none, custNeedSeat, custReadyToOrder, custDecided, custOrderReady,
		custDoneEating, custBillReady, custPaid, custReceiptReady, custLeaving};
		
	//CONSTRUCTOR *****************************
	public Restaurant3AbstractWaiter(Person pa) {
		super(pa);
	}

	
	//Private class to keep track of customers
	public static class MyCustomer {
		Restaurant3Customer cust;
		int tableNum;
		cState state;
		wEvent event;
		String choice;
		double bill = 0;
		double payment = 0;
		double change = 0;
			
		MyCustomer(Restaurant3Customer c, int tNum){
			cust = c;
			tableNum = tNum;
			state = cState.idle;
			event = wEvent.custNeedSeat;
		}
	}
}
