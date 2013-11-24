package market.test;

import junit.framework.TestCase;
import market.MarketTruckAgent;
import market.MarketTruckAgent.state;
import market.test.mock.EventLog;
import market.test.mock.MockCashier;
import market.test.mock.MockCook;

import java.util.*;

public class TruckTest extends TestCase{
	
	EventLog log = new EventLog();
	MarketTruckAgent truck;
	MockCook cook;
	MockCashier cashier;
	
	public void setUp() throws Exception{
		super.setUp();
		truck = new MarketTruckAgent();
		cook = new MockCook("cook");
		cashier = new MockCashier("C");
	}

	public void testNormalDeliveryCase(){
		
		truck.setCashier(cashier);
		
		List food = new ArrayList();
		
		truck.msgPleaseDiliver(cook, food);
		
		assertTrue("truck's scheduler should have returned true, but didn't.", truck.pickAndExecuteAnAction());
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.s == state.sending);
		
		assertTrue("cook should return a string with word Your order arraived, but the last event return "
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Your order arraived"));
	}
	
}
