package market.test.mock;

import java.util.List;

import person.Restaurant;
import restaurant1.Restaurant1CookRole;
import restaurant1.interfaces.Restaurant1Cashier;
import utilities.restaurant.RestaurantCashier;
import utilities.restaurant.RestaurantCook;
import market.MarketCashierRole;
import market.Food;
import market.interfaces.MarketCashier;
import market.interfaces.MarketCustomer;
import market.interfaces.MarketTruck;

public class MockCashier extends Mock implements MarketCashier{

	public EventLog log = new EventLog();
	
	public MockCashier(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgHereisOrder(MarketCustomer customer, List<Food> food) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got Your Order"));
	}

	@Override
	public void msgPayment(MarketCustomer customer, double m) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Receive "+m));
	}

	@Override
	public void msgHereisProduct(MarketCustomer customer, List<Food> order) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Got it"));
	}

	@Override
	public void msgGoToTable(MarketCustomer customer) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("You are at the table"));
	}





	@Override
	public void msgTruckBack(MarketTruck t) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void MsgIwantFood(RestaurantCook cook, RestaurantCashier ca,
			List<Food> food, int number) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBillFromTheAir(double money) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDevliveryFail(MarketTruck t, RestaurantCook cook,
			List<Food> food, Restaurant r, int restnum) {
		// TODO Auto-generated method stub
		
	}

}
