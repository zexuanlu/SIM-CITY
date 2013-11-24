package person;

import person.Location.LocationType;
import person.interfaces.Host;

public class Home extends Location{
	
	private PersonAgent host;
	
	public Home(String n, PersonAgent person, Position p, LocationType type){
		super(n, type, p);
		this.host = person;
	}

	public PersonAgent getHost(){ return host; }
	public void setHost(PersonAgent h){ this.host = h; }
}
