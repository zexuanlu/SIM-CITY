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
	
	public void msgatBusStop(Passenger p){
		log.add(new LoggedEvent("Passenger at Stop"));
	}
	
	public boolean isBusAtStop(Bus b){
		return true; //temporary hack
	}

	public void msgBusLeaving(Bus b){
		log.add(new LoggedEvent("Bus Leaving"));
		
	}
	public void msgatBusStop(Bus b){
		log.add(new LoggedEvent("Bus at Stop"));
	}
	
}
