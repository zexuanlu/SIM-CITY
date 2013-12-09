package utilities;
import java.util.*;

import person.interfaces.Person;
import market.interfaces.MarketTruck;
import simcity.CarAgent;
import simcity.interfaces.Bus;
import agent.Agent;


public class TrafficLightAgent extends Agent {    

	public List<MyCar> myCar = new ArrayList<MyCar>();
	public List<MyBus> myBus = Collections.synchronizedList(new ArrayList<MyBus>());
	public List<MyTruck> myTruck = new ArrayList<MyTruck>();
	public List<MyPeople> myPeople = new ArrayList<MyPeople>();

	private boolean Hlight = false;
	private boolean Vlight = true;
	private boolean Plight = false;
	
	private int lightCount = 0;
	 Timer timer = new Timer();

	////////////////////////////////////////////////////remember synchronized list!!!!!!!!!!!!!!!!!!!!
	
	public TrafficLightAgent(){
	//	 timer.scheduleAtFixedRate(new RemindTask(), 0, 
		//	        1 * 1000); //subsequent rate
		 
		 
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
		System.out.println("myCar size is "+myCar.size());
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

		if(Vlight){
			GoVertical();
			return true;
		}
		
		if(Hlight){
			GoHorizontal();
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
		
		synchronized(myBus){
		for(MyBus bus: myBus){
			if(bus.s == state.leftright){
				bus.bus.msgLightGreen();
				bus.s = state.done; 
			//	myBus.remove(bus);
			}
			}
		}
		
		
		for(MyCar car: myCar){
			if(car.s == state.leftright){
				car.car.msgLightGreen();
				car.s = state.done; 
				//myCar.remove(car);
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
		synchronized(myBus){
			for(MyBus bus: myBus){
				if(bus.s == state.updown){
					bus.bus.msgLightGreen();
					bus.s = state.done; 

//					myBus.remove(bus);
				}
			}
		}
		for(MyCar car: myCar){
			if(car.s == state.updown){
				car.car.msgLightGreen();
				car.s = state.done; 				
				//myCar.remove(car);
			}
		}

	}

	private void PeopleGo(){
		for(MyPeople p: myPeople){
			//message people
			myPeople.remove(p);
		}
	}
	
		public void RemindTask() {
			System.out.println("TrafficLightAgent remind task "+ lightCount);
		
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

