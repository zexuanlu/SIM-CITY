package person;

import agent.Role;
import person.interfaces.BankHost;

public class BankHostRole extends Role implements BankHost{

	public BankHostRole(String n, PersonAgent p) {
		super(n, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void msgGoToBank(String task, int amount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgBackToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
