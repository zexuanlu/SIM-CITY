package restaurant6.interfaces;

import restaurant6.Restaurant6Invoice;

public interface Restaurant6Market {

	/**
	 * Sent by the cashier to the market indicating amount of payment
	 * @param payment
	 * @param invoice
	 */
	public abstract void msgHereIsThePayment(double payment, Restaurant6Invoice invoice);
}
