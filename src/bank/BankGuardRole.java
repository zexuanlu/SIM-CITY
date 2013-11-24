package bank;

import agent.*;
import java.util.*;
import bank.interfaces.*;

public class BankGuardRole extends Role implements BankGuard {
	
	//Data
	List<Robbery> robberies = new ArrayList<Robbery>();
	
	
	public void msgHelpMe(BankTeller bt, BankCustomer bc, String location) {
		robberies.add(new Robbery(bt, bc, location));
		stateChanged();
	}

	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}
	
	class Robbery{
		BankTeller bt;
		BankCustomer bc;
		state rs;
		String location;
		Robbery(BankTeller bt, BankCustomer bc, String location){
			this.bt = bt;
			this.bc = bc;
			this.location = location;
		}
	}
	enum state {requested, scaredOff, finished}
}
