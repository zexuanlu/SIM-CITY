package restaurant6.test;

import junit.framework.TestCase;
import resident.test.mock.MockPerson;
import restaurant6.Restaurant6HostRole;
import restaurant6.test.mock.MockSDWaiter;
import restaurant6.test.mock.MockWaiter;

public class HostTest extends TestCase {
		// These are instantiated for each test separately via the setUp() method.
		Restaurant6HostRole host;
		MockSDWaiter waiter;
		MockPerson person;
		MockWaiter regWaiter;
		
		/**
		 * This method is run before each test. You can use it to instantiate the class variables
		 * for your agent and mocks, etc.
		 */
		public void setUp() throws Exception{
			super.setUp();		
			person = new MockPerson("Mock Person");
			host = new Restaurant6HostRole("Host", person);
			waiter = new MockSDWaiter("Mock Shared Data Waiter");
			regWaiter = new MockWaiter("Mock Regular Waiter");
		}
		
		/**
		 * Simple test to see if the end of day message works
		 */
		public void testEndOfDay() {
			// Preconditions: host shouldn't have any waiters 
			host.waiters.size();
			
			// Precondition: host shouldn't have any logged events
			assertEquals("Host should not have any logged events.", 0, host.log.size());
			
			// Sets the waiters to the host
			host.msgSetWaiter(waiter);
			host.msgSetWaiter(regWaiter);
			
			// Check that host's list of waiters is of size 2
			assertEquals("Host should have 2 waiters in list.",  2, host.waiters.size());
			
			// Message the host that it's the end of the day
			host.msgEndOfDay(host, 1000);
			
			// Checks that host has correct data 
			assertEquals("Host should change end of day boolean to true.", true, host.offWork);
			
			// Checks that host has correct amount of money
			assertEquals("Host should have correct amount set to restPay.", 1000.0, host.restPay);
			
			// Checks for additional log in host
			assertEquals("Host should have 1 log.", 1, host.log.size());
			assertTrue(host.log.getLastLoggedEvent().toString().contains("It's the end of the day! Time to tell all my worker bees to go home."));
			
			// Invokes the scheduler and makes sure that it returns true
			assertTrue(host.pickAndExecuteAnAction());
			
			// Checks to make sure it messaged all waiters
			assertEquals("SD Waiter should have event log.", 1, waiter.log.size());
			assertTrue(waiter.log.getLastLoggedEvent().toString().contains("Shared Data Waiter received message to go off work!"));
			assertEquals("Reg Waiter should have event log.", 1, regWaiter.log.size());
			assertTrue(regWaiter.log.getLastLoggedEvent().toString().contains("Waiter received message that it's time to go home!"));
			
			// Invokes scheduler to make sure that it returns false
			assertFalse(host.pickAndExecuteAnAction());
		}
}
