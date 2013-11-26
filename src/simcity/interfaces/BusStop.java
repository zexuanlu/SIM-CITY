package simcity.interfaces;

import java.awt.Dimension;


public interface BusStop {
	public abstract String getDirection(); 
	public abstract void msgatBusStop(Passenger p);
	public abstract void msgatBusStop(Bus b);
	public abstract boolean isBusAtStop(Bus b);
	public abstract void msgBusLeaving(Bus b);
	public abstract Dimension getDim();
	public abstract String getName();
}
