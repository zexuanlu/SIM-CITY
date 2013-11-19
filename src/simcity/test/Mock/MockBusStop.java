package simcity.test.Mock;


import simcity.interfaces.Bus;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Passenger;

public class MockBusStop extends Mock implements BusStop {
	
	public MockBusStop(String name){
		super(name);
	}
	
	public EventLog log = new EventLog(); 
	public Passenger passenger; 
	public Bus bus; 
	
	public void msgatBusStop(Passenger p){}
	
	public boolean isBusAtStop(Bus b){
		return true;
	}

	public void msgBusLeaving(Bus b){}
	public void msgatBusStop(Bus b){}
	
}
