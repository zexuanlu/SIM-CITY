package market;

import agent.*;
import person.Restaurant;
import person.interfaces.*;
import utilities.restaurant.RestaurantCashier;
import utilities.restaurant.RestaurantCook;

import java.util.*;

import market.interfaces.*;

public class MarketCashierRole extends Role implements MarketCashier{

	public List<MarketEmployee> employee = new ArrayList<MarketEmployee>();
	public List<MarketTruck> truck = new ArrayList<MarketTruck>();
	public List<Mycustomer> mycustomer = new ArrayList<Mycustomer>();
	public List<Myrest> myrest = new ArrayList<Myrest>();
	public List<PendingOrder> pendingPrder = new ArrayList<PendingOrder>();
	public Map<String, Double> price = new HashMap<String, Double>();
	public Map<String, Integer> inventory = new HashMap<String, Integer>();
	public List<PendingOrder> pendingOrder = new ArrayList<PendingOrder>();
	int employeeCount = 0;
	int truckCount = 0;
	int seatCount = 1;
	double income = 0;
	boolean endOfDay = false;
	Person person;
	

	public MarketCashierRole(Person person, String name){
		super(person);
		roleName = "Market Cashier";
		this.person = person;
		inventory.put("Steak", 2000);
		inventory.put("Car", 2000);
		inventory.put("Pizza", 2000);
		inventory.put("Chicken", 2000);
		inventory.put("Salad", 2000);
		inventory.put("Hamburger", 2000);
		inventory.put("Ribs", 2000);
		inventory.put("Pound Cake", 2000);
		inventory.put("Shrimp", 2000);
		inventory.put("Scallops", 2000);
		inventory.put("Lobster", 2000);
		inventory.put("Crab", 2000);
		inventory.put("Mint Chip Ice Cream", 2000);
		inventory.put("Rocky Road Ice Cream", 2000);
		inventory.put("Green Tea Ice Cream", 2000);
		inventory.put("Mocha Almond Fudge Ice Cream", 2000);
		inventory.put("Belgium", 2000);
		inventory.put("Sassy", 2000);
		inventory.put("Chocolate", 2000);
		
		price.put("Steak", (double) 2);
		price.put("Car", (double) 2);
		price.put("Pizza", (double) 2);
		price.put("Chicken", (double) 2);
		price.put("Salad", (double) 2);
		price.put("Hamburger", (double) 2);
		price.put("Ribs", (double) 2);
		price.put("Pound Cake", (double) 2);
		price.put("Shrimp", (double) 2);
		price.put("Scallops", (double) 2);
		price.put("Lobster", (double) 2);
		price.put("Crab", (double) 2);
		price.put("Mint Chip Ice Cream", (double) 2);
		price.put("Rocky Road Ice Cream", (double) 2);
		price.put("Green Tea Ice Cream", (double) 2);
		price.put("Mocha Almond Fudge Ice Cream", (double) 2);
		price.put("Belgium", (double) 2);
		price.put("Sassy", (double) 2);
		price.put("Chocolate", (double) 2);
		
	}


	public class Mycustomer{
		MarketCustomer c;
		List<Food> order;
		public List<Food> collectedOrder = new ArrayList<Food>();
		public state s = state.ordering;
		public int bill;
		public double pay;
		double change;

		Mycustomer(MarketCustomer c, List<Food> order){
			this.c = c;
			this.order = order;
		}
	}
	public enum state{ordering, ordered, paying, payed, collected, calling, taking, taken }


	public class Myrest{
		RestaurantCook ck;
		RestaurantCashier ca;
		List<Food> order;
		public List<Food> collectedOrder = new ArrayList<Food>();
		public int bill;
		public double pay;
		public int change;
		public int restNum;
		public state1 s1 = state1.ordering;

		Myrest(RestaurantCook ck, RestaurantCashier ca, List<Food> order, int restNum){
			this.ck = ck;
			this.ca = ca;
			this.order = order;
			this.restNum = restNum;
		}
	}
	public enum state1{ordering, ordered};
	
	public class PendingOrder{
		Restaurant r;
		RestaurantCook ck;
		List<Food> order;
		int restnum;
		
		public PendingOrder(RestaurantCook ck, List<Food> order, Restaurant r, int restnum){
			this.ck = ck;
			this.order = order;
			this.r = r;
			this.restnum = restnum;
		}
	}

	public Mycustomer find(MarketCustomer mc){
		Mycustomer a = null;
		for(Mycustomer m: mycustomer){
			if(mc == m.c){
				a = m;
				return a;
			}
		}
		return a;
	}

	public void addTruck(MarketTruck t){
		truck.add(t);
	}

	public void addEmployee(MarketEmployee e){
		employee.add(e);
	}

	public void msgEndOfDay(){
		endOfDay = true;
		stateChanged();
	}
	
	public void msgHereisOrder(MarketCustomer customer, List<Food> food){
		mycustomer.add(new Mycustomer(customer, food));
		stateChanged();
	}

	public void msgPayment(MarketCustomer customer, double m){
		Mycustomer mc = find(customer);
		mc.s = state.paying;
		mc.pay = m;
		stateChanged();
	}

	public void msgHereisProduct(MarketCustomer customer, List<Food> order){
		Mycustomer mc = find(customer);
		mc.s = state.collected;
		stateChanged();
	}

	public void msgGoToTable(MarketCustomer customer){
		Mycustomer mc = find(customer);
		mc.s = state.taking;
		stateChanged();
	}				
	// end of in market scenario

	public void MsgIwantFood(RestaurantCook cook, RestaurantCashier ca, List<Food> food, int number){
		myrest.add(new Myrest(cook, ca, food, number));
		stateChanged();
	}

