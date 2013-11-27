package person.interfaces;

import agent.Role;
import person.PersonAgent;
import person.interfaces.Customer;

public class CustomerRole extends Role implements Customer{
	
	public CustomerRole(String n, PersonAgent p) {
		super(p);
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
	public boolean pickAndExecuteAnAction() {
		System.out.println("Killer, im runing as a role");
		return false;
	}

	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}

}
