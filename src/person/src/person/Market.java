package person;

import person.Location.LocationType;
import person.interfaces.Host;

public class Market extends Location{
	
	private Host host;
	private TimeCard timeCard
	public Market(String n, Host h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Market(String n, Host h, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
		this.timeCard = t;
	}

	public Host getHost(){ return host; }
	public TimeCard getTimeCard(){ return timeCard; }
	public void setHost(Host h){ this.host = h; }
}
