package person;

import person.interfaces.BankCustomer;
import agent.Role;

public class BankCustomerRole extends Role implements BankCustomer{

	protected BankCustomerRole(String n, PersonAgent p) {
		super(n, p);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
