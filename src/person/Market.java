package person;

import agent.*;

public class Market extends Location{
	
	private TimeCard timeCard;
	public Market(String n, Position p, LocationType type){
		super(n, type, p);
	}
	public Market(String n, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
	}

	public TimeCard getTimeCard(){ return timeCard; }
}
