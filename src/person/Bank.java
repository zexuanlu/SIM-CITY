package person;

import agent.*;
import bank.interfaces.*;
public class Bank extends Location{
	
	private BankHost host;
	private TimeCard timeCard;
	
	public Bank(String n, BankHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Bank(String n, TimeCard t, BankHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
		this.timeCard = t;
	}

	public BankHost getHost(){ return host; }
	public TimeCard getTimeCard(){ return timeCard; }
	public void setHost(BankHost h){ this.host = h; }
}
