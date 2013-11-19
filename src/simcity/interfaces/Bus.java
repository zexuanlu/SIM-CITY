package simcity.interfaces;

import java.util.List;



public interface Bus {
	public abstract void msgCanIComeOnBus(Passenger p);
	
	public abstract void msgHeresMyFare(Passenger p, double Paid);
	
	public abstract void msgAtStop(String stop);
	
	public abstract void msgLeaving(Passenger p);
	
	public void msgHereisList(List<Passenger> sentpassengers);

	//need to add heres passengerlist
}
