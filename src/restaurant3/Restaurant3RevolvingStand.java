package restaurant3;

import java.util.Vector;


public class Restaurant3RevolvingStand extends Object{

	private int count = 0;
	public Vector<Restaurant3Order> orders;
	
	public Restaurant3RevolvingStand() {
		orders = new Vector<Restaurant3Order>();
	}
	
	synchronized public void insertOrder(Restaurant3Order o){
		orders.add(o);
		count ++;
		System.out.println("Item put in stand is "+count);
		if(count == 1) {
			System.out.println("\tNot Empty, notify");
			notify();                               		
		}
	}
	
	synchronized public Restaurant3Order removeOrder(){
		Restaurant3Order data;
		if(count == 0){
			return null;
		}
		else{
		data = remove_item();
		count --;
		System.out.println("Item left in stand is "+count);
		return data;
		}
	}
	
	synchronized public boolean isEmpty(){
		return orders.isEmpty();
	}
	
	synchronized private Restaurant3Order remove_item(){
		Restaurant3Order data = (Restaurant3Order) orders.firstElement();
		orders.removeElementAt(0);
		System.out.println(""+orders.size());
		return data;
	}

}