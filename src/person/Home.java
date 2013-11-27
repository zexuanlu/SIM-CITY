package person;

import resident.HomeOwnerRole;


public class Home extends Location{
	
	private HomeOwnerRole host;
	int homeNumber;
	
	public Home(String n, HomeOwnerRole hor, Position p, int hn, LocationType type){
		super(n, type, p);
		this.host = hor;
		this.homeNumber = hn;
	}

	public HomeOwnerRole getHost(){ return host; }
	public void setHost(HomeOwnerRole h){ this.host = h; }
	public int getNumber(){ return homeNumber; }
}
