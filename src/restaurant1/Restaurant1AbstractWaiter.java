package restaurant1;

import restaurant1.interfaces.Restaurant1Customer;
import agent.Agent;
import agent.Role;
import person.interfaces.Person;

public abstract class Restaurant1AbstractWaiter extends Role {

	public enum state 
	{available, waiting, seated, readytoorder, askedtoorder, attable, ordered, gotocook, orderready,outoffood, eating,atcook, checkingbill, bringbill, bringattable, starteating, done};
	
	
	public Restaurant1AbstractWaiter(Person pa) {
		super(pa);
	}
	
	public String name;
}	

