package agent;

import java.util.*;

public class TimeCard extends Agent {
	List<MyRole> myRoles = new ArrayList<MyRole>();
	List<Role> roles = new ArrayList<Role>();
	boolean endOfDay = false;
	
	public void msgBackToWork(/*Person person,*/ Role role) {
		for(Role r : roles){
			if(r == role){
				myRoles.add(new MyRole(/*person,*/ role, roleState.replacement));
				stateChanged();
				return;
			}
		}
		myRoles.add(new MyRole(/*person,*/ role, roleState.newWorker));
		stateChanged();
		return;
	}
	
	public void msgEndOfDay(){
		endOfDay = true;
		stateChanged();
	}
	
	public boolean pickAndExecuteAnAction(){
		if(endOfDay){
			endOfDay();
			return true;
		}
		for(MyRole r : myRoles){
			if(r.rs == roleState.newWorker){
				goBackToWork(r);
				return true;
			}
		}
		for(MyRole r : myRoles){
			if(r.rs == roleState.replacement){
				goOffWork(r);
				return true;
			}
		}
		return false;
	}
	
	private void endOfDay(){
		for(Role r : roles){
			//r.getPerson().msgDoneWithWork(r, 500.00);
		}
		roles.clear();
	}
	
	private void goBackToWork(MyRole role){
		//role.person.readyToWork(role.role);
		myRoles.remove(role);
		roles.add(role.role);
	}
	
	private void goOffWork(MyRole role){
		//double pay = 500.00;
		//role.person.msgDoneWithWork(role.role, pay);
		//role.role.switchPerson(Person person);
		myRoles.remove(role);
	}
	
	public class MyRole{
		public Role role;
		public roleState rs;
		//public Person person;
		public MyRole(/*Person person,*/ Role role, roleState rs){
			this.role = role;
			this.rs = rs;
			//this.person = person;
		}
	}
	enum roleState {replacement, newWorker, endOfDay}
}
