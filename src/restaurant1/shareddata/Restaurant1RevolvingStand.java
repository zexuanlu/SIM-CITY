package restaurant1.shareddata;

import java.util.Vector;

public class Restaurant1RevolvingStand {

	public Vector<Order> orders;
	
	public Restaurant1RevolvingStand() {
		orders = new Vector<Order>();
	}
	
	public void insertOrder(Order o){
		orders.add(o);
	}
	
	public Order removeOrder(){
		return orders.remove(0);
	}
	
	public boolean isEmpty(){
		return orders.isEmpty();
	}

}
