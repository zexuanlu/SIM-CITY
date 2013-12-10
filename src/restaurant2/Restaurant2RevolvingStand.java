package restaurant2;

import java.util.Vector;

public class Restaurant2RevolvingStand {
	private final int N = 5;
	private int count = 0;
	private Vector<Restaurant2Order> theData;

	public Restaurant2RevolvingStand(){
		theData = new Vector<Restaurant2Order>();
	}

	synchronized public void insert(Restaurant2Order data) {
		while (count == N) {
			try{ 
				System.out.println("\tFull, waiting");
				wait(5000);                         // Full, wait to add
			} catch (InterruptedException ex) {};
		}

		insert_item(data);
		count++;
		if(count == 1) {
			System.out.println("\tNot Empty, notify");
			notify();                               // Not empty, notify a 
			// waiting consumer
		}
	}

	synchronized public Restaurant2Order remove() {
		Restaurant2Order data;
		while(count == 0)
			try{ 
				System.out.println("\tEmpty, waiting");
				wait(5000);                         // Empty, wait to consume
			} catch (InterruptedException ex) {};

			data = remove_item();
			count--;
			if(count == N-1){ 
				System.out.println("\tNot full, notify");
				notify();                               // Not full, notify a 
				// waiting producer
			}
			return data;
	}

	private void insert_item(Restaurant2Order data){
		theData.addElement(data);
	}
	public boolean isEmpty(){
		return theData.isEmpty();
	}
	private Restaurant2Order remove_item(){
		Restaurant2Order data = (Restaurant2Order) theData.firstElement();
		theData.removeElementAt(0);
		return data;
	}
}
