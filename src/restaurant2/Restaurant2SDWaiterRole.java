package restaurant2;

import person.interfaces.Person;

public class Restaurant2SDWaiterRole extends Restaurant2AbstractWaiterRole{

	Restaurant2RevolvingStand revolver = new Restaurant2RevolvingStand();
	
	public Restaurant2SDWaiterRole(String name, Person p) {
		super(name, p);
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		return false;
	}
	
	public void ordersToCook(){
        waiterGui.DoTakeToCook(); 
        
        try {
                atCook.acquire();
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
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
