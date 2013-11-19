package simcity;
import java.util.*;
import agent.Agent; 
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 

public class BusStopAgent extends Agent implements BusStop{ //do i have to make it an agent? 
	public List <Passenger> passengers; 
	public List <myBus> busses; //already filled in beforehand
	
	private class myBus{
		public Bus bus; 
		public BusState busState; 
	    public myBus(Bus bu){
	    	bus = bu;
	    }
	}
	public enum BusState {notatStop, arrivedatStop, atStop}; 
	String name; 
	
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
			}
		}
	}
	
	public void msgatBusStop(Passenger p){
		passengers.add(p);
	}
	
	public void msgatBusStop(Bus b){
		for (myBus bu: busses){
			if (bu.bus == b){
				bu.busState = BusState.arrivedatStop; 
				stateChanged();
			}
		}
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
		mb.bus.msgHereisList(passengers);
	}
	
	//accessor
	public boolean isBusAtStop(Bus b){
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
		return false; 
	}
	
	public void addBus(Bus b){
		myBus mb = new myBus(b);
		mb.busState = BusState.notatStop; 
		busses.add(mb);
	}
	
	
}
