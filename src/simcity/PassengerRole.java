package simcity;
import agent.Role; 
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 


public class PassengerRole extends Role implements Passenger{
	String Destination; //eventual place that he wants to get to
	double Cash; // amount of money he has

	private enum State {none,asked,paid,onBus}; 
	private enum Action {asktoComeonBoard, toPay, toBoard, leaveBus};
	State state; 
	Action action; 
	
	private class BusRoute {
		Bus bus; 
		BusStop busstop; 
		int busStopX, busStopY; 
		int destinationX, destinationY; 
		double fare; 
		String destination; //busstop name that he wants to get off at
	}
	BusRoute busroute = new BusRoute(); 
	String name; 
	
	public PassengerRole(String name){
		super();
		this.name = name; 
		state= State.none; 
		Cash = 100; 
	}
	
	
	//Messages 
	public void msgHereIsPrice(Bus b, double fare){
		busroute.fare = fare;  //should I first check if it's the correct bus? 
		action = Action.toPay; 
		stateChanged();
	}
	
	public void msgComeOnBus(Bus b){
		action = Action.toBoard; 
		stateChanged();
	}
	
	public void msgNowAtStop(String stop){
		if (stop.equals(busroute.destination)){
			action = Action.leaveBus; 
			stateChanged();
		}
	}
	
	public void msgBusLeaving(Bus b){ 
		action = Action.leaveBus; 
		stateChanged();
	}
	
	public void gotoBus(){
		action = Action.asktoComeonBoard; 
		stateChanged();
	}
	
	public void msgAtBusStop(){ //called from animation
		//probably should rewrite this
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		if (state == State.none && action == Action.asktoComeonBoard){
			askBus();
			return true; 
		}
		if (state == State.asked && action == Action.toPay){
			payFare();
			return true; 
		}
		if (state == State.paid && action == Action.toBoard){
			boardBus();
			return true; 
		}
		if (state == State.onBus && action == Action.leaveBus){
			LeaveBus();
			return true; 
		}
		return false; 
	}
	
	public void askBus() {
		state = State.asked; 
		if (busroute.busstop.isBusAtStop(busroute.bus)){ //if the bus is at the stop send the message to the bus
			busroute.bus.msgCanIComeOnBus(this);
			return; 
		}
		else { //bus not at stop wait at busstop
			busroute.busstop.msgatBusStop(this);
		}
	}
	
	public void payFare(){
		if (Cash >= busroute.fare){
			Cash = Cash - busroute.fare; 
			state = State.paid; 
			busroute.bus.msgHeresMyFare(this, busroute.fare);
		}
		else { //can't pay has to leave
			LeaveBus();
		}	
	}
	
	public void boardBus(){
		state = State.onBus; 
		//DoGoOnBus(); gui stuff
	}
	
	public void LeaveBus(){
		state = State.none; 
		//DoLeaveBus();
		busroute.bus.msgLeaving(this);
	}
	
	//accessors/setters 
	public void setBus(Bus b){
		busroute.bus = b;
	}
	
	public void setBusStop(BusStop bs){
		busroute.busstop = bs; 
	}
	
	public void setDestination(String ds){
		busroute.destination = ds; 
	}
}
