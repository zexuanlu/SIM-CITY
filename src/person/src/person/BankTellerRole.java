package person;

import agent.Role;
import person.interfaces.BankTeller;

public class BankTellerRole extends Role implements BankTeller{

	protected BankTellerRole(String n, PersonAgent p) {
		super(n, p);
		// TODO Auto-generated constructor stub
	}

	public void msgBackToWork() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
