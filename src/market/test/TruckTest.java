package market.test;

import junit.framework.TestCase;
import market.Food;
import market.MarketTruckAgent;
import market.MarketTruckAgent.CarState;
import market.interfaces.Cook;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;
import market.test.mock.MockCook;

import java.util.*;

import simcity.astar.AStarTraversal;

public class TruckTest extends TestCase{
	
	EventLog log = new EventLog();
	MarketTruckAgent truck;
	MockCook cook;
	MockCashier cashier;
	AStarTraversal aStar;
	
	public void setUp() throws Exception{
		super.setUp();
		aStar = new AStarTraversal(null);
		truck = new MarketTruckAgent(aStar);
		cook = new MockCook("cook");
		cashier = new MockCashier("C");
		
	}

	public void testNormalDeliveryCase(){
		
		truck.setCashier(cashier);
		
		List<Food> food = new ArrayList<Food>();
		
		truck.gotoPosition(cook,food, 200, 200);
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.carstate == CarState.goTo);
		
		assertTrue("truck's scheduler should have returned true, but didn't.", truck.pickAndExecuteAnAction());
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.carstate == CarState.moving);
		
		assertTrue("cook should return a string with word Your order arraived, but the last event return "
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Your order arraived"));
	}
	
}
