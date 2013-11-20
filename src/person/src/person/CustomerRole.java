package person;

import agent.Role;
import person.interfaces.Customer;

public class CustomerRole extends Role implements Customer{
	
	public CustomerRole(String n, PersonAgent p) {
		super(n, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void gotHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgConfirmed(HostRole hr) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		System.out.println("Killer, im runing as a role");
		return false;
	}

}
