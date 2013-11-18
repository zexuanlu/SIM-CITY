package bank.gui;

import bank.*;

public class BankPanel {

	BankCustomerRole bc;
	BankTellerRole bt;
	BankDatabaseAgent bd;
	BankHostRole bh;
	
	BankPanel(){
		bc = new BankCustomerRole("bc");
		bt = new BankTellerRole("bt");
		bd = new BankDatabaseAgent("bd");
		bh = new BankHostRole("bh");
		bh.startThread();
		bc.startThread();
		bt.startThread();
		bd.startThread();
		bc.bh = bh;
		bt.setHost(bh);
		bh.addTeller(bt);
		bt.bd = bd;
		bc.msgGoToBank("deposit", 100);
	}
	
	public static void main(String[] args) {
		BankPanel bankPanel = new BankPanel();
	}

}
