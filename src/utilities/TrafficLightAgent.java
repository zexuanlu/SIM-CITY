package utilities;
import java.util.*;

import person.interfaces.Person;
import market.interfaces.MarketTruck;
import simcity.CarAgent;
import simcity.interfaces.Bus;
import agent.Agent;


public class TrafficLightAgent extends Agent {    

	public List<MyCar> myCar = new ArrayList<MyCar>();
	public List<MyBus> myBus = new ArrayList<MyBus>();
	public List<MyTruck> myTruck = new ArrayList<MyTruck>();
	public List<MyPeople> myPeople = new ArrayList<MyPeople>();

	private boolean Hlight = false;
	private boolean Vlight = true;
	private boolean Plight = false;
	
	private int lightCount = 0;

	////////////////////////////////////////////////////remember synchronized list!!!!!!!!!!!!!!!!!!!!
	
	public TrafficLightAgent(){
		 Timer timer = new Timer();
		 timer.scheduleAtFixedRate(new RemindTask(), 0, 
			        1 * 1000); //subsequent rate
	}
	
	public class MyCar {
		CarAgent car;
		state s;

		MyCar(CarAgent car, state s){
			this.car = car;
			this.s = s;
		}
	}

	public class MyBus {
		Bus bus;
		state s;

		MyBus(Bus bus, state s){
			this.bus = bus;
			this.s = s;
		}
	}

	public class MyTruck {
		MarketTruck truck;
		state s;

		MyTruck(MarketTruck truck, state s){
			this.truck = truck;
			this.s = s;
		}
	}

	public class MyPeople {
		Person p;

		MyPeople(Person p){
			this.p = p;
		}
	}

	enum state {leftright, updown}

	public void msgCheckLight(CarAgent c, int x, int y){
		if (x<= 1 && x >= 3){
			myCar.add(new MyCar(c, state.updown));
		}
		else if (y<= 1 && y >= 3){
			myCar.add(new MyCar(c, state.leftright));
		}
		stateChanged();
	}

	public void msgCheckLight(Bus bus, int x, int y){
		if (x<= 1 && x >= 3 ){
			myBus.add(new MyBus(bus, state.updown));
		}
		else if (y<= 1 && y >= 3 ){
			myBus.add(new MyBus(bus, state.leftright));
		}
		stateChanged();
	}

	public void msgCheckLight(MarketTruck truck, int x, int y){
		if (x<= 1 && x >= 3){
			myTruck.add(new MyTruck(truck, state.updown));
		}
		else if (y<= 1 && y >= 3){
			myTruck.add(new MyTruck(truck, state.leftright));
		}
		stateChanged();
	}

	public void msgCheckLight(Person p){

		myPeople.add(new MyPeople(p));
		stateChanged();
	}


	///////////////Timer!!!!!!!!!!!!!!!!!!!!!!!

	protected boolean pickAndExecuteAnAction() {

		if(Hlight){
			GoHorizontal();
			return true;
		}

		if(Vlight){
			GoVertical();
			return true;
		}

		if(Plight){
			PeopleGo();
			return true;
		}
		return false;
	}

	private void GoHorizontal(){

		for(MyTruck t: myTruck){
			if(t.s == state.leftright){
				//message truck
				myTruck.remove(t);
			}
		}
		for(MyBus bus: myBus){
			if(bus.s == state.leftright){
				//message bus
				myBus.remove(bus);
			}
		}
		for(MyCar car: myCar){
			if(car.s == state.leftright){
				//message car
				myCar.remove(car);
			}
		}


	}

	private void GoVertical(){

		for(MyTruck t: myTruck){
			if(t.s == state.updown){
				//message truck
				myTruck.remove(t);
			}
		}
		for(MyBus bus: myBus){
			if(bus.s == state.updown){
				//message bus
				myBus.remove(bus);
			}
		}
		for(MyCar car: myCar){
			if(car.s == state.updown){
				//message car
				myCar.remove(car);
			}
		}

	}

	private void PeopleGo(){
		for(MyPeople p: myPeople){
			//message people
			myPeople.remove(p);
		}
	}
	
	class RemindTask extends TimerTask{

		public void run() {
			int s = lightCount;
			if(lightCount < 2){
				lightCount ++;
			}
			else{
				lightCount = 0;
			}
			if(s == 0){
				Hlight = true;
				Vlight = false;
				Plight = false;
			}
			else if(s == 1){
				Hlight = false;
				Vlight = false;
				Plight = true;
			}
			else if(s == 2){
				Hlight = true;
				Vlight = false;
				Plight = false;
			}
			
		}
		
	}

}
