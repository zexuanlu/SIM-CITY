package person;

import person.Location.LocationType;
import person.interfaces.BankHost;
import person.interfaces.Host;

public class Bank extends Location{
	
	private BankHost host;
	
	public Bank(String n, BankHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}

	public BankHost getHost(){ return host; }
	public void setHost(BankHost h){ this.host = h; }
}
