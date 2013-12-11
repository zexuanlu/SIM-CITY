package restaurant1;

import java.util.*;
import java.util.concurrent.Semaphore;

import bank.interfaces.BankDatabase;
import restaurant1.interfaces.Restaurant1Cashier;
import restaurant1.interfaces.Restaurant1Customer;
import restaurant1.interfaces.Restaurant1Waiter;
import agent.Role;
import person.interfaces.Person;
import market.Food;
import market.interfaces.MarketCashier;

public class Restaurant1CashierRole extends Role implements Restaurant1Cashier{
	public boolean offWork = false; 
	public int offWorkMess = 0; 
	
	
	String name;
	public List<Check> check = Collections.synchronizedList(new ArrayList<Check>());
	public List<Bill> bill = Collections.synchronizedList(new ArrayList<Bill>());
	public Map<String, Double> Price = new HashMap<String, Double>();
	public double money = 0.0;
	public int accountNumber;
	Semaphore getMoney = new Semaphore(0, true);
	public BankDatabase bank;
	boolean payingbill = false;
	MarketCashier marketCashier;

	public Restaurant1CashierRole(String name, Person pa){
		super(pa);
		roleName = "Rest1 Cashier";
		this.name = name;
		Price.put("Steak", 15.99);
		Price.put("Chicken", 10.99);
		Price.put("Salad", 5.99);
		Price.put("Pizza", 8.99);
	}

	public class Bill{
		public MarketCashier m;
		public double pay;
		public double invoice;
		public state1 s1 = state1.notpaying;
		Bill(MarketCashier m, double pay){
			this.m = m;
			this.pay = pay;
		}
	}

	public class Check{
		Restaurant1Waiter w;
		public Restaurant1Customer c;
		String choice;
		public state s = state.checkrequst;
		double pay;
		public double price;
		public double change;

		public Check(Restaurant1Waiter wa, Restaurant1Customer ca, String choice){
			this.w = wa;
			this.c = ca;
			this.choice = choice;
		}
	}
	public enum state {checkrequst, checkback, payingcheck, checkdone};
	public enum state1 {notpaying, paynow}

	private Check find(Restaurant1Customer c){
		Check a = null;
		for(Check m: check){
			if(c == m.c){
				a = m;
				return a;
			}
		}
		return a;
	}

	private Bill findcashier(MarketCashier c){
		Bill a = null;
		for(Bill m: bill){
			if(c == m.m){
				a = m;
				return a;
			}
		}
		return a;
	}

	public void msgCheckthePrice(Restaurant1Waiter w, Restaurant1Customer c, String choice){
		check.add(new Check(w,c,choice));
		stateChanged();
	}

	public void msgPayment(Restaurant1Customer c, double paying){
		Check C = find(c);
		C.pay = paying;
		C.s = state.payingcheck;
		stateChanged();
	}
	
	public void msgGoOffWork(){
		offWorkMess ++; 
	//	if (offWorkMess == 2){
			offWork = true; 
			stateChanged(); 
		//}
	}

	public void msgPleasepaytheBill(MarketCashier c, double bills){
		bill.add(new Bill(c, bills));
		stateChanged();
	}

	public void msgYouCanPayNow(MarketCashier c, List<Food> food){
		Bill mb = findcashier(c);
		double a = 0;
		for(Food f: food){
			a += Price.get(f.choice);
		}
		mb.invoice = a;
		mb.s1 = state1.paynow;
		stateChanged();
	}



	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		if(money < 500.00){
			getMoneyFromBank();
			return true;
		}
		synchronized(bill){
			for(Bill b: bill){
				if(money > 0 && b.s1 == state1.paynow){
					DoPayBill();
					return true;
				}
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
		if (offWork){
			//CHECK NOTHING TO DO FO REALS
			goOffWork(); 
			return true; 
		}

		return false;
	}

	
	private void goOffWork(){
		print("restaurantcashier go offwork");

		offWork = false; 
		offWorkMess = 0; 
		this.person.msgGoOffWork(this, 0);
	}
	
	public void DoPayBill(){
		Bill b = bill.get(0);
		if(money > b.pay){
			money-= b.pay;
			Do("Here is "+ b.pay);
			bill.get(0).m.msgBillFromTheAir(b.pay);
			bill.remove(0);
		}
		else{
			b.pay -= money;
			bill.get(0).m.msgBillFromTheAir(money);
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

	public String getRoleName(){
		return roleName;
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
