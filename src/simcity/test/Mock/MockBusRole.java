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
	
	public void msgCanIComeOnBus(Passenger p){
		log.add(new LoggedEvent("Can I come on Bus"));
		
	}
	
	public void msgHeresMyFare(Passenger p, double Paid){
		log.add(new LoggedEvent("Passenger paid amount of "+ Paid));
	}
	
	public void msgAtStop(){} //called from animation
	
	public void msgLeaving(Passenger p){
		log.add(new LoggedEvent("Passenger leaving bus"));
	}
	
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

	@Override
	public void msgLightGreen() {
		// TODO Auto-generated method stub
		
	}
}
