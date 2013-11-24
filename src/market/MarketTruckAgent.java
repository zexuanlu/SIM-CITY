package market;

import agent.*;

import java.util.*;

import market.interfaces.MarketCashier;
import market.interfaces.Cook;
import market.interfaces.MarketTruck;

public class MarketTruckAgent extends Agent implements MarketTruck{

	List<Food> order;
	Cook cook;
	MarketCashier cashier;
	public enum state{receivingOrder, sending }
	public state s;
	
	public void setCashier(MarketCashier cashier){
		this.cashier = cashier;
	}
	
	public void msgPleaseDiliver(Cook c, List<Food> food){
		s = state.receivingOrder;
		cook = c;
		order = food;
		stateChanged();
		}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		// TODO Auto-generated method stub
		
		if(s == state.receivingOrder){
			SendOrder();
			return true;
		}
		
		return false;
	}
	
	void SendOrder(){
		s = state.sending;
		//DoSendOrder();
		cook.msgHereisYourFood(order);
		//DoGoBack();
		cashier.msgTruckBack(this);
		}

}
