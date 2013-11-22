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
		for(MyRole mr : roles){
			if(mr.role == role){
				mr.rs = roleState.newWorker;
				stateChanged();
				return;
			}
		}
	}

	public boolean pickAndExecuteAnAction(){
		for(MyRole r : roles){
			if(r.rs == roleState.newWorker){
				goBackToWork(r);
				return true;
			}
		}
		for(MyRole r : roles){
			if(r.rs == roleState.replacement){
				goOffWork(r);
				return true;
			}
		}
		return false;
	}
	
	public void goBackToWork(MyRole role) {
		//role.person.readyToWork(role.role);
		roles.remove(role);
	}

	public void goOffWork(MyRole role) {
		//double pay = 500.00;
		//role.role.msgDoneWithWork(pay);
		role.rs = roleState.waiting;
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
}
