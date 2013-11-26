package person;

import resident.HomeOwnerRole;


public class Home extends Location{
	
	private HomeOwnerRole host;
	
	public Home(String n, HomeOwnerRole hor, Position p, LocationType type){
		super(n, type, p);
		this.host = hor;
	}

	public HomeOwnerRole getHost(){ return host; }
	public void setHost(HomeOwnerRole h){ this.host = h; }
}
