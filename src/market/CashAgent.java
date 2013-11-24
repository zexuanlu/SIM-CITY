package market;

import market.interfaces.MarketCashier;
import market.test.mock.EventLog;
import agent.Agent;

public class CashAgent extends Agent{
	EventLog log = new EventLog();
	public void msgPleasepaytheBill(MarketCashier c, double bill){
		
	}
	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
