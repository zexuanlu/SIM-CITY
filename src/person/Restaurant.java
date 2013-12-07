package person;

import utilities.restaurant.*;
import utilities.TimeCard;

public class Restaurant extends Location{
	
	private RestaurantHost host;
	private RestaurantCashier cashier;
	private RestaurantCook cook;
	private TimeCard timeCard;
	
	public Restaurant(String n, RestaurantHost h, Position p, LocationType type){
		super(n, type, p);
		this.host = h;
	}
	public Restaurant(String n, RestaurantHost h, TimeCard t, Position p, LocationType type){
		super(n, type, p);
		this.timeCard = t;
		this.host = h;
	}
	public RestaurantHost getHost(){ return host; }
	public void setHost(RestaurantHost h){ this.host = h; }
	public RestaurantCashier getCashier() {
		return cashier;
	}
	public void setCashier(RestaurantCashier cashier) {
		this.cashier = cashier;
	}
	public RestaurantCook getCook() {
		return cook;
	}
	public void setCook(RestaurantCook cook) {
		this.cook = cook;
	}
	public TimeCard getTimeCard(){ return timeCard; }
}
