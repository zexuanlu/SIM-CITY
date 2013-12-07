package person;

import utilities.TimeCard;
import agent.*;
import bank.BankHostRole;
import bank.interfaces.*;
public class Bank extends Location{
	
	BankHostRole host;
	private TimeCard timeCard;
	
	public Bank(String n, BankHostRole h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Bank(String n, TimeCard t, BankHostRole h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
		this.timeCard = t;
	}

	public BankHostRole getHost(){ return host; }
	public TimeCard getTimeCard(){ return timeCard; }
	public void setHost(BankHostRole h){ this.host = h; }
}
