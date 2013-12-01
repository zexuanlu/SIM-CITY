package person;

import market.interfaces.MarketCashier;
import person.Location.LocationType;
import agent.TimeCard;
import restaurant.Restaurant1CashierRole;
import restaurant.Restaurant1CookRole;
import restaurant.Restaurant1HostRole;

public class Restaurant extends Location{
	
	private Restaurant1HostRole host;
	private Restaurant1CashierRole cashier;
	private Restaurant1CookRole cook;
	private TimeCard timeCard;
	
	public Restaurant(String n, Restaurant1HostRole h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Restaurant(String n, Restaurant1HostRole h, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
		this.host = h;
	}
	public Restaurant1HostRole getHost(){ return host; }
	public void setHost(Restaurant1HostRole h){ this.host = h; }
	public Restaurant1CashierRole getCashier() {
		return cashier;
	}
	public void setCashier(Restaurant1CashierRole cashier) {
		this.cashier = cashier;
	}
	public Restaurant1CookRole getCook() {
		return cook;
	}
	public void setCook(Restaurant1CookRole cook) {
		this.cook = cook;
	}
	public TimeCard getTimeCard(){ return timeCard; }
}
