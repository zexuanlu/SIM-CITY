package restaurant6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import person.interfaces.Person;
import agent.Agent;
import agent.Role;
import restaurant6.Restaurant6Order;
import restaurant6.Restaurant6Order.OrderState;
import restaurant6.gui.Restaurant6WaiterGui;
import restaurant6.interfaces.*;

public abstract class Restaurant6AbstractWaiterRole extends Role implements Restaurant6Waiter {

	protected List<MyCustomer> waiterCustomers = Collections.synchronizedList(new ArrayList<MyCustomer>());
    
    // List of checks
	protected List<Restaurant6Check> waiterChecks = Collections.synchronizedList(new ArrayList<Restaurant6Check>());
    
	public boolean offWork = false;
	public double myPay = 0; 
	protected Restaurant6HostRole host;
	protected Restaurant6CookRole cook;
	protected Restaurant6Cashier cashier;
	protected String custChoice;
	protected boolean askForBreak; // When the waiter wants to go on break
	protected boolean onBreak; // Waiter is on break
	protected boolean guiSelectBreak; 
	protected boolean hostResponded;
	protected boolean hostReply;
	protected boolean willGoOnBreak;        // Waiter has received approval from host to go on break
	protected boolean goingOnBreak; // Waiter is going on break
	protected boolean goToFront;
    
	protected String menuWithoutChoice; 
    Timer breakTimer = new Timer();
    
    public boolean getRequestedBreak() {
            return askForBreak;
    }
    
    public boolean getHostAnswer() {
            return willGoOnBreak;
    }
    
    public boolean getOnBreak() {
            return goingOnBreak;
    }
    
    public boolean getGuiBreak() {
            return guiSelectBreak;
    }
    
    public enum MyWaiterState {RequestBreak, ApprovedBreak, GoingOnBreak, OnBreak, Working};
    protected MyWaiterState waiterState;
    
    // A global variable for the number of tables
    protected static final int NTABLES = 3;
    
    // Collection of tables
    public Collection<Restaurant6Table> tables;

    protected String name;
    protected Semaphore atTable = new Semaphore(0,true);
    protected Semaphore atCook = new Semaphore(0,true);
    protected Semaphore atCashier = new Semaphore(0,true);
    protected Semaphore atFront = new Semaphore(0,true); // So the waiter can pick the customer up from the waiting area
    protected Semaphore atPlate = new Semaphore(0,true); // So the waiter can pick the customer up from the waiting area
    protected Semaphore canSeat = new Semaphore(0,true);
    
    public Restaurant6WaiterGui waiterGui = null;

    public Restaurant6AbstractWaiterRole (String name, Person p) {
        super(p);

        this.name = name;
        int x = 30;
        int y = 360;
        
        // Make some tables
        tables = Collections.synchronizedList(new ArrayList<Restaurant6Table>(NTABLES));
        
        for (int ix = 1; ix <= NTABLES; ix++) {
            tables.add(new Restaurant6Table(ix));//how you add to a collections
        }
        
        // Set table positions for GUI
        for (Restaurant6Table table: tables){
            table.setXPos(x);
            table.setYPos(y);
            x = x + 200;
        }
        
        onBreak = false;
        
        waiterState = MyWaiterState.Working;
    }
    
    // Used by the host to determine number of customers each waiter has
    public int returnWaiterCust() {
        return waiterCustomers.size();
    }
    
    // Hack to establish connection to Cook agent
    public void setCook(Restaurant6CookRole cook) {
            this.cook = cook;
    }

    // Hack to establish connection to Host agent
    public void setHost(Restaurant6HostRole host) {
        this.host = host;
    }
    
    // Hack to establish connection to Cashier agent
    public void setCashier(Restaurant6CashierRole c) {
        this.cashier = c;
    }
    
    // Inner class of MyCustomer so the waiter has a list of all of its customers
    protected static class MyCustomer {
        public Restaurant6Customer cust;
        public int tableNum;
        public String choice;
        public Restaurant6Order order;
        public enum CustState {None, Waiting, Seated, ReadyToOrder, LeavingCannotAfford, Ordering, NeedToReorder, Ordered, WaitingForFood, Served, Leaving};
        private CustState myCustState;
        
        // Constructor for MyCustomer
        MyCustomer(Restaurant6Customer c, int tNum) {
            cust = c;
            tableNum = tNum;
            myCustState = CustState.Waiting;
            order = null;
        }
        
