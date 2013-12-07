package restaurant5.test.mock;
import restaurant5.interfaces.Waiter5;
import restaurant5.CashierAgent5; 
import restaurant5.Check5;
import restaurant5.CustomerAgent5;

public class MockWaiter5 extends Mock5 implements Waiter5 {
	public Check5 c; 
	public EventLog5 log = new EventLog5(); 
	public CashierAgent5 cashier; 
	public MockWaiter5(String name) {
		super(name);
	}
	
	public void msgHereisCheck(Check5 check){
		log.add(new LoggedEvent5("Received HereIsYourTotal from cashier. Total = "+ check.price));
		c = check; 
		//System.out.println("hit");
		//System.out.println(log.size());
	}
	
	public void msgDoneEating(CustomerAgent5 c){}
	public void msgseatCustomer(CustomerAgent5 cust, int table){}
	public void msgoffBreak(){}
	public void msgWanttoGoonBreak(){};
	public void msggoOnBreak(){};
	public void msgcantgoOnBreak(){};
	public void msgOutof(String choice, int table){};
	public void msgReadytoOrder(CustomerAgent5 cust){};
	public void msghereisChoice(CustomerAgent5 c, String choice){};
	public void msgorderDone(String choice, int table){};
	public void msgDoneandLeaving(CustomerAgent5 c){};
	public String getName(){return "hi";}; 
	public int getPlace(CustomerAgent5 c){return 0;}; 
}