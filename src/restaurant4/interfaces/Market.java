package restaurant4.interfaces;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Market {
	
	public void msgPayingBill(double bill);

	public void msgCannotPay(String contract);
}	