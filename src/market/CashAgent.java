package market;

import marketinterface.Cashier;
import markettestmock.EventLog;
import agent.Agent;

public class CashAgent extends Agent{
	EventLog log = new EventLog();
	public void msgPleasepaytheBill(Cashier c, double bill){
		
	}
	@Override
	protected boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		return false;
	}

}
