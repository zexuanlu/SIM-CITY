package bank.interfaces;

import agent.*;

public interface TimeCard {
	void msgBackToWork(Role role);
	
	void msgOffWork(Role role);
	
	void goBackToWork(Role role);
	
	void goOffWork(Role role);
}
