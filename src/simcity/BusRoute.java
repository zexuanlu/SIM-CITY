package simcity;

import simcity.interfaces.Bus;
import simcity.interfaces.BusStop; 

public class BusRoute {
		public Bus bus; 
		public BusStop busstop; 
		public int busStopX, busStopY; 
		String destination; //busstop name that he wants to get off at
		public int destinationX, destinationY; 
		public double fare; 
	}

