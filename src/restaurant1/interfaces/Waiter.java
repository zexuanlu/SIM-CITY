package restaurant1.interfaces;



public interface Waiter {
	public abstract void msgIWantFood(Customer cust, int table, int loc);

	public abstract void msgreadytoorder(Customer customer);
	
	public abstract void msgAnimationDoneAtTable(Customer customer);
	
	public abstract void msgorderisready(Customer customer, String choice, int table);
	
	public abstract void msgorderiscooked(int table);
	
	public abstract void msgatCook();
	
	public abstract void msgordertotable(Customer customer);
	
	
	public abstract void msgLeavingTable(Customer c) ;
	
	

	public abstract void msgAtTable() ;
	
	public abstract void msgIsback();

	public abstract void msgOutOfFood(int table);
	
	public abstract void IwantBreak();
	
	public abstract void msgHereistheCheck(Customer c, double p);

}
