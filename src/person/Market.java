package person;

import utilities.TimeCard;
import market.MarketCashierRole; 
import market.interfaces.MarketCashier;

public class Market extends Location{
	
	private MarketCashier cashier;
	private TimeCard timeCard;
	
	public Market(String n, Position p, LocationType type){
		super(n, type, p);
	}
	public Market(String n, MarketCashier cashier, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
		this.setCashier(cashier);
	}

	public TimeCard getTimeCard(){ return timeCard; }
	public MarketCashierRole getCashier() {
		return (MarketCashierRole)cashier;
	}
	public void setCashier(MarketCashier cashier) {
		this.cashier = cashier;
	}
}
