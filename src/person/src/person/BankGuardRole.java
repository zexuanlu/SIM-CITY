package person;

import person.interfaces.BankGuard;
import agent.Role;

public class BankGuardRole extends Role implements BankGuard{

	protected BankGuardRole(String n, PersonAgent p) {
		super(n, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void msgBackToWork() {
		// TODO Auto-generated method stub
		
	}

}
