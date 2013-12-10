package bank;

import agent.*;
import person.interfaces.*;

import java.util.*;

import bank.interfaces.*;

/**
 * This is an incomplete class that will be used when robberies are implemented in v2
 * @author Joseph
 *
 */
public class BankGuardRole extends Role implements BankGuard {
	
	//Data
	String name;
	List<Robbery> robberies = new ArrayList<Robbery>();
	
	BankGuardRole(Person person, String name){
		super(person);
		roleName = "Bank Guard";
	}
	
	
	public void msgHelpMe(BankTeller bt, BankCustomer bc, String location) {
		robberies.add(new Robbery(bt, bc, location));
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}
	
	//Utilities
	public String toString(){
		return name;
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
	
	public String getRoleName(){
		return roleName;
	}
	
	public utilities.Gui getGui(){
		return null; 
	}
	
}
