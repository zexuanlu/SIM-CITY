package restaurant2.interfaces;

import restaurant2.Restaurant2CashierRole;
import restaurant2.Check;
import restaurant2.Table;

public interface Restaurant2Market {
	
	public abstract void msgRestockMe();
	public abstract void msgHeresPayment(Restaurant2CashierRole cashier, int payment);
	public abstract void msgHeresLatePayment(Restaurant2CashierRole cashier, int payment);
	
}
