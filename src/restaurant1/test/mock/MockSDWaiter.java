package restaurant1.test.mock;

import restaurant1.Restaurant1CookRole;
import restaurant1.interfaces.Customer;
import restaurant1.interfaces.Waiter;
import restaurant1.test.mock.EventLog;

public class MockSDWaiter extends Mock implements Waiter {

	public EventLog log = new EventLog();
	
	public MockSDWaiter() {
		super("MockSDWaiter");
	}

	public void msgIWantFood(Customer cust, int table, int loc){
		
	}

	public void msgreadytoorder(Customer customer){
		log.add(new LoggedEvent("Customer " + customer.toString() + " is ready to order"));
	}
	
	public void msgAnimationDoneAtTable(Customer customer){
		
	}
	
	public void msgorderisready(Customer customer, String choice, int table){
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
	public void msgordertotable(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgLeavingTable(Customer c) {
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
	public void IwantBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereistheCheck(Customer c, double p) {
		// TODO Auto-generated method stub
		
	}
}