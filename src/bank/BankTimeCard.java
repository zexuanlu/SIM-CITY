package bank;

import java.util.*;
import agent.*;
import bank.interfaces.*;

public class BankTimeCard extends Agent implements TimeCard {

	//Data
	List<MyTeller> tellers;
	MyHost mh;
	List<MyRole> roles;
	
	public void msgBackToWork(/*Person person,*/ Role role) {
		if(role instanceof BankTeller){
			for(MyTeller mt : tellers){
				if(mt.bt == role){
					roles.add(new MyRole(/*person,*/ role, roleState.replacement));
					stateChanged();
					return;
				}
			}
			roles.add(new MyRole(/*person,*/ role, roleState.newWorker));
			stateChanged();
			return;
		}
		else if(role instanceof BankHost){
			if(mh.bh == role){
				roles.add(new MyRole(/*person,*/ role, roleState.replacement));
				stateChanged();
				return;
			}
			roles.add(new MyRole(/*person,*/ role, roleState.newWorker));
			stateChanged();
			return;
		}
	}

	public void msgOffWork(Role role) {
		// TODO Auto-generated method stub

	}

	public boolean pickAndExecuteAnAction(){
		return false;
	}
	
	public void goBackToWork(Role role) {
		// TODO Auto-generated method stub

	}

	public void goOffWork(Role role) {
		// TODO Auto-generated method stub

	}
	
	class MyTeller{
		BankTeller bt;
		boolean isWorking;
		MyTeller(BankTeller bt){
			this.bt = bt;
		}
	}

	class MyHost{
		BankHost bh;
		boolean isWorking;
		MyHost(BankHost bh){
			this.bh = bh;
		}
	}
	
	class MyRole{
		Role role;
		roleState rs;
		MyRole(Role role, roleState rs){
			this.role = role;
			this.rs = rs;
		}
	}
	enum roleState {replacement, newWorker}
}
