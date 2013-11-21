package market;

import java.util.*;
import agent.*;
import marketinterface.*;

public class MarketTimeCard extends Agent implements TimeCard{

	List<MyEmployee> employee;
	MyCashier cashier;
	List<MyRole> roles;
	

	public void msgBackToWork(Role role) {
		if(role instanceof Employee){
			for(MyEmployee me : employee){
				if(me.e == role){
					roles.add(new MyRole(/*person,*/ role, roleState.replacement));
					stateChanged();
					return;
				}
			}
			roles.add(new MyRole(/*person,*/ role, roleState.newWorker));
			stateChanged();
			return;
		}
		else if(role instanceof Cashier){
			if(cashier.c == role){
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



	protected boolean pickAndExecuteAnAction() {
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
		//role.role.msgDoneWithWork();
		role.rs = roleState.waiting;
	}
	
	class MyEmployee{
		Employee e;
		boolean isWorking;
		MyEmployee(Employee e){
			this.e = e;
		}
	}

	class MyCashier{
		Cashier c;
		boolean isWorking;
		MyCashier(Cashier c){
			this.c = c;
		}
	}
}
