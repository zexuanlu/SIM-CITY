package person;

import restaurant.Restaurant1HostRole;

public class Restaurant extends Location{
	
	private Restaurant1HostRole host;
	
	public Restaurant(String n, Restaurant1HostRole h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}

	public Restaurant1HostRole getHost(){ return host; }
	public void setHost(Restaurant1HostRole h){ this.host = h; }

}
