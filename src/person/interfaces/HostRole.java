package person.interfaces;

import person.PersonAgent;
import person.interfaces.Host;
import agent.Role;

public class HostRole extends Role implements Host{

	public HostRole(String n, PersonAgent p) {
		super(p);
		// TODO Auto-generated constructor stub
	}
	
	public String getName(){
		return this.getName();
	}
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getRoleName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgIWantFood(PersonAgent p, CustomerRole cRole) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgClockIn(PersonAgent p, HostRole hRole) {
		// TODO Auto-generated method stub
		
	}

}
