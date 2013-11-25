package simcity;
import person.Position;
import simcity.gui.PassengerGui; 
import agent.Role;
import simcity.interfaces.BusStop; 
import simcity.interfaces.Bus; 
import simcity.interfaces.Passenger; 
import person.interfaces.Person; 

import java.util.concurrent.Semaphore;

//Passenger Role created with an original position and also an eventual Destination
public class PassengerRole extends Role implements Passenger{   //TEMPORARY MADE AGENT SO THAT IT WORKS
	private Semaphore atStop = new Semaphore(0,true);
	PassengerGui myGui = null; 
	Person myPerson; 
	public CityMap citymap = null; 
	String Destination; //eventual place that he wants to get to
	double Cash; // amount of money he has

	private enum State {none,asked,paid,onBus}; 
	private enum Action {asktoComeonBoard, toPay, toBoard, leaveBus};
	State state; 
	Action action; 
	
	BusRoute busroute = new BusRoute(); 
	String name; 
	
	public PassengerRole(String name,Person p){
		super(p);
		this.name = name; 
		state= State.none; 
		Cash = 100; 
		myPerson = p; 
	}
	
	//Messages 
	public void msgHereIsPrice(Bus b, double fare){
		print("msg here is price called inpassenger");
		busroute.fare = fare;  //should I first check if it's the correct bus? 
		action = Action.toPay; 
		stateChanged();
	}
	
	public void msgComeOnBus(Bus b){
		action = Action.toBoard; 
		stateChanged();
	}
	
	public void msgNowAtStop(String stop){
		print("atStop name is "+ stop + "bus stop destination is " + busroute.destination);
		if (stop.equals(busroute.destination)){
			System.out.println("hit in statechanged of msgnowatstop");
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
		atStop.release();
	}
	
	//Scheduler
	public boolean pickAndExecuteAnAction() {
		print ("Passenger scheduler called");
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
		atStop.drainPermits();
		if (myGui != null){
			myGui.GoToBusStop(busroute.busStopX, busroute.busStopY);
			try{
				atStop.acquire();
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (busroute.busstop.isBusAtStop(busroute.bus)){ //if the bus is at the stop send the message to the bus
			print("Passenger asked to come on bus");
			busroute.bus.msgCanIComeOnBus(this);
			return; 
		}
		else { //bus not at stop wait at busstop
			print("Passenger at busStop");
			busroute.busstop.msgatBusStop(this);
		}
	}
	
	public void payFare(){
	//	if (myPerson. >= busroute.fare){
			//myPerson.msgAddMoney(-(int)busroute.fare);
			state = State.paid; 
			print("Passenger paid fare");
			busroute.bus.msgHeresMyFare(this, busroute.fare);
	//	}
	//	else { //can't pay has to leaveBus
		//	print("Passenger can't afford fare, leaving bus");
			//LeaveBus();
	//	}	
	}
	
	public void boardBus(){
		state = State.onBus; 
		if (myGui != null){
			myGui.GoOnBus();
		}
	}
	
	public void LeaveBus(){
		state = State.none; 
	//	if (myGui != null){
	//		myGui.LeaveBus(busroute.destinationX, busroute.destinationY, citymap.getDestination(Destination).width, citymap.getDestination(Destination).height);
	//	}
		print("Passenger leaving bus");
		busroute.bus.msgLeaving(this);
		myPerson.msgAtDest(myGui.xPos, myGui.yPos);

	//	myPerson.msgFinishedEvent(this);
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
	
	public void setPassDestination(int finalx, int finaly){
		print("final x and final y" + finalx + " , " + finaly);
		busroute = citymap.generateBusInformation(finalx,finaly, myGui.xPos, myGui.yPos);
	}
	
	public void setCityMap (CityMap cm){
		citymap = cm; 
	}
	
	public void setGui(PassengerGui pgui){
		myGui = pgui; 
	}

	public String toString(){
		return name; 
	}
	
}
