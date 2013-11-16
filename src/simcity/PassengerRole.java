package simcity;
import agent.Role; 

public class PassengerRole extends Role{
	String Destination; //eventual place that he wants to get to
	double Cash; // amount of money he has

	private enum State {none,asked,paid,onBus}; 
	private enum Action {asktoComeonBoard, toPay, toBoard, leaveBus};
	State state; 
	Action action; 
	
	private class BusRoute {
		private BusRole bus; 
		private BusStop busstop; 
		private int busStopX, busStopY; 
		private int destinationX, destinationY; 
		private double fare; 
		private String destination; //busstop name that he wants to get off at
	}
	BusRoute busroute; 
	
	//Messages 
	public void msgHereIsPrice(BusRole b, double fare){
		busroute.fare = fare;  //should I first check if it's the correct bus? 
		action = Action.toPay; 
		stateChanged();
	}
	
	public void msgComeOnBus(BusRole b){
		action = Action.toPay; 
		stateChanged();
	}
	
	public void msgNowAtStop(String stop){
		if (stop.equals(busroute.destination)){
			action = Action.leaveBus; 
			stateChanged();
		}
	}
	
	public void msgBusLeaving(BusRole b){ 
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
	protected boolean pickAndExecuteAnAction() {
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
			//leaveBus();
			return true; 
		}
		return false; 
	}
	
	public void askBus() {
		state = State.asked; 
		//busroute.bus.msgCanIComeOnBus(this);
	}
	
	public void payFare(){
		if (Cash >= busroute.fare){
			Cash = Cash - busroute.fare; 
			state = State.paid; 
			//busroute.bus.msgHeresMyFare(this, busroute.fare);
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
		//busroute.bus.msgLeftBus(this);
	}
}
