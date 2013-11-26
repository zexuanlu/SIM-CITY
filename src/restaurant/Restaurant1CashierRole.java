package restaurant;

import java.util.*;

import restaurant.interfaces.Cashier;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import agent.Role;
import person.PersonAgent;

import market.interfaces.MarketCashier;

public class Restaurant1CashierRole extends Role implements Cashier{
	
	String name;
	public List<Check> check = Collections.synchronizedList(new ArrayList<Check>());
	public List<Bill> bill = Collections.synchronizedList(new ArrayList<Bill>());
	public Map<String, Double> Price = new HashMap<String, Double>();
	public double money = 70;
	boolean payingbill = false;
	MarketCashier marketCashier;
	
	public Restaurant1CashierRole(String name, PersonAgent pa){
		super(pa);
		this.name = name;
		Price.put("Steak", 15.99);
		Price.put("Chicken", 10.99);
		Price.put("Salad", 5.99);
		Price.put("Pizza", 8.99);
	}
	
	public class Bill{
		public MarketCashier m;
		public double pay;
		
		Bill(MarketCashier m, double pay){
			this.m = m;
			this.pay = pay;
		}
	}
	
	public class Check{
		Waiter w;
		public Customer c;
		String choice;
		public state s = state.checkrequst;
		double pay;
		public double price;
		public double change;
		
		public Check(Waiter wa, Customer ca, String choice){
			this.w = wa;
			this.c = ca;
			this.choice = choice;
		}
	}
	public enum state {checkrequst, checkback, payingcheck, checkdone};
	
	private Check find(Customer c){
		Check a = null;
		for(Check m: check){
			if(c == m.c){
				a = m;
				return a;
			}
		}
		return a;
	}
	
	public void msgCheckthePrice(Waiter w, Customer c, String choice){
		check.add(new Check(w,c,choice));
		stateChanged();
	}
	
	public void msgPayment(Customer c, double paying){
		Check C = find(c);
		C.pay = paying;
		C.s = state.payingcheck;
		stateChanged();
	}
	
	public void msgPleasepaytheBill(MarketCashier c, double bills){
		bill.add(new Bill(c, bills));
		stateChanged();
	}
	
	

	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		synchronized(bill){
		if(!bill.isEmpty() && (money > 0)){
			DoPayBill();
			return true;
		}
		}
		synchronized(check){
		for(Check checks: check){
			if(checks.s == state.checkrequst){
				DoGiveCheckBack(checks);
				return true;
			}
		}
		}
		synchronized(check){
		for(Check checks: check){
			if(checks.s == state.payingcheck){
				DoChange(checks);
				return true;
			}
		}
		}
		return false;
	}

	public void DoPayBill(){
		Bill b = bill.get(0);
		if(money > b.pay){
			money-= b.pay;
			Do("Here is "+ b.pay);
			bill.get(0).m.msgBillFromTheAir(this, b.pay);
			bill.remove(0);
		}
		else{
			b.pay -= money;
			bill.get(0).m.msgBillFromTheAir(this, money);
			money = 0;
			Do("Money not enough, I will pay the rest next time");
		}

	}
	
	public void DoGiveCheckBack(Check C){
		C.price = Price.get(C.choice);
		Do("Please pay "+C.price);
		C.s = state.checkback;
		C.w.msgHereistheCheck(C.c, C.price);
	}
	

	public void DoChange(Check C){
		C.s = state.checkdone;
		double a = C.pay - C.price;
		C.change = a;
		money += C.price;
		Do("Here is your change: "+a);
		//log.add(new LoggedEvent("Here is your change: "+a));
		if(a < 0)
			Do("Please pay next time");
		C.c.msgHereisYourChange(a);
	}

	
}
