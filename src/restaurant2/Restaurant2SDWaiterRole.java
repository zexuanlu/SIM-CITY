package restaurant2;

import java.util.ConcurrentModificationException;

import person.interfaces.Person;

public class Restaurant2SDWaiterRole extends Restaurant2AbstractWaiterRole{
	public boolean offWork = false; 
	public Restaurant2RevolvingStand revolver;

	public Restaurant2SDWaiterRole(String name, Person p) {
		super(name, p);
	}

	public void msgGoOffWork(){
		offWork = true; 
		stateChanged(); 
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
			
			if (offWork){
				goOffWork();
				return true; 
			}
		}
		catch(ConcurrentModificationException e){
			return true;
		}
		return false;	
	}
	
	private void goOffWork(){
		offWork = false; 
		cashier.msgGoOffWork();
		cook.msgGoOffWork(); 
		
		this.person.msgGoOffWork(this, 0);
	}

	public void ordersToCook(){
		waiterGui.DoTakeToCook(); 
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		//System.err.println("FREE AT LAST");
		synchronized(MyCustomers){
			for(MyCustomer mc : MyCustomers){
				if(mc.getOrder() != "none"){
					revolver.insert(new Restaurant2Order(this, mc.getCustomer(), mc.getOrder()));
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
