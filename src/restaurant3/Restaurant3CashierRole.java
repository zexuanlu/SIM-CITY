package restaurant3;

import agent.Role;
import bank.interfaces.BankDatabase;
import person.PersonAgent;
import restaurant3.interfaces.Restaurant3Cashier;
import restaurant3.interfaces.Restaurant3Waiter;
import market.interfaces.MarketCashier;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Semaphore;

public class Restaurant3CashierRole extends Role implements Restaurant3Cashier {

	//MEMBER DATA
	String name;
	double money = 500;
	public BankDatabase bank;
	public int accountNumber;
	Semaphore getMoney = new Semaphore(0,true);
	
	//Food prices
	static final double steak = 10.00;
	static final double pizza = 7.50;
	static final double chicken = 6.75;
	static final double salad = 5.00;
	
	//Enum to keep track of bill state
	public enum bState {pending, withCust, paid}; 
	
	
	//Private class to keep track of customer bills
	private class Bill{
		Restaurant3Waiter wtr;
		int tableNum;
		String choice;
		double amount;
		double payment;
		bState state;
		
		Bill(Restaurant3Waiter w, int tNum, String c){
			wtr = w;
			tableNum = tNum;
			choice = c;
			state = bState.pending;
		}
	}
	
	//Private class to keep track of market bills
	private class MarketBill{
		MarketCashier mCash;
		double payAmt;
		
		MarketBill(MarketCashier m, double pA){
			mCash = m;
			payAmt = pA;
		}
	}
	
	//Lists
	List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	List<MarketBill> mBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	
	//Utilities
	private Map<String, Double> prices = new HashMap<String, Double>();
	
	public Restaurant3CashierRole(String name, PersonAgent pa) {
		super(pa);
		this.name = name;
		roleName = "Restaurant 3 Cashier";
		
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
	
	public String getRoleName(){
		return roleName;
	}

	//MESSAGES ***********************************
	@Override
	public void msgPrepareBill(Restaurant3Waiter w, int table, String choice) {
		print(name + ": received bill request from waiter " + w.getName());
		bills.add(new Bill(w, table, choice));
		stateChanged();
	}

	@Override
	public void msgHereIsMoney(Restaurant3Waiter w, int table, double money) {
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
	public void msgPleasepaytheBill(MarketCashier c, double pAmt){
		print(name + ": received a bill from market");
		mBills.add(new MarketBill(c, pAmt));
		stateChanged();
	}

	//SCHEDULER *************************************
	@Override
	public boolean pickAndExecuteAnAction() {
		if(money < 500.00){
			getMoneyFromBank();
			return true;
		}
		synchronized(mBills){
			if(!mBills.isEmpty() && money > 0){
				payMarketBill();
				return true;
			}
		}
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
		money += b.amount;
		bills.remove(b);
	}
	
	public void payMarketBill(){
		print(name + " paying bill for market");
		MarketBill mb = mBills.get(0);
		if(money >= mb.payAmt){
			money -= mb.payAmt;
			print(name + ": paid full bill of " + mb.payAmt);
			mb.mCash.msgBillFromTheAir(mb.payAmt);
			mBills.remove(mb);
		}
		else {
			double payment = mb.payAmt - money;
			money = 0;
			print(name + ": paid partial bill. Will pay " + mb.payAmt + " later");
			mb.mCash.msgBillFromTheAir(payment);
		}
	}
	
	public utilities.Gui getGui(){
		return null; 

	}
	private void getMoneyFromBank(){
		bank.msgWithdrawMoney(this, (1000.00-money), accountNumber);
		try{
			getMoney.acquire();
		}
		catch(InterruptedException ie){
			ie.printStackTrace();
		}
	}
	
	public void msgAddMoney(double amount) {
		money += amount;
		getMoney.release();
	}
}
