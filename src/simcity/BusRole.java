package simcity;
import java.util.*; 
import agent.Role; 

public class BusRole extends Role {
	int capacity; 
	int Cash; 
	int fare; 
	
	List<myPassenger> passengers = new ArrayList<myPassenger>(); 
	private class myPassenger{
		PassengerRole p; 
		PersonState state;
		myPassenger(PassengerRole pass){
			p = pass; 
		}
	}
	public enum PersonState {
		waiting, paying, paid, seated, leaving
	};
	
	public enum BusState {
		atStop, stopped, abouttoleave, moving;
	}
	BusState busState; 
	
	List<String> RouteA = new ArrayList<String>(); //journey there
	List<String> RouteB = new ArrayList<String>(); //journey back
	String currentStop; 
	
	public void msgCanIComeOnBus(PassengerRole p){
		myPassenger mp = new myPassenger(p);
		mp.state = PersonState.waiting; 
		passengers.add(mp);
	}
	
	public void msgHeresMyFare(PassengerRole p, double Paid){
		for (myPassenger mp: passengers){
			if (mp.p == p){
				if (Paid == fare){
					mp.state = PersonState.paid;
					break; 
				}
			}
		}
		stateChanged(); //should I call this only in there?
	}
	
	public void msgAtStop(String stop){
		currentStop = stop; 
		busState = BusState.atStop; 
		stateChanged();
	}
	
	public void msgLeaving(PassengerRole p){
		for (myPassenger mp:passengers){
			if (mp.p == p){
				mp.state = PersonState.leaving;
			}
		}
		stateChanged();
	}
	
	public void msgStartBus(){ //called from timer
		busState = BusState.abouttoleave;
		stateChanged();
	}
	
	//Scheduler
		protected boolean pickAndExecuteAnAction() {
			
			
			return true; 
			
		}
}
