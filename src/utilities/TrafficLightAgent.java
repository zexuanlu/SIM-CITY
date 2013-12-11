package utilities;
import java.util.*;

import person.interfaces.Person;
import market.interfaces.MarketTruck;
import simcity.CarAgent;
import simcity.interfaces.Bus;
import agent.Agent;


public class TrafficLightAgent extends Agent {    

	public List<MyCar> myCar = Collections.synchronizedList(new ArrayList<MyCar>());
	public List<MyBus> myBus = Collections.synchronizedList(new ArrayList<MyBus>());
	public List<MyPeople> myPeople = Collections.synchronizedList(new ArrayList<MyPeople>());

	private boolean Hlight = false;
	private boolean Vlight = true;
	private boolean Plight = false;

	private int lightCount = 0;
	Timer timer = new Timer();

	////////////////////////////////////////////////////remember synchronized list!!!!!!!!!!!!!!!!!!!!

	public TrafficLightAgent(){


		timer.schedule(new TimerTask() {
			public void run() {
				RemindTask();
			}
		},
		2000);//

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
		state s; 

		MyPeople(Person p){
			this.p = p;
		}
	}

	enum state {leftright, updown, done}

	public void msgCheckLight(CarAgent c, int x, int y){
		if (x<= 440 && x >= 340){
			myCar.add(new MyCar(c, state.updown));
		}
		else if (y<= 280 && y >= 180){
			myCar.add(new MyCar(c, state.leftright));
		}
		stateChanged();
	}

	public void msgCheckLight(Bus bus, int x, int y){
		if (x<= 440 && x >= 340 ){
			myBus.add(new MyBus(bus, state.updown));
		}
		else if (y<= 280 && y >= 180 ){
			myBus.add(new MyBus(bus, state.leftright));
		}
		stateChanged();
	}


	public void msgCheckLight(Person p){

		myPeople.add(new MyPeople(p));
		stateChanged();
	}


	///////////////Timer!!!!!!!!!!!!!!!!!!!!!!!

	protected boolean pickAndExecuteAnAction() {

		if(Vlight){
			GoVertical();
			return true;
		}

		if(Hlight){
			GoHorizontal();
			return true;
		}

		if(Plight){
			print("hit here in peoplego");
			PeopleGo();
			return true;
		}
		return false;
	}

	private void GoHorizontal(){

		synchronized(myBus){
			for(MyBus bus: myBus){
				if(bus.s == state.leftright){
					bus.bus.msgLightGreen();
					bus.s = state.done; 
					//	myBus.remove(bus);
				}
			}
		}

		synchronized(myCar){
			for(MyCar car: myCar){
				if(car.s == state.leftright){
					car.car.msgLightGreen();
					car.s = state.done; 
					//myCar.remove(car);
				}
			}
		}


	}

	private void GoVertical(){	
		synchronized(myBus){
			for(MyBus bus: myBus){
				if(bus.s == state.updown){
					bus.bus.msgLightGreen();
					bus.s = state.done; 

					//					myBus.remove(bus);
				}
			}
		}
		synchronized(myCar){
			for(MyCar car: myCar){
				if(car.s == state.updown){
					car.car.msgLightGreen();
					car.s = state.done; 				
					//myCar.remove(car);
				}
			}
		}

	}

	private void PeopleGo(){

		synchronized(myPeople){
			
			for(MyPeople p: myPeople){
			//	if(p.s != state.done){

				p.p.ToGo();
				p.s = state.done;
			//	}
			}
		}
	}

	public void RemindTask() {

		Hlight = false;
		Vlight= false;
		Plight = false;

		try { Thread.sleep(1000); }
		catch (Exception e){}

		int s = lightCount;
		if(lightCount < 2){
			lightCount ++;
		}
		else{
			lightCount = 0;
		}
		if(lightCount == 0){ //horizontal
			Hlight = true;
			Vlight = false;
			Plight = false;
		}
		else if(lightCount == 1){ //person
			Hlight = false;
			Vlight = false;
			Plight = true;
		}
		else if(lightCount == 2){ //vertical
			Hlight = false;
			Vlight = true;
			Plight = false;
		}

		timer.schedule(new TimerTask() {
			public void run() {
				RemindTask();
			}
		},
		2500);//

		stateChanged();

	}

}