        // Returns the state of MyCustomer
        public CustState getState() {
            return myCustState;
        }
        
        // Sets the state of MyCustomer
        public void setState (CustState state) {
            myCustState = state;
        }
    }
    
    // Finds the table number for a customer 
    public int findTableNum(Restaurant6Customer customer) {
        int custTableNumber;
        custTableNumber = -1;
        
        for (MyCustomer waitCust : waiterCustomers) {
            if (waitCust.cust == customer) {
                custTableNumber = waitCust.tableNum;
                break;
            }
        }
        
        return custTableNumber;
    }
    
    public String getCustChoice() {
        return custChoice;
    }
    
    public String getMaitreDName() {
        return name;
    }

    public String getName() {
        return name;
    }

    public List getCustomers() {
        return waiterCustomers;
    }

    public Collection getTables() {
        return tables;
    }
    
    // Messages
    // From the host to end the work day
    public void msgEndOfDay(double m) {
    	myPay = m;
    	offWork = true;
    	stateChanged();
    }
    
    // By the GUI to go on break
    public void msgOnBreak() {
		guiSelectBreak = true;
		// Waiter will ask for a break
		waiterState = MyWaiterState.RequestBreak;
		print("I want to go on break");
		stateChanged();
    }

    // The host's reply to waiter wanting to go on break
    public void goOnBreak(boolean b) {
		hostResponded = true;
		hostReply = b;
		
		// Waiter state will depend on the host's reply
		if (hostReply) {
		        waiterState = MyWaiterState.ApprovedBreak;
		}
		else {
		        waiterState = MyWaiterState.Working;
		}
		
		stateChanged();
    }
    
    // Message from checkbox indicating off break
    public void msgOffBreak() {
		host.msgSetWaiter(this);
		stateChanged();
    }
    
	// Waiter receives message from cook saying that there's no more food
	public void outOfFood(Restaurant6Order o) {
		print("Out of food message received");
		menuWithoutChoice = o.getOrder();
		
		for (MyCustomer c : waiterCustomers) {
			if (c.tableNum == o.getTableNum())
				c.setState(MyCustomer.CustState.NeedToReorder);
		}
		
		stateChanged();
	}
	
	// Called by host for waiter to seat customer
	public void seatAtTable(Restaurant6Customer cust, int tn) {
		waiterCustomers.add(new MyCustomer(cust, tn));
		stateChanged();
	}

	// Called by customer to indicate ready to order
	public void readyToOrder(Restaurant6Customer c) {
		for (MyCustomer customer : waiterCustomers) {
			if (customer.cust == c) {
				customer.setState(MyCustomer.CustState.ReadyToOrder);
				stateChanged();
			}
		}
	}
	
	// Called by the customer to indicate cannot afford anything
	public void cannotAffordAnything(Restaurant6Customer c) {
		for (MyCustomer customer : waiterCustomers) {
			if (customer.cust == c) {
				customer.setState(MyCustomer.CustState.LeavingCannotAfford);
				stateChanged();
			}
		}
	}
	
	// Called by customer to give waiter order
	public void hereIsMyOrder(String myChoice, Restaurant6Customer c) {
		for (MyCustomer customer : waiterCustomers) {
			if (customer.cust == c) {
		  		customer.choice = myChoice;
		  		Restaurant6Order o = new Restaurant6Order(customer.choice, customer.tableNum, this);
				customer.order = o;
		  		customer.setState(MyCustomer.CustState.Ordered);
		  		stateChanged();
			}
		}	
	}
	
	// Called by the cashier to give waiter the check
	public void msgHereIsTheCheck(Restaurant6Check c) {
		waiterChecks.add(c);
		stateChanged();
	}
	
	// Called by cook to have waiter serve customer order
	public void orderIsReady(Restaurant6Order o) { 	
		for (MyCustomer customer : waiterCustomers) {
			if (customer.tableNum == o.getTableNum()) {
				customer.order.setOrderStatus(OrderState.Cooked);
				print("Added to my list of orders to be served");
				stateChanged();
			}
		}
	}
	
	// Called by customer to let waiter know to clear table
	public void doneEatingAndLeaving(Restaurant6Customer c) {
		for (MyCustomer customer : waiterCustomers) {
			if (customer.cust == c) {
				customer.setState(MyCustomer.CustState.Leaving);
			}
		}
		
		if (waiterState == MyWaiterState.ApprovedBreak && waiterCustomers.size() == 1) { // If the waiter is going on break, start break after finishing all customers
			waiterState = MyWaiterState.GoingOnBreak;
		}
		
		stateChanged();
	}

