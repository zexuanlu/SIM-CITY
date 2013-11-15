package simcity;


public class PassengerRole {
	String Destination; //eventual place that he wants to get to
	int Cash; // amount of money he has

	private enum State {none,asked,paid,onBus}; 
	private enum Action {asktoComeonBoard, toPay, toBoard, leaveBus};
	State state; 
	Action action; 
	
	private class BusRoute {
		private BusRole bus; 
		private BusStop busstop; 
		private int busStopX, busStopY; 
		private int destinationX, destinationY; 
		private int fare; 
		private String destination; //busstop name that he wants to get off at
	}
	BusRoute busroute; 
	
	//Messages 
	public void msgHereIsPrice(BusRole b, int fare){
		busroute.fare = fare;  //should I first check if it's the correct bus? 
		action = Action.toPay; 
	}
	
	public void msgComeOnBus(BusRole b){
		action = Action.toPay; 
	}
	
	public void msgNowAtStop(String stop){
		if (stop.equals(busroute.destination)){
			action = Action.leaveBus; 
		}
	}
	
	public void msgBusLeaving(BusRole b){
		action = Action.leaveBus; 
	}
	
	public void msgAtBusStop(){
		action = Action.asktoComeonBoard; 
	}

}
