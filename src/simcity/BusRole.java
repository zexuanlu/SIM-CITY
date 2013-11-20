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
	
	public BusMap busmap = new BusMap(); 
	
	Timer timer = new Timer();
	public List<myPassenger> passengers = new ArrayList<myPassenger>(); 
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
		stopped, toAnnounce, abouttoleave, moving;
	}
	BusState busState; 
	
	public List<String> RouteA = new ArrayList<String>(); //journey there
	//public List<String> RouteB = new ArrayList<String>(); //journey back
	public String currentStop; 
	public BusStop currentbusstop; 
	
	public BusRole(String name){
		super();		
		this.name = name; 
	}

	
	public void msgCanIComeOnBus(Passenger p){
		if (busState != BusState.moving){
			myPassenger mp = new myPassenger(p);
			mp.state = PersonState.waiting; 
			passengers.add(mp);
			stateChanged();
		}
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
		//this should ONLY be semaphore crap
		
		//currentStop = stop; 
		//busState = BusState.atStop; 
		//stateChanged();
	}
	
	public void msgLeaving(Passenger p){
		for (myPassenger mp:passengers){
			if (mp.p == p){
				mp.state = PersonState.leaving;
			}
		}
		stateChanged();
	}
	
	public void msgtimetoLeave(){
		busState = BusState.abouttoleave; 
		stateChanged();
	}
	
	//Scheduler
		public boolean pickAndExecuteAnAction() {
			for (myPassenger mp: passengers){
				if (mp.state == PersonState.leaving){
					passengerLeaving(mp);
					return true; 
				}
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
			
			
			for (myPassenger mp:passengers){
				if (mp.state == PersonState.waiting){
					seatPassenger(mp);
					return true; 
				}
			}
			
			if (busState == BusState.abouttoleave){
				leaveStop();
				return true; 
			}
			
			if (busState == BusState.moving){
				ontheMove();
				return true; 
			}
			
			return false; 	
		}
		
		private void seatPassenger(myPassenger mp){
			if (passengers.size() <= capacity){
				mp.state = PersonState.paying; 
				mp.p.msgHereIsPrice(this, fare);;
			}
		}
		
		private void welcomeAboard(myPassenger mp){
			mp.state = PersonState.seated; 
			mp.p.msgComeOnBus(this);
		}
		
		public void passengerLeaving(myPassenger mp){
			passengers.remove(mp);
		}
		
		
		public void announceStop(){
			busState = BusState.stopped; 
			for (myPassenger mp: passengers){
				mp.p.msgNowAtStop(currentStop);
			}
			
			timer.schedule(new TimerTask() {
				public void run() {
					msgtimetoLeave();
				}
			},10000);			
		}
		
		
		public void leaveStop() {
			busState = BusState.moving; 
			currentbusstop.msgBusLeaving(this);
			stateChanged();
			
			//AM I SWIMMING IN DANGEROUS WATERS?

		}
		
		public void ontheMove(){
			busState = BusState.toAnnounce; 
			//gotoNextBusStop()
			//semaphore crap
			
			//temporary assumption made that only RouteA 
			//or else have to deal with direction
			
			int index = 0;
			for (String s :RouteA){
				index = index + 1; 
				if (currentStop.equals(s)){
					break; 
				}
			}
			
			if (index == RouteA.size()){
				index = 0; //loop back to the first stop
			}
			
			currentStop = RouteA.get(index); //knows what nextStop is
			currentbusstop = busmap.getBusStop(currentStop);
			//SOMEHOW HAS TO FIGURE OUT THE NEXT BUS STOP I guess grab it from a map? 

			currentbusstop.msgatBusStop(this); //make it ask for a list
			stateChanged();
		}
		
		public void setFare(int f){
			fare = f; 
		}
		
		public void setCapacity (int c){
			capacity = c;
		}
		
		public void setCurrentStop (String c){
			currentStop = c;
		}
		
		public void setBusMap (BusMap b){
			busmap = b;
		}

}
