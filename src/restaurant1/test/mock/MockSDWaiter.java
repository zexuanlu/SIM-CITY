package restaurant1.test.mock;

import restaurant1.Restaurant1CookRole;
import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Waiter;
import restaurant1.test.mock.EventLog;

public class MockSDWaiter extends Mock implements Restaurant1Waiter {

	public EventLog log = new EventLog();
	
	public MockSDWaiter() {
		super("MockSDWaiter");
	}

	public void msgIWantFood(Restaurant1Customer cust, int table, int loc){
		
	}

	public void msgreadytoorder(Restaurant1Customer customer){
		log.add(new LoggedEvent("Customer " + customer.toString() + " is ready to order"));
	}
	
	public void msgAnimationDoneAtTable(Restaurant1Customer customer){
		
	}
	
	public void msgorderisready(Restaurant1Customer customer, String choice, int table){
		log.add(new LoggedEvent("Order for customer " + customer + " is ready"));
	}
	
	public void msgorderiscooked(int table){
		log.add(new LoggedEvent("Order for table " + table + " is cooked"));
	}

	@Override
	public void msgatCook() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgordertotable(Restaurant1Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeavingTable(Restaurant1Customer c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIsback() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfFood(int table) {
		// TODO Auto-generated method stub
		
	}



	@Override
	public void msgHereistheCheck(Restaurant1Customer c, double p) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgGoOffWork() {
		// TODO Auto-generated method stub
		
	}
}