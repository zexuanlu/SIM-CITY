package restaurant5;
import java.util.*;
import java.util.concurrent.Semaphore;

import bank.interfaces.BankDatabase;
import market.interfaces.MarketCashier;
import person.PersonAgent; 
import restaurant5.interfaces.Cashier5; 
import restaurant5.interfaces.Customer5; 
import restaurant5.interfaces.Waiter5; 
import restaurant5.interfaces.Market5; 
import agent.Role;

public class Restaurant5Cashier extends Role implements Cashier5{
	Menu5 myMenu = new Menu5(); 
	String name; 
	int Cash; 
	int debt; 
	public BankDatabase bank;
	public int accountNumber;
	Semaphore getMoney = new Semaphore(0,true);
	
	PersonAgent myPerson; 

	private class Bill {
		Waiter5 w; 
		Customer5 c; 
		String choice; 
		BillState bs; 
		int price; 
		int paid; 
		int amountFlaked; 
		Bill(Waiter5 _w, Customer5 _c, String _choice, BillState _s){
			amountFlaked = 0;
			w = _w; 
			c = _c; 
			bs = _s;
			choice = _choice; 
		}
	}
	
	private class MarketBill {
		MarketCashier market; 
		int Bill; 
		MarketState mstate; 
		MarketBill(MarketCashier m, int b){
			market = m;
			Bill = b; 
		}
	}
	private enum MarketState {toPay, flaked, Pay, Done, waitForCook}; 
	private enum BillState {computing, computed, givenMoney, paid,flaked};
	
	
	public List<Bill> bills = Collections.synchronizedList( new ArrayList<Bill>() );
	private List<MarketBill> marketbills = Collections.synchronizedList(new ArrayList<MarketBill>());
	private List<Bill> flakes = Collections.synchronizedList(new ArrayList<Bill>());
	
	
	public Restaurant5Cashier(String name, PersonAgent p){
		super(p);
		myPerson = p; 
		this.name = name;
		Cash = 1000; 
	}

//Messages 
public void msgmarketbill(Market5 m, int Bill){
	//MarketBill marketbill = new MarketBill(m,Bill);
	//marketbill.mstate = MarketState.toPay; 
	//marketbills.add(marketbill);
	//stateChanged();
}


public void msgReceivedFood(){
	for (MarketBill mb: marketbills){
		if (mb.mstate == MarketState.waitForCook){
			mb.mstate = MarketState.toPay; 
			stateChanged(); 
			return; 
		}
	}
}

public void msgPleasepaytheBill(MarketCashier c, double bills){
	int b = (int) bills; 
	MarketBill marketbill = new MarketBill(c,b);
	marketbill.mstate = MarketState.waitForCook; 
	marketbills.add(marketbill);
	stateChanged();
}
	
public void msgcomputeBill(Waiter5 w1, Customer5 c1, String choice){
	Bill b = new Bill(w1,c1,choice, BillState.computing);
	bills.add(b);
	stateChanged();
}

public void msgPayment(Customer5 c, int cash) {
	
	synchronized(bills){
		for (Bill b: bills){
			if (b.bs == BillState.computed && b.c == c){
				b.paid = cash;
				b.bs = BillState.givenMoney; 
				Cash = Cash + cash; 
			}
		}
	}
	stateChanged();
}

public String getName(){
	return name;
}

public void msgAddMoney(){
	Cash = Cash + 2000; 
}

public void msgDrainMoney(){
	Cash = 100; 
}

public boolean pickAndExecuteAnAction() {
	if(Cash < 500.00){
		getMoneyFromBank();
		return true;
	}
	synchronized(bills){
		for (Bill b: bills){
			if (b.bs == BillState.computing){
				computeBill(b);
				return true; 
			}
		}
	}
	
	synchronized(bills){
		for (Bill b:bills){
			if (b.bs == BillState.givenMoney){
				processBill(b);
				return true; 
			}
		}
	}
	
	synchronized(bills){
		for (MarketBill b:marketbills){
			if (b.mstate == MarketState.toPay){
				payMarketBill(b);
				return true; 
			}
		}
	}
	return false;
	//we have tried all our rules and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
}

//Actions
private void payMarketBill(MarketBill b){
	//assume for right now he doesn't run out of money; 
	if (Cash >= b.Bill){
		Cash = Cash - b.Bill; 
		b.mstate = MarketState.Done; 
		print ("Cashier here is payment to Market of "+ b.Bill);
	//	b.market.msghereispayment(b.Bill, b.Bill); 	
		b.market.msgBillFromTheAir(b.Bill);
	}
	else if (Cash < b.Bill){
		int paid = Cash; 
		Cash = 0; //send all that you can
		b.mstate = MarketState.Done; 
		print ("Cashier couldn't pay all of Bill but paid only " + paid); 
		//b.market.msghereispayment(b.Bill, paid);
		b.market.msgBillFromTheAir(paid);
	}
}


private void computeBill(Bill b){
	b.bs = BillState.computed; 
	b.price = myMenu.prices.get(b.choice);
	//search to see if he flaked or not
	synchronized(flakes){
		for (Bill f: flakes){
			if (b.c == f.c && f.bs == BillState.flaked){
				f.bs = BillState.paid; 
				b.price += f.amountFlaked; 
				print ("Cashier in a previous visit, Customer flaked " + f.amountFlaked);
			}
		}
	}
	Check5 check = new Check5(b.c,b.choice,b.price);
	print("Cashier here is Check of " +  b.price);
	(b.w).msgHereisCheck(check);
}

private void processBill(Bill b){
	if (b.paid >= b.price){
		b.bs = BillState.paid; 
		print("Cashier here is Change of " + (b.paid - b.price));
		(b.c).msgChange(b.paid - b.price);
	}
	else if (b.paid < b.price){ //Flake condition
		b.bs = BillState.paid; 
		Bill flaker = new Bill(b.w, b.c, b.choice, BillState.flaked);
		flaker.amountFlaked = b.price - b.paid; 
		flakes.add(flaker);
		print ("Cashier you did not pay " + (flaker.amountFlaked));
		(b.c).msgChange(0);
	}
}

public String toString(){
	return name; 
}

public String getRoleName(){
	return "Restaurant 5 Cashier";
}

private void getMoneyFromBank(){
	bank.msgWithdrawMoney(this, (1000.00-Cash), accountNumber);
	try{
		getMoney.acquire();
	}
	catch(InterruptedException ie){
		ie.printStackTrace();
	}
}

public void msgAddMoney(double amount) {
	Cash += amount;
	getMoney.release();
}


}