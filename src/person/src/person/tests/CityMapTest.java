package person.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import person.Bank;
import person.BankHostRole;
import person.Event;
import person.Location;
import person.Location.LocationType;
import person.Market;
import person.PersonAgent;
import person.Event.EventType;
import person.Position;
import person.Restaurant;
import person.interfaces.BankHost;
import person.interfaces.Host;
import person.tests.mock.EventLog;
import person.tests.mock.MockHostRole;
/*
 * Tests the various look up methods within the city map class 
 * 1. Random
 * 2. Proximity
 * 3. Type
 * 4. Name
 * 
 * @author Grant Collins
 */
public class CityMapTest extends TestCase{

	PersonAgent person;
	List<Location> locs; 
	Bank bank;
	Restaurant cr;
	Market market;
	Restaurant ir;
	Restaurant fr;
	
	public void setUp() throws Exception{

		super.setUp();	
		locs = new ArrayList<Location>();
		bank = new Bank("Bank", new BankHostRole("Host", person), new Position(10, 10), LocationType.Bank);
		cr = new Restaurant("Chinese", new MockHostRole("ChineseHost"), new Position(20, 20), LocationType.Restaurant);
		market = new Market("MarketA", new MockHostRole("MarketHost"), new Position(30, 30), LocationType.Market);
		ir = new Restaurant("Italian", new MockHostRole("ItalianHost"), new Position(40, 40), LocationType.Restaurant);
		fr = new Restaurant("Fancy", new MockHostRole("FancyHost"), new Position(40, 55), LocationType.Restaurant);

		locs.add(bank);
		locs.add(cr);
		locs.add(market);
		locs.add(ir);
		locs.add(fr);

		person = new PersonAgent("Grant", locs);
	}
	@Test
	public void testCityMapMethods() {
		//Pre: Check that there are locations in the citymap
		assertTrue("The city map should have 5 locations in it, it doesn't", person.cityMap.map.size() == 5);
		assertTrue("distanceTo(0, 0, bank) should return 14 (when cast as int) it does not", (int)person.cityMap.distanceTo(0,0,bank) == 14);
		assertTrue("Calling chooseByName should return the location with the name Chinese, it is not", 
				person.cityMap.chooseByName("Chinese").getName() == "Chinese");
		//the method call should return...
		assertTrue("Calling chooseByLocation for a restaurant from the origin (0, 0) should return chinese, it does not instead: "
				+ person.cityMap.chooseByLocation(0,0,100, LocationType.Restaurant).getName(), 
				person.cityMap.chooseByLocation(0,0,100, LocationType.Restaurant).getName() == "Chinese");
		assertTrue("Calling chooseByLocation for a restaurant from (30, 35) should return italian it does not", 
				person.cityMap.chooseByLocation(30,35,100, LocationType.Restaurant).getName() == "Italian");
		assertTrue("Calling chooseByLocation for any location from (0, 60) w/ search radius of 1 should return nothing, but it did", 
				person.cityMap.chooseByLocation(0,60,1, LocationType.Restaurant) == null);
		assertTrue("Calling chooseByType for bank should return bank, it does not", 
				person.cityMap.chooseByType(LocationType.Bank).getName() == "Bank");
		assertTrue("Calling chooseByType for a restaurant should return one of the three restaurants at random, it does not", 
				person.cityMap.chooseByType(LocationType.Restaurant).getType() == LocationType.Restaurant);
		assertTrue("Calling chooseByType for a market should return marketA, it does not", 
				person.cityMap.chooseByType(LocationType.Market).getName() == "MarketA");
	}

}
