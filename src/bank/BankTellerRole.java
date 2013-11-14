package bank;

import agent.*;

/**
 * class BankTellerRole
 * This role is the bank teller, who manages requests from people to access and 
 * change their current bank account info
 * @author Joseph
 *
 */
public class BankTellerRole extends Role {

	protected boolean pickAndExecuteAnAction(){
		return false;
	}
	
	
	class Task{
		String type;
		int amount;
		int balance;
		int accountNumber;
		taskState ts;
		Task(String type, int amount, int accountNumber){
			this.type = type;
			this.amount = amount;
			this.accountNumber = accountNumber;
		}
	}
	enum taskState {requested, waiting, completed}
}
