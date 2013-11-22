package market;

import agent.*;

import java.util.*;

import marketinterface.Cashier;
import marketinterface.Cook;
import marketinterface.Customer;
import marketinterface.Employee;
import marketinterface.Truck;

public class CashierAgent extends Role implements Cashier{

	public List<Employee> employee = new ArrayList<Employee>();
	public List<Truck> truck = new ArrayList<Truck>();
	public List<Mycustomer> mycustomer = new ArrayList<Mycustomer>();
	public List<Myrest> myrest = new ArrayList<Myrest>();
	public Map<String, Double> price = new HashMap<String, Double>();
	public Map<String, Integer> inventory = new HashMap<String, Integer>();
	int employeeCount = 0;
	int truckCount = 0;
	double income = 0;

	public CashierAgent(){
		inventory.put("Steak", 2);
		inventory.put("Car", 2);
		price.put("Steak", (double) 2);
		price.put("Car", (double) 2);
	}


	public class Mycustomer{
		Customer c;
		List<Food> order;
		public List<Food> collectedOrder = new ArrayList<Food>();
		public state s = state.ordering;
		public int bill;
		public double pay;
		double change;

		Mycustomer(Customer c, List<Food> order){
			this.c = c;
			this.order = order;
		}
	}
	public enum state{ordering, ordered, paying, payed, collected, calling, taking, taken }


	public class Myrest{
		Cook ck;
		CashAgent ca;
		List<Food> order;
		public List<Food> collectedOrder = new ArrayList<Food>();
		public int bill;
		public double pay;
		public int change;
		public state1 s1 = state1.ordering;

		Myrest(Cook ck, CashAgent ca, List<Food> order){
			this.ck = ck;
			this.ca = ca;
			this.order = order;
		}
	}
	public enum state1{ordering, ordered};

	public Mycustomer find(Customer mc){
		Mycustomer a = null;
		for(Mycustomer m: mycustomer){
			if(mc == m.c){
				a = m;
				return a;
			}
		}
		return a;
	}

	public void addTruck(Truck t){
		truck.add(t);
	}

	public void addEmployee(Employee e){
		employee.add(e);
	}

	public void msgHereisOrder(Customer customer, List<Food> food){
		mycustomer.add(new Mycustomer(customer, food));
		stateChanged();
	}

	public void msgPayment(Customer customer, double m){
		Mycustomer mc = find(customer);
		mc.s = state.paying;
		mc.pay = m;
		stateChanged();
	}

	public void msgHereisProduct(Customer customer, List<Food> order){
		Mycustomer mc = find(customer);
		mc.s = state.collected;
		stateChanged();
	}

	public void msgGoToTable(Customer customer){
		Mycustomer mc = find(customer);
		mc.s = state.taking;
		stateChanged();
	}				
	// end of in market scenario

	public void MsgIwantFood(Cook cook, CashAgent ca, List<Food> food){
		myrest.add(new Myrest(cook, ca, food));
		stateChanged();
	}

	public void msgBillFromTheAir(CashAgent ca, double money){
		// don’t know what to do (⊙o⊙)
		income += money;
		stateChanged();
	}

	public void msgTruckBack(Truck t){
		truck.add(t);
		stateChanged();
	}

	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
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
		if(!truck.isEmpty()){
			for(Myrest rest: myrest){
				if(rest.s1 == state1.ordering){
					TakeOrderFromCook(rest, truck.get(0));
					return true;
				}
			} 
		}

		return false;
	}


	void TakeOrder(Mycustomer customer){
		customer.s = state.ordered;
		customer.bill = DoCalculateBill(customer, customer.order);
		customer.c.msgPleasePay(customer.bill);
		int s = employeeCount;
		if(employeeCount < (employee.size() - 1)){
			employeeCount++;
		}
		else{
			employeeCount = 0;
		}
		employee.get(s).msgCollectOrer(customer.c, customer.collectedOrder);
	}

	void Change(Mycustomer customer){
		customer.s = state.payed;
		customer.change = customer.pay - customer.bill;
		income += customer.bill;
		//Calculate the change;
		customer.c.msgHereisYourChange(customer.change);
	}

	void CallCustomer(Mycustomer customer){
		customer.s = state.calling;
		customer.c.msgYourFoodReady();
	}

	void GiveFood(Mycustomer customer){
		customer.s = state.taken;
		//DoGiveOrder();
		customer.c.msgHereisYourOrder(customer.collectedOrder);
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

	void TakeOrderFromCook(Myrest rest, Truck t){
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
		employee.get(s).msgCollectTheDilivery(rest.ck, rest.collectedOrder, t);
		truck.remove(t);
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

}
