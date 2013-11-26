package bank.interfaces;

/**
 * Interface of the Bank Guard, which is used solely for the non-norm of people
 * robbing banks
 * @author Joseph
 *
 */
public interface BankGuard {
	public void msgHelpMe(BankTeller bt, BankCustomer bc, String location);
	
}
