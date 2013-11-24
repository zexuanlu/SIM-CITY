package person.test;

import java.util.ArrayList;
import java.util.List;

import person.Bank;
import person.SimEvent;
import person.Home;
import person.Location;
import person.Market;
import person.PersonAgent;
import person.Position;
import person.Restaurant;
import person.SimEvent.EventType;
import person.Location.LocationType;
import person.test.mock.MockHostRole;

public class CitySetUpHelper {
	
	List<Location> locs = new ArrayList<Location>();
	List<SimEvent> events = new ArrayList<SimEvent>();
	//Bank bank = new Bank("Bank", new (BankHost)MockHostRole("BankHost"), new Position(10, 10), LocationType.Bank);
	Restaurant cr = new Restaurant("Chinese", new MockHostRole("ChineseHost"), new Position(20, 20), LocationType.Restaurant);
	Restaurant ir = new Restaurant("Italian", new MockHostRole("ItalianHost"), new Position(40, 40), LocationType.Restaurant);
	Restaurant fr = new Restaurant("Fancy", new MockHostRole("FancyHost"), new Position(40, 55), LocationType.Restaurant);
	Market marketA = new Market("MarketA", new MockHostRole("MarketAHost"), new Position(30, 30), LocationType.Market);
	Market marketB = new Market("MarketB", new MockHostRole("MarketBHost"), new Position(30, 0), LocationType.Market);
	Market marketC = new Market("MarketC", new MockHostRole("MarketCHost"), new Position(100, 0), LocationType.Market);
	
	SimEvent goToWork = new SimEvent(cr, 1, 9, 12, EventType.HostEvent); //should have a score of 2 + 1 = 3
	SimEvent goToRestaurant = new SimEvent(fr, 3, 2, 3, EventType.CustomerEvent);//3 + 5 = 8;
	SimEvent goToMarket = new SimEvent(marketA, 3, 4, 5, EventType.CustomerEvent);//3 + 3 = 6;
	
	public CitySetUpHelper(){
		
		//locs.add(bank);
		locs.add(cr);
		locs.add(ir);
		locs.add(fr);
		locs.add(marketA);
		locs.add(marketB);
		locs.add(marketC);
		
		events.add(goToMarket);
		events.add(goToWork);
		events.add(goToRestaurant);
	}
	public List<Location> getLocationList(){ return locs; }
	public List<SimEvent> getEventList(){ return events; }
}
