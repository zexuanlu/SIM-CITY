package simcity.test.Mock;
import java.util.List;

import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 
import simcity.interfaces.BusStop; 

public class MockBusRole extends Mock implements Bus{
	public MockBusRole(String name){
		super(name);
	}
	public EventLog log = new EventLog(); 
	public Passenger passenger; 
	public BusStop busstop; 
	
	public List<Passenger> passengerlist; 
	
	public void msgCanIComeOnBus(Passenger p){}
	
	public void msgHeresMyFare(Passenger p, double Paid){}
	
	public void msgAtStop(String stop){}
	
	public void msgLeaving(Passenger p){}
	
	public void msgHereisList(List<Passenger> sentpassengers){
		passengerlist = sentpassengers; 
	}
	
	public boolean passengerinList(Passenger p){
		for (Passenger mp: passengerlist){
			if (mp == p){
				return true; 
			}
		}
		return false; 
	}
}
