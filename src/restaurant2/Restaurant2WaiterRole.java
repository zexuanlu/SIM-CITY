package restaurant2;

import java.util.ConcurrentModificationException;

import person.interfaces.Person;
import restaurant2.Restaurant2AbstractWaiterRole.MyCustomer;
import restaurant2.Restaurant2AbstractWaiterRole.WaiterState;
import restaurant2.interfaces.Restaurant2Customer;

public class Restaurant2WaiterRole extends Restaurant2AbstractWaiterRole{

	public Restaurant2WaiterRole(String name, Person p) {
		super(name, p);
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		try{
			for(MyCustomer mc : MyCustomers)
			{
				if(mc.getState() == "toBeServed"){
					serveCustomers(mc);
					return true;
				}
				else if(mc.getState() == "waitingForCheck"){
					deliverCheck(mc);
					return true;
				}
				else if(mc.getState() == "served"){
					getCheck(mc);
					return true;
				}
				else if(mc.getState() == "readyToOrder"){
					takeOrder(mc);
					return true;
				}
				else if(mc.getState() == "reOrder"){
					returnForNewOrder(mc);
					return true;
				}
				else if(mc.getState() == "waitingForFood"){
					ordersToCook();
					return true;
				}
				else if(mc.getState() == "waitingToBeSeated" && state == WaiterState.backHome){
					seatCustomer(mc, mc.tableAt());
					return true;
				}
			}
		}
		catch(ConcurrentModificationException e){
			return true;
		}
		return false;	
	}

	public void ordersToCook(){

		DoTakeOrdersToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		synchronized(MyCustomers){
			for(MyCustomer mc : MyCustomers){
				if(mc.getOrder() != "none") {
					cook.msgOrderToCook(mc.getOrder(), this, (Restaurant2CustomerRole)mc.getCustomer());
					mc.changeState("ordered");
					mc.setOrder("none");
				}
			}
		}
	}

	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}
}
