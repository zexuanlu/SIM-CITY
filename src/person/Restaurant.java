package person;

import market.interfaces.MarketCashier;
import person.Location.LocationType;
import agent.TimeCard;
import restaurant.Restaurant1HostRole;

public class Restaurant extends Location{
	
	private Restaurant1HostRole host;
	private TimeCard timeCard;
	
	public Restaurant(String n, Restaurant1HostRole h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Restaurant(String n, Restaurant1HostRole h, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
		this.host = h;
	}
	public Restaurant1HostRole getHost(){ return host; }
	public void setHost(Restaurant1HostRole h){ this.host = h; }
	public TimeCard getTimeCard(){ return timeCard; }
}
