package restaurant6;

import java.util.Vector;

public class Restaurant6RevolvingStand extends Object {
    private final int N = 5;
    private int count = 0;
    private Vector<Restaurant6Order> theData;
    
    public Restaurant6RevolvingStand(){
        theData = new Vector<Restaurant6Order>();
    }
    
    synchronized public void insert(Restaurant6Order data) {
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
    
    synchronized public Restaurant6Order remove() {
    	Restaurant6Order data;
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
    
    private void insert_item(Restaurant6Order data){
        theData.addElement(data);
    }
    
    private Restaurant6Order remove_item(){
    	Restaurant6Order data = (Restaurant6Order) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }

}