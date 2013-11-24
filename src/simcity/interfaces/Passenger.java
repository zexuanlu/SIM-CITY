package simcity.interfaces;


public interface Passenger {
	
	public abstract void msgHereIsPrice(Bus b, double fare);

	public abstract void msgComeOnBus(Bus b);

	public abstract void msgNowAtStop(String stop);
	
	public abstract void gotoBus();


}
