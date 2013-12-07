package restaurant2;

import person.interfaces.Person;
import restaurant2.interfaces.Customer;

public class Restaurant2WaiterRole extends Restaurant2AbstractWaiterRole{

	public Restaurant2WaiterRole(String name, Person p) {
		super(name, p);
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
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
			for(MyCustomer mc : MyCustomers)
			{
				if(mc.getOrder() != "none") 
				{
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
