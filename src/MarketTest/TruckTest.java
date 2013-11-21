package MarketTest;

import junit.framework.TestCase;
import market.TruckAgent;
import market.TruckAgent.state;
import markettestmock.EventLog;
import markettestmock.MockCook;

import java.util.*;

public class TruckTest extends TestCase{
	
	EventLog log = new EventLog();
	TruckAgent truck;
	MockCook cook;
	
	public void setUp() throws Exception{
		super.setUp();
		truck = new TruckAgent();
		cook = new MockCook("cook");
	}

	public void testNormalDeliveryCase(){
		
		List food = new ArrayList();
		truck.msgPleaseDiliver(cook, food);
		
		assertTrue("truck's scheduler should have returned true, but didn't.", truck.pickAndExecuteAnAction());
		
		assertTrue("truck should contain a state == sending. It doesn't.",
				truck.s == state.sending);
		
		assertTrue("cook should return a string with word Your order arraived, but the last event return "
				+ cook.log.getLastLoggedEvent().toString(), cook.log.containsString("Your order arraived"));
	}
	
}
