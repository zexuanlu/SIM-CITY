package simcity.interfaces;


public interface BusStop {
	public abstract void msgatBusStop(Passenger p);
	
	public abstract void msgatBusStop(Bus b);
	public abstract boolean isBusAtStop(Bus b);
	public abstract void msgBusLeaving(Bus b);

}
