package utilities;

import java.util.*; 

import agent.Agent;
import agent.Role;
import person.interfaces.*;

public class TimeCard extends Agent {
	List<MyRole> myRoles = Collections.synchronizedList(new ArrayList<MyRole>());
	List<Role> roles = Collections.synchronizedList(new ArrayList<Role>());
	boolean endOfDay = false;
	
	public void msgBackToWork(Person person, Role role) {
		synchronized(roles){
			for(Role r : roles){
				if(r == role){
					myRoles.add(new MyRole(person, role, roleState.replacement));
					stateChanged();
					return;
				}
			}
		}
		myRoles.add(new MyRole(person, role, roleState.newWorker));
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
		synchronized(myRoles){
			for(MyRole r : myRoles){
				if(r.rs == roleState.newWorker){
					goBackToWork(r);
					return true;
				}
			}
		}
		synchronized(myRoles){
			for(MyRole r : myRoles){
				if(r.rs == roleState.replacement){
					goOffWork(r);
					return true;
				}
			}
		}
		return false;
	}
	
	private void endOfDay(){
	
		synchronized(roles){
			for(Role r : roles){
				r.msgEndOfDay();
			}
		}
		endOfDay = false;
	}
	
	private void goBackToWork(MyRole role){
		role.person.msgReadyToWork(role.role);
		myRoles.remove(role);
		roles.add(role.role);
	}
	
	private void goOffWork(MyRole role){
		double pay = 1000.00;
		role.role.getPerson().msgGoOffWork(role.role, pay);
		role.role.switchPerson(role.person);
		role.person.msgReadyToWork(role.role);
		myRoles.remove(role);
	}
	
	public class MyRole{
		public Role role;
		public roleState rs;
		public Person person;
		public MyRole(Person person, Role role, roleState rs){
			this.role = role;
			this.rs = rs;
			this.person = person;
		}
	}
	enum roleState {replacement, newWorker}
}
