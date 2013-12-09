package restaurant3;

import agent.Agent;
import agent.Role;
import restaurant3.interfaces.Cashier;
import restaurant3.interfaces.Waiter;

import java.util.*;
import java.util.Map.Entry;

import person.interfaces.Person;

public class Restaurant3CashierRole extends Agent implements Cashier {

	//MEMBER DATA
	String name;
	
	//Food prices
	static final double steak = 10.00;
	static final double pizza = 7.50;
	static final double chicken = 6.75;
	static final double salad = 5.00;
	
	//Enum to keep track of bill state
	public enum bState {pending, withCust, paid}; 
	
	
	//Private class to keep track of bills
	private class Bill{
		Waiter wtr;
		int tableNum;
		String choice;
		double amount;
		double payment;
		bState state;
		
		Bill(Waiter w, int tNum, String c){
			wtr = w;
			tableNum = tNum;
			choice = c;
			state = bState.pending;
		}
	}
	
	//Lists
	List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	
	//Utilities
	private Map<String, Double> prices = new HashMap<String, Double>();
	
	public Restaurant3CashierRole(String name) {
		super();
		this.name = name;
		
		//Initialize price info
		prices.put("Steak", steak);
		prices.put("Pizza", pizza);
		prices.put("Chicken", chicken);
		prices.put("Salad", salad);
	}
	
	//HELPER METHODS *****************************
	public String getName(){
		return name;
	}

	@Override
	public void msgPrepareBill(Waiter w, int table, String choice) {
		print(name + ": received bill request from waiter " + w.getName());
		bills.add(new Bill(w, table, choice));
		stateChanged();
	}

	@Override
	public void msgHereIsMoney(Waiter w, int table, double money) {
		print(name + ": received money from table " + table);
		synchronized(bills){
			for(Bill b : bills){
				if(b.wtr == w && b.tableNum == table){
					b.payment = money;
					b.state = bState.paid;
				}
			}
		}
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		if(!bills.isEmpty()){
			synchronized(bills){
				
				//Check for pending bills
				for(Bill b : bills){
					if(b.state == bState.pending){
						prepareBill(b);
						return true;
					}
				}
				
				for(Bill b : bills){
					if(b.state == bState.paid){
						sendChange(b);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	//ACTIONS ************************************
	public void prepareBill(Bill b){
		print(name + ": preparing bill for table " + b.tableNum);
		for(Entry<String, Double> e : prices.entrySet()){
			if(e.getKey().equals(b.choice)){
				b.amount = e.getValue();
				b.state = bState.withCust;
				b.wtr.msgHereIsABill(b.tableNum, b.amount);
			}
		}
		stateChanged();
	}
	
	public void sendChange(Bill b){
		print(name + ": calculating and sending change for table " + b.tableNum);
		b.wtr.msgHereIsChangeReceipt(b.tableNum, (b.payment - b.amount));
		bills.remove(b);
	}

	public String getRoleName() {
		return "Restaurant 3 Cashier";
	}

}
