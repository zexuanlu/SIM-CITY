package restaurant6;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;

import person.interfaces.Person;
import restaurant6.Restaurant6Order.OrderState;
import restaurant6.interfaces.Restaurant6Waiter;

public class Restaurant6SDWaiterRole extends Restaurant6AbstractWaiterRole implements Restaurant6Waiter {
	
	private Restaurant6RevolvingStand revolvingStand;
	
	// Collection of tables
    public Collection<Restaurant6Table> tables;
	
	public Restaurant6SDWaiterRole(String name, Person p) {
		super(name, p);

		this.name = name;
		int x = 200;
		int y = 50;
		
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
		
		waiterState = MyWaiterState.Working;
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {			
		if (waiterState == MyWaiterState.Working) {
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
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	// Goes to cook to put in order
	protected void goToCook(MyCustomer customer) {		
		waiterGui.DoGoToCook(); // GUI goes to cook
		
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		customer.setState(MyCustomer.CustState.WaitingForFood);
		revolvingStand.insert(customer.order);
		waiterGui.DoGoToHomePosition(); // GUI leaves cook
	}

	// Gets person's role name
	public String getRoleName() {
		return "Restaurant 6 Shared Data Waiter";
	}
}
