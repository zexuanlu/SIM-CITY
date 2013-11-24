package person;

import person.Location.LocationType;
import person.interfaces.Host;

public class Restaurant extends Location{
	
	private Host host;
	
	public Restaurant(String n, Host h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}

	public Host getHost(){ return host; }
	public void setHost(Host h){ this.host = h; }

}
