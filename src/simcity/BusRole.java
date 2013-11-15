package simcity;
import java.util.*; 

public class BusRole {
	int capacity; 
	int Cash; 
	int fare; 
	
	List<myPassenger> passengers = new ArrayList<myPassenger>(); 
	private class myPassenger{
		PassengerRole p; 
		PersonState state;
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

}
