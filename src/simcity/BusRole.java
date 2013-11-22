package simcity;
import java.util.*;
import java.awt.Dimension;
import java.util.concurrent.Semaphore;

import simcity.gui.BusGui; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 
import simcity.interfaces.BusStop; 
import agent.Agent; 

public class BusRole extends Agent implements Bus {
	int capacity; 
	int Cash; 
	int fare; 
	String name;
	
	private Semaphore atStop = new Semaphore(0,true);
	private BusGui busgui; 
	public CityMap citymap = new CityMap(); 
	
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
		starting, stopped, toAnnounce, abouttoleave, moving;
	}
	BusState busState; 
	
	public List<String> RouteA = new ArrayList<String>(); //journey there
	public String currentStop; 
	public BusStop currentbusstop; 
	
	public BusRole(String name){
		super();		
		this.name = name; 
		capacity = 100; 

		
	}
	
	public void msgStartBus(){
		busState = BusState.starting;
		print("Bus Starting");
		stateChanged();
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
	
	public void msgAtStop(){ //sent from animation
		atStop.release();
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
		print ("Bus time to leave");
		busState = BusState.abouttoleave; 
		stateChanged();
	}
	
	//Scheduler
		public boolean pickAndExecuteAnAction() {
			if (busState == BusState.starting){
				ontheMove();
				return true; 
			}
			
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
				print("Bus here is fare");
				mp.p.msgHereIsPrice(this, fare);;
			}
		}
		
		private void welcomeAboard(myPassenger mp){
			mp.state = PersonState.seated; 
			print("Bus Come on bus");
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
			},5000);			
		}
		
		
		public void leaveStop() {
			busState = BusState.moving; 
			print("Bus leaving stop");
			currentbusstop.msgBusLeaving(this);
			stateChanged();
			
			//AM I SWIMMING IN DANGEROUS WATERS?

		}
		
		public void ontheMove(){
			busState = BusState.toAnnounce; 
			//temporary assumption made that only RouteA 
			//or else have to deal with direction
			if (currentStop != null){
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
			}
			else {
				currentStop = RouteA.get(0); //go to first destination; this is only if first stop
			}
			
			currentbusstop = citymap.getBusStop(currentStop);
			
			Dimension d = citymap.getDimension(currentbusstop);
			
			atStop.drainPermits();
			busgui.GoToBusStop(d.width, d.height);
			try {
				atStop.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			print("Bus at bus stop");
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
		
		public void setBusMap (CityMap b){
			citymap = b;
		}
		
		public void setGui(BusGui gui){
			busgui = gui; 
		}
		
		public void addtoRoute(String r){
			RouteA.add(r);
		}
		

}
