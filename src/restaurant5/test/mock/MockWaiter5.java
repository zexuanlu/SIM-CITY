package restaurant5.test.mock;
import restaurant5.interfaces.Waiter5;
import restaurant5.Restaurant5Cashier; 
import restaurant5.Check5;
import restaurant5.Restaurant5CustomerAgent;

public class MockWaiter5 extends Mock5 implements Waiter5 {
	public Check5 c; 
	public EventLog5 log = new EventLog5(); 
	public Restaurant5Cashier cashier; 
	public MockWaiter5(String name) {
		super(name);
	}
	
	public void msgHereisCheck(Check5 check){
		log.add(new LoggedEvent5("Received HereIsYourTotal from cashier. Total = "+ check.price));
		c = check; 
		//System.out.println("hit");
		//System.out.println(log.size());
	}
	
	public void msgDoneEating(Restaurant5CustomerAgent c){}
	public void msgseatCustomer(Restaurant5CustomerAgent cust, int table){}
	public void msgoffBreak(){}
	public void msgWanttoGoonBreak(){};
	public void msggoOnBreak(){};
	public void msgcantgoOnBreak(){};
	public void msgOutof(String choice, int table){};
	public void msgReadytoOrder(Restaurant5CustomerAgent cust){};
	public void msghereisChoice(Restaurant5CustomerAgent c, String choice){};
	public void msgorderDone(String choice, int table){};
	public void msgDoneandLeaving(Restaurant5CustomerAgent c){};
	public String getName(){return "hi";}; 
	public int getPlace(Restaurant5CustomerAgent c){return 0;}; 
}