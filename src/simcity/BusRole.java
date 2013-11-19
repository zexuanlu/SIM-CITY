package simcity;
import java.util.*; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 
import simcity.interfaces.BusStop; 
import agent.Role; 

public class BusRole extends Role implements Bus {
	int capacity; 
	int Cash; 
	int fare; 
	String name;
	
	Timer timer = new Timer();
	List<myPassenger> passengers = new ArrayList<myPassenger>(); 
	private class myPassenger{
		public Passenger p; 
		public PersonState state;
		myPassenger(Passenger pass){
			p = pass; 
		}
	}
	
	public enum PersonState {
		waiting, paying, paid, seated, leaving
	};
	
	public enum BusState {
		atStop, interactingbusStop, stopped, toAnnounce, abouttoleave, moving;
	}
	BusState busState; 
	
	public List<String> RouteA = new ArrayList<String>(); //journey there
	public List<String> RouteB = new ArrayList<String>(); //journey back
	String currentStop; 
	public BusStop currentbusstop; 
	
	public BusRole(String name){
		super();		
		this.name = name; 
	}

	
	public void msgCanIComeOnBus(Passenger p){
		myPassenger mp = new myPassenger(p);
		mp.state = PersonState.waiting; 
		passengers.add(mp);
		stateChanged();
	}
	
	public void msgHereisList(List<Passenger> sentpassengers){
		for (Passenger p: sentpassengers){
			myPassenger mp = new myPassenger(p);
			mp.state = PersonState.waiting;
			passengers.add(mp);
		}
		//will i have to call stateChanged as many times as there are customers in list?
		stateChanged();
	}
	
	public void msgHeresMyFare(Passenger p, double Paid){
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
	
	public void msgAtStop(String stop){ //sent from animation
		currentStop = stop; 
		busState = BusState.atStop; 
		stateChanged();
	}
	
	public void msgLeaving(Passenger p){
		for (myPassenger mp:passengers){
			if (mp.p == p){
				mp.state = PersonState.leaving;
			}
		}
		stateChanged();
	}
	
	//Scheduler
		protected boolean pickAndExecuteAnAction() {
			for (myPassenger mp: passengers){
				if (mp.state == PersonState.leaving){
					passengerLeaving(mp);
					return true; 
				}
			}
			
			if (busState == BusState.atStop){
				askforList();
				return true; 
			}
			
			if (busState == BusState.toAnnounce){
				announceStop();
				return true; 
			}
			
			for (myPassenger mp: passengers){
				if (mp.state == PersonState.paid){
					welcomeAboard(mp);
					return true; 
				}
			}
			
			if (busState == BusState.abouttoleave){
				leaveStop();
				return true; 
			}
			
			for (myPassenger mp:passengers){
				if (mp.state == PersonState.waiting){
					seatPassenger(mp);
					return true; 
				}
			}
			
			return false; 	
		}
		
		public void seatPassenger(myPassenger mp){
			if (passengers.size() <= capacity){
				mp.state = PersonState.paying; 
				mp.p.msgHereIsPrice(this, fare);;
			}
		}
		
		public void welcomeAboard(myPassenger mp){
			mp.state = PersonState.seated; 
			mp.p.msgComeOnBus(this);
		}
		
		public void passengerLeaving(myPassenger mp){
			passengers.remove(mp);
		}
		
		public void askforList(){
			currentbusstop.msgatBusStop(this);
			busState = BusState.toAnnounce; 
			stateChanged();
		}
		
		public void announceStop(){
			busState = BusState.stopped; 
			for (myPassenger mp: passengers){
				mp.p.msgNowAtStop(currentStop);
			}
			
			timer.schedule(new TimerTask() {
				public void run() {
					busState = BusState.abouttoleave; 
					stateChanged();
				}
			},10000);			
		}
		
		
		public void leaveStop() {
			busState = BusState.moving; 
			//for (myPassenger p:passengers){
				//if (p.state == PersonState.waiting){
				//	p.p.msgBusLeaving(this);
				//}
			//}
			//doGotoStop(nextStop); 
		}

}
