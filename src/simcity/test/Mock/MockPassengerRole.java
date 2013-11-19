package simcity.test.Mock;
import simcity.interfaces.BusStop;
import simcity.interfaces.Passenger; 
import simcity.interfaces.Bus; 

public class MockPassengerRole extends Mock implements Passenger {
	
	public EventLog log = new EventLog(); 
	public Bus bus; 
	public BusStop busstop; 
	
	public MockPassengerRole(String name){
		super(name);
	}
	
	public void msgHereIsPrice(Bus b, double fare){}

	public void msgComeOnBus(Bus b){}

	public void msgNowAtStop(String stop){}
	
	public void gotoBus(){}
}
