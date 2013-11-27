package simcity.test.Mock;


import java.awt.Dimension;

import simcity.interfaces.Bus;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Passenger;

public class MockBusStop extends Mock implements BusStop {
	
	public MockBusStop(String name){
		super(name);
	}
	
	public boolean busstop; 
	public EventLog log = new EventLog(); 
	public Passenger passenger; 
	public Bus bus; 
	
	public String getDirection(){
		return "Temp"; 
	}
	
	public void msgatBusStop(Passenger p){
		log.add(new LoggedEvent("Passenger at Stop"));
	}
	
	public boolean isBusAtStop(Bus b){
		return busstop; //temporary hack
	}

	public void msgBusLeaving(Bus b){
		log.add(new LoggedEvent("Bus Leaving"));
		
	}
	public void msgatBusStop(Bus b){
		log.add(new LoggedEvent("Bus at Stop"));
	}
	
	
	public Dimension getDim(){
		return new Dimension(0,0);
	}
	
}
