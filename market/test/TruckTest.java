package market.test;

import junit.framework.TestCase;
import market.Food;
import market.MarketTruckAgent;
import market.MarketTruckAgent.state;
import restaurant.*;
import restaurant.test.mock.MockRestaurantCook;
import market.gui.MarketTruckGui;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;

import java.util.*;

import simcity.astar.AStarTraversal;

public class TruckTest extends TestCase{
	
	EventLog log = new EventLog();
	MarketTruckAgent truck;
	MockRestaurantCook cook;
	MockCashier cashier;
	MarketTruckGui truckGui;
	
	public void setUp() throws Exception{
		super.setUp();
		truck = new MarketTruckAgent();
		truckGui = new MarketTruckGui(truck);
		cook = new MockRestaurantCook("cook");
		cashier = new MockCashier("C");
		
	}

	public void testNormalDeliveryCase(){
		
		truck.setCashier(cashier);
		
		truck.setGui(truckGui);
		
		List<Food> food = new ArrayList<Food>();
		
		truck.gotoPosition(cook,food, 200, 200);
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.s == state.collecting);
		
		truck.msgrelease();
		
		assertTrue("truck's scheduler should have returned true, but didn't.", truck.pickAndExecuteAnAction());
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.s == state.sending);
		
		truck.msgGoBack();
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.s == state.readytoback);
		
		truck.msgrelease();
		
		assertTrue("truck's scheduler should have returned true, but didn't.", truck.pickAndExecuteAnAction());
		
		assertTrue("truck should contain a state == back. It doesn't.",
				truck.s == state.back);
		
	}
	
}
