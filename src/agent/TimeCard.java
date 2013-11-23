package agent;

import java.util.List;

public class TimeCard extends Agent {
	List<MyRole> myRoles;
	List<Role> roles;
	
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
	
	public boolean pickAndExecuteAnAction(){
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
	
	void goBackToWork(MyRole role){
		//role.person.readyToWork(role.role);
		myRoles.remove(role);
		roles.add(role.role);
	}
	
	void goOffWork(MyRole role){
		//double pay = 500.00;
		//role.person.msgDoneWithWork(role.role, pay);
		//role.role.changePerson(Person person);
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
	enum roleState {replacement, waiting, newWorker}
}