	// When waiter GUI has reached the customer's table
	public void msgAtTable() {//from animation
		print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	// When waiter GUI has reached cook
	public void msgAtCook() {
		print("msgAtCook() called");
		atCook.release();
		stateChanged();
	}
	
	// When waiter GUI has reached cashier
	public void msgAtCashier() {
		print("msgAtCashier() called");
		atCashier.release();
		stateChanged();
	}
	
	// When waiter GUI has reached the front to pick up the waiting customer
	public void msgAtFront() {
		atFront.release();
		stateChanged();
	}
	
	// When the waiter GUI has reached the cook's plating area to pick up the food
	public void msgAtPlate() {
		print("msgAtPlate() called");
		atPlate.release();
		stateChanged();
	}
	
	// When the host tells the waiter that the customer is at the front
	public void msgCustomerAtFront() {
		canSeat.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {			
		try {
            if (waiterState == MyWaiterState.RequestBreak) {
                askHostForBreak();
                return true;
            }
	    }
	    catch (ConcurrentModificationException e) {
	            return false;
	    }
	
	    try {
            if (hostResponded) {
                setCheckboxChecked(hostReply);
                return true;
            }
	    } catch (ConcurrentModificationException e) {
            	return false;
	    }
	    
	    try {
            if (waiterState == MyWaiterState.GoingOnBreak) {
                startBreak();
                return true;
            }
	    } catch (ConcurrentModificationException e) {
	            return false;
	    }
		if (waiterState == MyWaiterState.Working || waiterState == MyWaiterState.ApprovedBreak) {
			try {
				if (!waiterChecks.isEmpty()) {
					giveCustomerBill(waiterChecks.get(0));
					return true;
				}
			} catch (ConcurrentModificationException e) {
				return false;
			}
			if (!waiterCustomers.isEmpty()) {
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.Waiting) {
							seatCustomer(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.ReadyToOrder) {
							goToCustomer(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.LeavingCannotAfford) {
							leaveTable(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.Ordered) {
							goToCook(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.NeedToReorder) {
							getNewOrder(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.WaitingForFood && customer.order.getOrderStatus() == OrderState.Cooked) {
							print("Ready to serve order");
							serveOrder(customer.order);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
				try {
					for (MyCustomer customer : waiterCustomers) {
						if (customer.getState() == MyCustomer.CustState.Leaving) {
							clearTable(customer);
							return true;
						}
					}
				} catch (ConcurrentModificationException e) {
					return false;
				}
						return true;
			}	
			if (offWork) {
				goHome();
				return true;
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	// When the host tells the waiter that it's time to go home
	private void goHome() {
		print("Off for the day!");
		this.person.msgGoOffWork(this, myPay);
		cook.msgOffWork();
		cashier.msgOffWork();
		offWork = false;
	}
	
	// Asks for the break
    protected void askHostForBreak() {
        waiterGui.DoCheckBreak(true, this);
        print("Can I go on break?");
        host.goOnBreakPlease(this);
        waiterState = MyWaiterState.Working;
    }
    
    // Action to set the checkbox as the host's response
    protected void setCheckboxChecked(boolean p) {                
        if (p) {
            waiterGui.DoCheckBreak(p, this);
        }
        else if (!p) {
            waiterGui.DoUncheckBreak(this);
        }
        hostResponded = false;
    }
    
    // Goes on break - timer for now
    protected void startBreak() {
        waiterState = MyWaiterState.OnBreak;
        final Restaurant6AbstractWaiterRole w = this;
        print("On break..");
        
        breakTimer.schedule(new TimerTask() {
            public void run() {
                print("Done with break. Going back to work!");
                waiterState = MyWaiterState.Working;
                waiterGui.DoUncheckBreak(w);
                host.msgSetWaiter(w);
                stateChanged();
            }
        }, 5000); 
    }
    
	// Seats the customer at the table assigned
	protected void seatCustomer(MyCustomer customer) {
		waiterGui.DoGoToFront();
		
		try {
			atFront.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		host.msgWaiterAtFront(this);
		
		try {
			canSeat.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		customer.setState(MyCustomer.CustState.Seated);
		
		DoSeatCustomer(customer); // For the GUI
		
		customer.cust.followMe(customer.tableNum, new Restaurant6FoodMenu(), this); // Message to customer to follow waiter
		
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		waiterGui.DoGoToHomePosition(); // For the GUI to leave
	}
	
	// Takes the customer's order
	protected void goToCustomer(MyCustomer customer) {
		for (MyCustomer waitingCust : waiterCustomers) { // Sets myCustomer's state
			if (waitingCust.cust == customer.cust)
				waitingCust.myCustState = MyCustomer.CustState.Ordering;
		}
		
		DoGoToCustomer(customer.cust); // For the GUI to go to the customer		
		
		
	
		customer.cust.whatWouldYouLike();	// Message to ask customer for choice
		
		// Semaphore to make sure waiter is at table
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	// Leaves the table if the customer cannot afford anything
	protected void leaveTable(MyCustomer customer) {
		waiterGui.DoGoToHomePosition();
		customer.setState(MyCustomer.CustState.None);
		host.tableIsClear(customer.tableNum, this);
		waiterCustomers.remove(customer);
	}
	
	// Gives the bill to the customer
	protected void giveCustomerBill(Restaurant6Check check) {		
		print("Going to deliver check to " + check.getCustomer());
		DoGoToCustomer(check.getCustomer());
		
		// Semaphore to make sure waiter is at table
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Send message to the customer giving the check
		check.getCustomer().hereIsYourCheck(check);
		
		waiterGui.DoGoToHomePosition();
		
		// Remove the check from the list. Waiter doesn't need it anymore
		waiterChecks.remove(check);
	}

	// Gets the customer's new order if restaurant is out of the customer's choice 
	protected void getNewOrder(MyCustomer customer) {
		//waiterState = MyWaiterState.TakingOrder;
		
		for (MyCustomer waitingCust : waiterCustomers) {
			if (waitingCust.cust == customer.cust)
				waitingCust.myCustState = MyCustomer.CustState.Ordering;
		}
		
		DoGoToCustomer(customer.cust);
		
		// Semaphore to make sure waiter is at table
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customer.cust.pleaseOrderSomethingNew(menuWithoutChoice);
	}
	
	// Goes to cook to put in order
	// CHANGED FOR SHARED DATA
	protected abstract void goToCook(MyCustomer customer);
	
	// Serves the order to the customer
	protected void serveOrder(Restaurant6Order o) {
		custChoice = o.getOrder();
	
		// Sets the order's status to served for myCustomer
		for (MyCustomer customer : waiterCustomers) {
			if (customer.order == o) {
				customer.order.setOrderStatus(OrderState.Served);
			}
		}
		
		waiterGui.DoGoToPlate(); // GUI goes to cook
		
		try {
			atPlate.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		waiterGui.setDeliveringFood(true); // For the food icon to show up
		
		waiterGui.DoGoToTable(o.getTableNum()); // GUI goes to customer
		
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create temporary customer to hold customer information 
		Restaurant6Customer tempCust = null;
		
		for (MyCustomer customer : waiterCustomers) {
			if (customer.tableNum == o.getTableNum()) {
				customer.cust.hereIsYourFood(o.getOrder());
				customer.setState(MyCustomer.CustState.Served);
				tempCust = customer.cust;
				print(customer.order.getOrder() + " served");		
				break;
			}
		}
		
		waiterGui.setDeliveringFood(false);
		
		//waiterState = MyWaiterState.GettingCheck;
		waiterGui.DoGoToCashier(); 
		
		// Message cashier the customer's order
		cashier.pleaseComputeCheck(o.getOrder(), this, tempCust);
		
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// Waiter clears table by sending host message and removing customer from list
	protected void clearTable(MyCustomer customer) {
		customer.setState(MyCustomer.CustState.None);
		host.tableIsClear(customer.tableNum, this);
		waiterCustomers.remove(customer);
	}
	
	// The animation DoXYZ() routines
	protected void DoSeatCustomer(MyCustomer customer) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer.cust + " at " + customer.tableNum);
		waiterGui.DoGoToTable(customer.tableNum);
	}

	protected void DoGoToCustomer(Restaurant6Customer customer) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Going to " + customer);
		waiterGui.DoGoToTable(findTableNum(customer));
	}
	
	//utilities

	public void setGui(Restaurant6WaiterGui gui) {
		waiterGui = gui;
	}

	public Restaurant6WaiterGui getGui() {
		return waiterGui;
	}
}
