package restaurant4.interfaces;

/**
 * A sample Host interface built to unit test a CashierAgent.
 *
 * @author Joseph Boman
 *
 */
public interface Restaurant4Host {
	
	public abstract void msgTableFree(int table);

	public abstract void msgIWantBreak(Restaurant4Waiter restaurant4Waiter);

	public abstract void msgEndingBreak(Restaurant4Waiter restaurant4AbstractWaiter);

	public abstract void msgIWantFood(Restaurant4Customer restaurant4CustomerRole);

	public abstract void msgWaitTooLong(Restaurant4Customer restaurant4CustomerRole);

}