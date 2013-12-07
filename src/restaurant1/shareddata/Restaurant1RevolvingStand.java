package restaurant1.shareddata;

import java.util.Vector;

import restaurant2.Restaurant2Order;

public class Restaurant1RevolvingStand extends Object{

	private final int N = 5;
	private int count = 0;
	public Vector<Order> orders;
	
	public Restaurant1RevolvingStand() {
		orders = new Vector<Order>();
	}
	
	synchronized public void insertOrder(Order o){
		while (count == N) {
			try{ 
				System.out.println("\tFull, waiting");
				wait(5000);                         // Full, wait to add
			} catch (InterruptedException ex) {};
		}
		
		orders.add(o);
		if(count == 1) {
			System.out.println("\tNot Empty, notify");
			notify();                               // Not empty, notify a 
			// waiting consumer
		}
	}
	
	synchronized public Order removeOrder(){
		Order data;
		if(count == 0){
			return null;
		}
		data = remove_item();
		count --;
		if(count == N-1){ 
			System.out.println("\tNot full, notify");
			notify();                               // Not full, notify a 
			// waiting producer
		}
		return data;
		
	}
	
	synchronized public boolean isEmpty(){
		return orders.isEmpty();
	}
	
	synchronized private Order remove_item(){
		Order data = (Order) orders.firstElement();
		orders.removeElementAt(0);
		return data;
	}

}
