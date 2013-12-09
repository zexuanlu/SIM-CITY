package simcity.interfaces;

import java.util.List;



public interface Bus {
	public abstract void msgCanIComeOnBus(Passenger p);
	
	public abstract void msgHeresMyFare(Passenger p, double Paid);
	
	public abstract void msgAtStop();
	
	public abstract void msgLeaving(Passenger p);
	public abstract void msgLightGreen();
	
	public void msgHereisList(List<Passenger> sentpassengers);

	//need to add heres passengerlist
}
