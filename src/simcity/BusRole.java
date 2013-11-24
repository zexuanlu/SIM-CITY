package simcity;
import java.util.*;
import java.awt.Dimension;
import java.util.concurrent.Semaphore;

import simcity.astar.AStarNode;
import simcity.astar.AStarTraversal;
import simcity.astar.Position;
import simcity.gui.BusGui; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 
import simcity.interfaces.BusStop; 
import agent.Agent; 

public class BusRole extends Agent implements Bus {
	int scale = 20; 
	int heightofStreet = 20; 
	
	
	int capacity; 
	int Cash; 
	int fare; 
	String name;
	
	Position currentPosition; 
	Position originalPosition;
	AStarTraversal aStar; 
	
	private Semaphore atStop = new Semaphore(0,true);
	private Semaphore atSlot = new Semaphore(0,true);
	
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
	
	public void msgatSlot(){
		atSlot.release();
	}
	
	public void msgStartBus(){
		busState = BusState.moving;
		print("Bus Starting");
		stateChanged();
	}

	
	public void msgCanIComeOnBus(Passenger p){
		if (busState != BusState.moving){
			myPassenger mp = new myPassenger(p);
			mp.state = PersonState.waiting; 
			passengers.add(mp);
		}
		System.out.println("size of passengers is "+ passengers.size());
		stateChanged();
	}
	
	public void msgHereisList(List<Passenger> sentpassengers){
		if (sentpassengers.size()>0){
			for (Passenger p: sentpassengers){
				myPassenger mp = new myPassenger(p);
				mp.state = PersonState.waiting;
				passengers.add(mp);
			}
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
			
			if (passengers.size() != 0){
				for (myPassenger mp: passengers){
					if (mp.state == PersonState.leaving){
						passengerLeaving(mp);
						return true; 
					}
				}
			}
			
			
			if (busState == BusState.toAnnounce){
				announceStop();
				return true; 
			}
			
			if (passengers.size() != 0){
			for (myPassenger mp: passengers){
				if (mp.state == PersonState.paid){
					welcomeAboard(mp);
					return true; 
				}
			}
			}
			
			if (passengers.size() != 0){
				for (myPassenger mp:passengers){
					if (mp.state == PersonState.waiting){
						seatPassenger(mp);
						return true; 
					}
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
			
			if (busgui != null){
				atStop.drainPermits();
				
				if (currentbusstop.getDirection().equals("up")){
					GoToBusStop(d.width, d.height - heightofStreet);
				}
				else if (currentbusstop.getDirection().equals("down")){
					GoToBusStop(d.width, d.height + heightofStreet);
	
				}
				else if (currentbusstop.getDirection().equals("left")){
					GoToBusStop(d.width - heightofStreet, d.height);
				}	
				else if (currentbusstop.getDirection().equals("right")){
					GoToBusStop(d.width + heightofStreet, d.height);
				}
				try {
					atStop.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
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
		public void setAStar(	AStarTraversal as ){
			aStar = as; 
			currentPosition = new Position(busgui.xPos/scale, busgui.yPos/scale);
	        currentPosition.moveInto(aStar.getGrid());
	        originalPosition = currentPosition;
		}
		
		//this is just a subroutine for waiter moves. It's not an "Action"
	    //itself, it is called by Actions.
	    private void guiMoveFromCurrentPositionTo(Position to){

		AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
		List<Position> path = aStarNode.getPath();
		Boolean firstStep   = true;
		Boolean gotPermit   = true;

		for (Position tmpPath: path) {
		    //The first node in the path is the current node. So skip it.
		    if (firstStep) {
			firstStep   = false;
			continue;
		    }

		    //Try and get lock for the next step.
		    int attempts    = 1;
		    gotPermit       = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());

		    //Did not get lock. Lets make n attempts.
		    while (!gotPermit && attempts < 3) {
			//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

			//Wait for 1sec and try again to get lock.
			try { Thread.sleep(1000); }
			catch (Exception e){}

			gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
			attempts ++;
		    }

		    //Did not get lock after trying n attempts. So recalculating path.            
		    if (!gotPermit) {
			//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
		    	
		    	/////////////ADDED
		    	path.clear();
		    	aStarNode=null; //added later
			guiMoveFromCurrentPositionTo(to);
			break;
		    }

		    //Got the required lock. Lets move.
		    //System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
		    currentPosition.release(aStar.getGrid());
		    currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
		    busgui.moveto(currentPosition.getX(), currentPosition.getY());
			try {
				atSlot.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			}
	    }
		
	    private void GoToBusStop(int x, int y){
	    	guiMoveFromCurrentPositionTo(new Position((x/scale),(y/scale)));
	    }
	    
		
}
