package person;

import person.bank.interfaces.TimeCard;
import person.Location.LocationType;
import person.bank.BankHostRole;
import person.bank.BankTimeCard;
import person.bank.interfaces.BankHost;
import person.interfaces.Host;

public class Bank extends Location{
	
	private BankHost host;
	private BankTimeCard timeCard;
	
	public Bank(String n, BankHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Bank(String n, BankTimeCard t, BankHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
		this.timeCard = t;
	}

	public BankHost getHost(){ return host; }
	public TimeCard getTimeCard(){ return timeCard; }
	public void setHost(BankHost h){ this.host = h; }
}
