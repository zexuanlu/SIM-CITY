package restaurant5.test.mock;

import restaurant5.interfaces.Market5; 
import restaurant5.Restaurant5Cashier; 

public class MockMarket5 extends Mock5 implements Market5 {

	public EventLog5 log = new EventLog5(); 
	public Restaurant5Cashier cashier; 
	public int receivedmoney; 
	
	public MockMarket5(String name) {
		super(name);
	}
	public void msghereispayment(int original, int paid){
		log.add(new LoggedEvent5("Received Payment from Cashier"));
		receivedmoney = paid; 
		if (paid < original){
			log.add(new LoggedEvent5("Cashier Flaked!"));
		}
	}
}