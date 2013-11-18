package bank.gui;

import bank.*;

public class BankPanel {

	BankCustomerRole bc;
	BankTellerRole bt;
	BankDatabaseAgent bd;
	
	BankPanel(){
		bc = new BankCustomerRole("bc");
		bt = new BankTellerRole("bt");
		bd = new BankDatabaseAgent("bd");
		bc.startThread();
		bt.startThread();
		bd.startThread();
		bc.bt = bt;
		bt.bd = bd;
		bc.msgGoToBank("depositMoney", 100);
	}
	
	public static void main(String[] args) {
		BankPanel bankPanel = new BankPanel();
	}

}
