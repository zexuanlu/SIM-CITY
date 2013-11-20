package bank.interfaces;

import agent.*;

public interface TimeCard {
	void msgBackToWork(Role role);
	
	void msgOffWork(Role role);
	
	void goBackToWork(MyRole role);
	
	void goOffWork(MyRole role);
	
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