	public void msgBillFromTheAir(double money){
		income += money;
		stateChanged();
	}

	public void msgTruckBack(MarketTruck t){
		truck.add(t);
		stateChanged();
	}
	
	public void msgDevliveryFail(MarketTruck t, RestaurantCook cook, List<Food> food, Restaurant r, int restnum){
		truck.add(t);
		pendingOrder.add(new PendingOrder(cook, food, r, restnum));
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		
		
		if(!truck.isEmpty()){
			for(Myrest rest: myrest){
				if(rest.s1 == state1.ordering){
					TakeOrderFromCook(rest, truck.get(0));
					return true;
				}
			} 
		}

		if(!truck.isEmpty()){
			if(!pendingOrder.isEmpty()){
				for(PendingOrder po: pendingOrder){
					if(!po.r.isClosed()){
						SendPendingOrder(po);
				return true;
					}
				}
			}
		}
		
		for(Mycustomer customer: mycustomer){
			if(customer.s == state.ordering){
				TakeOrder(customer);
				return true;
			}
		}
		for(Mycustomer customer: mycustomer){
			if(customer.s == state.paying){
				Change(customer);
				return true;
			}
		}
		for(Mycustomer customer: mycustomer){
			if(customer.s == state.collected){
				CallCustomer(customer);
				return true;
			}
		}
		for(Mycustomer customer: mycustomer){
			if(customer.s == state.taking){
				GiveFood(customer);
				return true;
			}
		}
		// end of in market scenario

		if(mycustomer.isEmpty() && myrest.isEmpty() && endOfDay){
			workDayOver();
			return true;
		}
		
		return false;
	}


	void TakeOrder(Mycustomer customer){
		customer.s = state.ordered;
		Do("Hey!!!!!!!");
		customer.bill = DoCalculateBill(customer, customer.order);
		customer.c.msgPleasePay(customer.bill);
		int s = employeeCount;
		if(employeeCount < (employee.size() - 1)){
			employeeCount++;
		}
		else{
			employeeCount = 0;
		}
		Do("Here!!!!!!!");
		employee.get(s).msgCollectOrer(customer.c, customer.collectedOrder);
	}

	void Change(Mycustomer customer){
		customer.s = state.payed;
		customer.change = customer.pay - customer.bill;
		income += customer.bill;
		//Calculate the change;
		int a = seatCount;
		if(seatCount < 9){
			seatCount ++;
		}
		else{
			seatCount = 1;
		}
		customer.c.msgHereisYourChange(customer.change, a);
	}

	void CallCustomer(Mycustomer customer){
		customer.s = state.calling;
		customer.c.msgYourFoodReady();
	}

	void GiveFood(Mycustomer customer){
		customer.s = state.taken;
		//DoGiveOrder();
		customer.c.msgHereisYourOrder(customer.collectedOrder);
		mycustomer.remove(customer);
	}

	int DoCalculateBill(Mycustomer customer, List<Food> order){
		int bill = 0;
		for(int i = 0; i < order.size(); i++){
			if(order.get(i).amount <= inventory.get(order.get(i).choice)){
				bill +=price.get(order.get(i).choice) * order.get(i).amount;
				customer.collectedOrder.add(order.get(i));
				inventory.put(order.get(i).choice, inventory.get(order.get(i).choice) - order.get(i).amount);
			}
			else{
				bill +=price.get(order.get(i).choice) * inventory.get(order.get(i).choice);
				customer.collectedOrder.add(new Food(order.get(i).choice, inventory.get(order.get(i).choice)));
				inventory.put(order.get(i).choice,0);
			}
		}
		return bill;
	}

	// end of in market scenario

	void TakeOrderFromCook(Myrest rest, MarketTruck t){
		rest.s1 = state1.ordered;
		rest.bill = DocalculateThebillFromRest(rest, rest.order);
		rest.ca.msgPleasepaytheBill(this, rest.bill);
		int s = employeeCount;
		if(employeeCount < (employee.size() - 1)){
			employeeCount++;
		}
		else{
			employeeCount = 0;
		}
		employee.get(s).msgCollectTheDilivery(rest.ck, rest.order, t, rest.restNum);
		truck.remove(t);
		myrest.remove(rest);
	}

	int DocalculateThebillFromRest(Myrest rest, List<Food> order){
		int bill = 0;
		for(int i = 0; i < order.size(); i++){
			if(order.get(i).amount <= inventory.get(order.get(i).choice)){
				bill +=price.get(order.get(i).choice) * order.get(i).amount;
				rest.collectedOrder.add(order.get(i));
				inventory.put(order.get(i).choice, inventory.get(order.get(i).choice) - order.get(i).amount);
			}
			else{
				bill +=price.get(order.get(i).choice) * inventory.get(order.get(i).choice);
				rest.collectedOrder.add(new Food(order.get(i).choice, inventory.get(order.get(i).choice)));
				inventory.put(order.get(i).choice,0);
			}
		}
		return bill;
	}
	
	public void SendPendingOrder(PendingOrder po){
		int s = employeeCount;
		MarketTruck t = truck.get(0);
		if(employeeCount < (employee.size() - 1)){
			employeeCount++;
		}
		else{
			employeeCount = 0;
		}
		employee.get(s).msgCollectTheDilivery(po.ck, po.order, t, po.restnum);
		truck.remove(t);
		pendingOrder.remove(po);
	}

	public void workDayOver(){
		for(MarketEmployee e: employee){
			e.msgWorkDayOver();
		}
		endOfDay = false;
		getPerson().msgGoOffWork(this, 500.00);
	}
	
	public String getRoleName(){
		return roleName;
	}
}
