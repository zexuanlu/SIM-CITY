package person;

import market.MarketCashierRole;
import market.interfaces.MarketCashier;
import agent.*;

public class Market extends Location{
	
	private MarketCashier cashier;
	private TimeCard timeCard;
	
	public Market(String n, Position p, LocationType type){
		super(n, type, p);
	}
	public Market(String n, MarketCashier cashier, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
		this.cashier = cashier;
	}

	public TimeCard getTimeCard(){ return timeCard; }
}
