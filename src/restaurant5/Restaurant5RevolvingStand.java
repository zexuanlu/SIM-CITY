package restaurant5;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import restaurant5.Restaurant5CookAgent; 
import java.util.Vector; 
import restaurant5.interfaces.Waiter5; 

public class Restaurant5RevolvingStand extends Object {
	private final int capacity = 3; 
	private int currentNum = 0; 
	public Vector<CookOrder5> cookorders; 
	public Restaurant5RevolvingStand(){
		cookorders = new Vector<CookOrder5>();
	}
	synchronized public void insertOrder(CookOrder5 o){
		 while (currentNum == capacity) {
			 try{
				 System.out.println("\tFull, waiting");
				 wait(5000); // Full, wait to add
			 } catch (InterruptedException ex) {};
			 }
		 insertItem(o);
		 currentNum++; 
	}
	
	synchronized public CookOrder5 removeOrder(){
			currentNum--; 
			return removeItem(); //ONLY REMOVES FIRST ITEM
		}
	
	private CookOrder5 removeItem(){
		if (cookorders.size() > 0){
			CookOrder5 o = cookorders.remove(0);
			return o; 
		}
		else {
			return null; 
		}
	}
	
	private void insertItem(CookOrder5 o){
		cookorders.add(o);
	}
}
