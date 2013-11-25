package simcity;
import java.awt.*;
import java.util.ArrayList; 
import java.util.List; 

import agent.Agent; 
import simcity.gui.BusStopGui;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 

public class BusStopAgent extends Agent implements BusStop{//do i have to make it an agent? 
	public List <Passenger> passengers; 
	public List <myBus> busses; //already filled in beforehand
	private BusStopGui myGui = null; 
	int scale = 20; 
	
	public String direction = null; 
	
	private class myBus{
		public Bus bus; 
		public BusState busState; 
	    public myBus(Bus bu){
	    	bus = bu;
	    }
	}
	
	public enum BusState {notatStop, arrivedatStop, atStop}; 
	public String name; 
	public String getName(){
		return name; 
	}
	

	public BusStopAgent(String name){
		super(); 
		this.name = name; 
		passengers = new ArrayList<Passenger>();
		busses = new ArrayList<myBus>();
	}
	
	public void msgBusLeaving(Bus b){
		for (myBus bu: busses){
			if (bu.bus == b){
				bu.busState = BusState.notatStop; 
				passengers.clear();
			}
		}
	}
	
	public void msgatBusStop(Passenger p){
		passengers.add(p);
	}
	
	public void msgatBusStop(Bus b){
		//check if already added in busses list, if not then add it
		boolean added = false; 
		
		for (myBus bu: busses){
			if (bu.bus == b){
				bu.busState = BusState.arrivedatStop; 
				added = true; 
				break;
			}
		}
		if (!added ){
			myBus mb = new myBus(b);
			mb.busState = BusState.arrivedatStop; 
			busses.add(mb);
			}
		stateChanged();
	}
	
	public List<Passenger> heresPassList(){
		return passengers; 
	}
	
	public boolean pickAndExecuteAnAction(){
		for (myBus mb: busses){
			if (mb.busState == BusState.arrivedatStop){
				giveList(mb);
				return true; 
			}
		}
		return false; 

	}
	
	//actions 
	private void giveList(myBus mb){
		mb.busState = BusState.atStop;
		print("BusStop here is list of "+passengers.size()+" passengers");
		mb.bus.msgHereisList(passengers);
	}
	
	//accessor
	public boolean isBusAtStop(Bus b){
		
		if (busses.size() > 0){
			for (myBus mb: busses){
				if (mb.bus == b){
					if (mb.busState != BusState.notatStop){
						return true; 
					}
					else {
						return false; 
					}
				}
			}
		}
		return false; 
	}
	
	public void addBus(Bus b){
		myBus mb = new myBus(b);
		mb.busState = BusState.notatStop; 
		busses.add(mb);
	}
	
	public void setGui(BusStopGui bgui){
		myGui = bgui; 
	}
	
	public Dimension getDim(){
		return myGui.getDim();
	}
	
	public void setDirection(String s){
		direction = s; 
	}
	
	public String getDirection(){
		return direction; 
	}

	
}
