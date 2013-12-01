package bank.interfaces;

/** 
 * A basic BankHost interface containing the basic messaging calls
 * 
 * @author Joseph
 *
 */
public interface BankHost {
	
	public void msgINeedTeller(BankCustomer bc);
	
	public void msgBackToWork(BankTeller bt);
	
	public void msgAtDestination();
	
	public void addTeller(BankTeller bt);
}
