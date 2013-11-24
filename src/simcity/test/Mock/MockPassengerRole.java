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
	
	public void msgHereIsPrice(Bus b, double fare){
		log.add(new LoggedEvent("Bus has sent fare of "+ fare));
	}

	public void msgComeOnBus(Bus b){
		log.add(new LoggedEvent("Welcomed onboard"));
	}

	public void msgNowAtStop(String stop){
		log.add(new LoggedEvent("At stop " + stop));
	}
	
	public void gotoBus(){}
}
