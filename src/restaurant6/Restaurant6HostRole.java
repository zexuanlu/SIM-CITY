package restaurant6;

import agent.Agent;   
import agent.Role;

import java.util.*;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;
import restaurant6.interfaces.Restaurant6Waiter;
import restaurant6.test.mock.EventLog;
import restaurant6.test.mock.LoggedEvent;
import utilities.restaurant.RestaurantHost;


/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class Restaurant6HostRole extends Role implements RestaurantHost {
	
		// Log for JUnit testing
		public EventLog log = new EventLog();
        
        // A global for the number of tables
        static final int NTABLES = 3;
        
        // Creates a collection of tables
        public Collection<Restaurant6Table> tables;
        
        // Boolean to determine off work
        public boolean offWork = false;
        
        // To contain how much money is paid
        public double restPay = 1000;         

        private String name; 
        
        /* To initialize variables that will later be used to find waiter with
         * the minimum amount of customers 
         */
        private int minCustomers = 100;
        private int numOccupied = 0; // To keep a counter of the number of tables occupied
        private boolean waiterRequestBreak;
        private MyWaiter waiterIndex;
        
        // Creates a class of MyWaiter
        private static class MyWaiter {
            public Restaurant6Waiter waiter;
            public String name;
            public boolean allowedToGoOnBreak;
            public int numCustomers = 0;
            public boolean isAtFront;
            public List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
            
            MyWaiter(Restaurant6Waiter w, String n, boolean b) {
                waiter = w;
                name = n;
                allowedToGoOnBreak = b;
                //isAtFront = false;
            }
        }
        
        // Creates a list of waiters that the host can access
        public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
        
        // Class of MyCustomer
        private static class MyCustomer {
            public Restaurant6CustomerRole customer;
            public String name;
            public int tableNum;
            public Restaurant6Waiter waiter;
            public enum CustomerState {WaitingFull, Deciding, WantsToStay, WillBeAssigned, DontWantToStay}
            public CustomerState custState; 
    
            // Constructor for MyCustomer
            MyCustomer(Restaurant6CustomerRole c, String n) {
                customer = c;
                name = n;
                custState = CustomerState.WaitingFull;
            }
        }
        
        // A list of customers in the waiting area
        public List<MyCustomer> waitingCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
        
        // A list of customers who just arrived
        public List<MyCustomer> arrivedCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
        
        // To see if customer is at the front
        private Semaphore customerAtFront = new Semaphore(0,true);
        
        // Constructor for the host agent
        public Restaurant6HostRole(String name, Person p) {
            super(p);

            this.name = name;
            int x = 200;
            int y = 50;
            
            // Makes some tables
            tables = Collections.synchronizedList(new ArrayList<Restaurant6Table>(NTABLES));
            
            for (int ix = 1; ix <= NTABLES; ix++) {
                    tables.add(new Restaurant6Table(ix));//how you add to a collections
            }
            
            // Sets table position for each table
            for (Restaurant6Table table: tables) {
                    table.setXPos(x);
                    table.setYPos(y);
                    x = x + 200;
            }
        }

        public String getMaitreDName() {
            return name;
        }

        public String getName() {
            return name;
        }

        public List getWaitingCustomers() {
            return waitingCustomers;
        }

        public Collection getTables() {
            return tables;
        }
        
        public List getWaiters() {
            return waiters;
        }
        
        // Messages
        // Message from the timecard that it's time to go off work
        public void msgGoOffWork(Restaurant6HostRole h, double money) {
        	offWork = true;
        	restPay = money;
        	log.add(new LoggedEvent("It's the end of the day! Time to tell all my worker bees to go home."));
        	print("It's the end of the day! Time to tell all my worker bees to go home.");
        	stateChanged();
        }
        
        // Called by CustomerAgent to indicate hungry 
        public void msgIWantFood(Restaurant6CustomerRole cust) {
            arrivedCustomers.add(new MyCustomer(cust, cust.getCustomerName()));
            stateChanged();
        }
        
        // Message from the customer saying want to wait even though restaurant is full
        public void wantToStay(Restaurant6CustomerRole cust) {
            // Change customer status to WantsToStay
            for (MyCustomer c : arrivedCustomers) { //waitingCustomers) {
                if (c.customer == cust) {
                        c.custState = MyCustomer.CustomerState.WantsToStay;
                }
            }
            stateChanged();
        }        
        
        // Message from the customer saying don't want to wait when restaurant is full
        public void dontWantToStay(Restaurant6CustomerRole cust) {
                // Change customer status to DontWantToStay
                for (MyCustomer c : arrivedCustomers) {
                        if (c.customer == cust) {
                                c.custState = MyCustomer.CustomerState.DontWantToStay;
                        }
                }
                stateChanged();
        }
        
        // Hack to establish connection between WaiterAgent and HostAgent
        public void msgSetWaiter(Restaurant6Waiter wait) {
            waiters.add(new MyWaiter(wait, wait.getName(), false));
            stateChanged();
        }
        
        // Message from the waiter saying ready to pick up customer
        public void msgWaiterAtFront(Restaurant6Waiter waiter) {
            for (MyWaiter w : waiters) {
                    if (w.waiter == waiter){ 
                            w.isAtFront = true;
                    }
            }
            stateChanged();
        }
        
        // Message from the customer saying ready to pick up customer
        public void msgCustomerAtFront() {
            customerAtFront.release();
            stateChanged();
        }
        
        // Message from the waiter asking for break
        public void goOnBreakPlease(Restaurant6Waiter wait) {
            waiterRequestBreak = true;

            for (MyWaiter w : waiters) {
                if (w.waiter == wait) {
                    if (waiters.size() == 1) {
                            w.allowedToGoOnBreak = false;
                            stateChanged();
                    }
                    else {
                            w.allowedToGoOnBreak = true;
                            stateChanged();
                    }
                }
            }
        }
        
        // Message from the waiter saying that the table is clear so host can set unoccupied
        public void tableIsClear(int tableNum, Restaurant6Waiter wait) {
                // Sets the table as unoccupied
                for (Restaurant6Table table : tables) {
                    if (table.getTableNum() == tableNum) {
                            table.setUnoccupied();
                            --numOccupied;
                            print("Table is clear");
                            break;
                    }
                }
                // Removes customer from the waiter
                for (MyWaiter w : waiters) {
                    if (w.waiter == wait) {
                            --w.numCustomers;
                            print("Removed customer from waiter");
                            break;
                    }
                }
                stateChanged();
        }

        /**
         * Scheduler.  Determine what action is called for, and do it.
         */
        public boolean pickAndExecuteAnAction() {
                /* If there is a table that is unoccupied, the scheduler will look
                 * for the waiter with the fewest number of customers and assign the 
                 * waiting customer to that waiter.
                 */
                if (waiterRequestBreak) {
                        for (MyWaiter w : waiters) {
                                if (w.allowedToGoOnBreak) {
                                        stopAssigningWaiter(w);
                                        return true;
                                }
                        }
                        for (MyWaiter w : waiters) {
                                if (!w.allowedToGoOnBreak) {
                                        tellWaiterNoBreak(w);
                                        return true;
                                }
                        }
                }
                
                for (MyWaiter w : waiters) {
                        if (w.isAtFront) {
                                tellCustomerComeToFront(w.customers.get(0));
                                return true;
                        }
                }
                
                // If customer wants to stay, tell the customer that they will be assigned
                for (MyCustomer c : arrivedCustomers) { // waitingCustomers) {
                        if (c.custState == MyCustomer.CustomerState.WantsToStay) {
                                willAssignCustomer(c); //waitingCustomers.get(0));
                                return true;
                        }
                }
                
                // If customer is waiting and the restaurant is full, tell customer it is full
                for (MyCustomer c : arrivedCustomers) { //waitingCustomers) {
                        if (c.custState == MyCustomer.CustomerState.WaitingFull && numOccupied >= 3) {
                                tellCustomerRestaurantFull(c); //waitingCustomers.get(0));
                                return true;
                        }
                }
                
                // If customer doesn't want to stay, remove the customer from the list
                for (MyCustomer c : arrivedCustomers) {
                        if (c.custState == MyCustomer.CustomerState.DontWantToStay) {
                                removeCustomerFromList(c);
                                return true;
                        }
                }
                
                // If there are customers who arrived, tell them to go to the waiting area
                if (!arrivedCustomers.isEmpty()) {
                        if (numOccupied < 3) {
                                ++numOccupied;
                                tellCustomerToWait(arrivedCustomers.get(0));
                                return true;
                        }
                }
                
                /*
                 *  If the customer is ready to be seated and the number of tables occupied is
                 *  less than 3, tell the waiter to seat the customer 
                 */
                if (!waitingCustomers.isEmpty()) {        
                        if (!waiters.isEmpty()) {
                                        for (Restaurant6Table table : tables) {
                                                if (!table.isOccupied()) {
                                                        for (MyWaiter w : waiters) {
                                                                print("Checking waiter " + w.name);
                                                                print("Number of customers for " + w.name + " is " + w.numCustomers);
                                                                if (w.numCustomers <= minCustomers) {
                                                                        minCustomers = w.numCustomers;
                                                                        waiterIndex = w;
                                                                }
                                                        }
                                                        tellWaiterToSeat(waiterIndex.waiter, waitingCustomers.get(0), table);
                                                        return true;
                                                        }
                                                }
                                        }
                                }
                /*
                 * If it's the end of the day and there are no more customer's, the host wil message
                 * all workers saying it's time to go off work.
                 */
                if (waitingCustomers.isEmpty() && offWork) {
                	endWorkDay();
                	return true;
                }
                        
                return false;
                //we have tried all our rules and found
                //nothing to do. So return false to main loop of abstract agent
                //and wait.
        }

        // Actions   
        // Tells all of the host's current waiters to go off work
        private void endWorkDay() {
        	for (MyWaiter w : waiters) {
        		w.waiter.msgEndOfDay(restPay);
        	}
        	
        	this.person.msgGoOffWork(this, restPay);
        	offWork = false;
        }
        
        // Tells the customer that the restaurant is full
        private void tellCustomerRestaurantFull(MyCustomer cust) {
            print("I'm sorry, our restaurant is full.");
            cust.customer.msgCustomerFull();
            cust.custState = MyCustomer.CustomerState.Deciding;
        }
        
        // Tells the customer that will be assigned to table
        private void willAssignCustomer(MyCustomer cust) {
            print("I will assign you to a table soon.");
            cust.custState = MyCustomer.CustomerState.WillBeAssigned;
            cust.customer.msgPleaseHaveASeat();
            arrivedCustomers.remove(cust);
            waitingCustomers.add(new MyCustomer(cust.customer, cust.name));
        }
        
        // Removes customer from the list
        private void removeCustomerFromList(MyCustomer cust) {
            arrivedCustomers.remove(cust);
        }
        
        // Tells the customer to have a seat at the waiting area
        private void tellCustomerToWait(MyCustomer cust) {
            print("Please have a seat in the waiting area.");
            cust.customer.msgPleaseHaveASeat();
            arrivedCustomers.remove(cust);
            waitingCustomers.add(new MyCustomer(cust.customer, cust.name));
        }
        
        /*
         * Tells the waiter to come to the front to seat the customer at an unoccupied table and sets table
         * as occupied by the customer.
         */
        private void tellWaiterToSeat(Restaurant6Waiter waiter, MyCustomer cust, Restaurant6Table t) {
            for (MyWaiter w : waiters) {
                if (w.waiter == waiter) {
                        w.customers.add(cust);
                        cust.waiter = w.waiter;
                        cust.tableNum = t.getTableNum();
                        w.numCustomers++;
                        break;
                }
            }
            
            // Tells waiter to come to the pickup spot
            t.setOccupant(cust.customer);
            waiter.seatAtTable(cust.customer, t.getTableNum());
            waitingCustomers.remove(cust);
            
            print("I assigned customer " + cust.name + " to " + waiter.getName());
        }
        
        private void tellCustomerComeToFront(MyCustomer cust) {
                
            for (MyWaiter w : waiters) {
                if (cust.waiter == w.waiter) {
                        w.customers.remove(cust);
                        w.isAtFront = false;
                }
            }
            
            cust.customer.msgReadyToBeSeated();
            
            try {
                    customerAtFront.acquire();
            } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
            }
            
            cust.waiter.msgCustomerAtFront();
        }
        
        private void tellWaiterNoBreak(MyWaiter waiter) {
            waiterRequestBreak = false;
            print("Not letting waiter " + waiter.name + " go on break");
            
            for (MyWaiter w : waiters) {
                    if (w.waiter == waiter.waiter) {
                            w.waiter.goOnBreak(w.allowedToGoOnBreak); // Sends the reply of the host, dependent on if there is only one waiter
                    }
            }
        }
        
        private void stopAssigningWaiter(MyWaiter waiter) {
            waiterRequestBreak = false;
            print("Letting waiter " + waiter.name + " go on break");
            
            for (MyWaiter w : waiters) {
                    if (w.waiter == waiter.waiter) {
                            w.waiter.goOnBreak(w.allowedToGoOnBreak); // Sends the reply of the host, dependent on if there is only one waiter
                    }
            }
            
            waiters.remove(waiter);
            
            int tempMin = 100; // To find the minimum number of customers after the waiter on break has been removed
            
            for (MyWaiter w : waiters) {
                    if (w.numCustomers <= tempMin) {
                            tempMin = w.numCustomers;
                    }
            }
            
            minCustomers = tempMin;
        }

        public utilities.Gui getGui(){
        	return null; 
        }
        
		public String getRoleName() {
			return "Restaurant 6 Host";
		}
}