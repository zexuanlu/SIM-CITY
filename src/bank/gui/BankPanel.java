package bank.gui;

import javax.swing.JFrame;

import bank.*;

public class BankPanel extends JFrame{

	BankCustomerRole bc;
	BankTellerRole bt;
	BankDatabaseAgent bd;
	BankHostRole bh;
	static AnimationPanel ani = new AnimationPanel();
	BankCustomerGui cGui;
	
	BankPanel(){
		bc = new BankCustomerRole("bc");
		bt = new BankTellerRole("bt");
		bd = new BankDatabaseAgent("bd");
		bh = new BankHostRole("bh");
		cGui = new BankCustomerGui(bc, ani);
		ani.addGui(cGui);
		bc.gui = cGui;
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
		bankPanel.setTitle("Testing");
		bankPanel.setVisible(true);
		ani.setVisible(true);
		bankPanel.setBounds(50,50,300,600);
		bankPanel.add(ani);
		bankPanel.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
