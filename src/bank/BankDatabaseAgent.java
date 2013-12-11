package bank;

import java.util.*;

import utilities.restaurant.RestaurantCashier;
import bank.test.mock.*;
import agent.*;
import bank.interfaces.*;

/**
 * This class is an agent that behaves as a computer system, handling
 * requests from bank tellers to manipulate bank accounts and sends
 * the info back to the tellers
 * 
 * @author Joseph Boman
 *
 */
public class BankDatabaseAgent extends Agent implements BankDatabase {
	
	//Data
	public EventLog log;//Used for testing
	public Map<Integer,Account> accounts;//A map of all of the accounts mapped to the account numbers
	public List<Request> requests = Collections.synchronizedList(new ArrayList<Request>());//A list of requests from the tellers
	public Double totalMoney;
	Timer timer = new Timer();
	
	/**
	 * The constructor for the bank database, which initializes the map
	 */
	public BankDatabaseAgent(){
		accounts = new HashMap<Integer, Account>();
		log = new EventLog();
		totalMoney = 1000000.00;
	}
	
	//Messages
	/**
	 * Received from the bank teller requesting a new account
	 * 
	 * @param bc the customer to create the account for
	 * @param bt the teller requesting the new account
	 */
	public void msgOpenAccount(BankCustomer bc, BankTeller bt){
		requests.add(new Request(bt, bc));
		log.add(new LoggedEvent("Received msgOpenAccount from BankTeller"));
		stateChanged();
	}
	
	/**
	 * Received from a bank teller requesting a deposit of money to an account
	 * 
	 * @param bc the customer who wants to deposit money
	 * @param money the amount of money to be deposited
	 * @param accountNumber the account number to be deposited into
	 * @param bt the teller who requested the deposit
	 */
	public void msgDepositMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		requests.add(new Request("deposit", accounts.get(accountNumber), money, bt, bc));
		log.add(new LoggedEvent("Received msgDepositMoney from BankTeller"));
		stateChanged();
	}
	
	/**
	 * Received from Restaurant Cashier at the end of the day to 
	 * deposit left over money
	 * 
	 * @param rc the Restaurant Cashier
	 * @param money the amount to be deposited
	 * @param accountNumber the account number of the account
	 */
	public void msgDepositMoney(RestaurantCashier rc, double money, int accountNumber){
		requests.add(new Request(rc, "deposit", accounts.get(accountNumber), money));
		log.add(new LoggedEvent("Received msgDepositMoney from RestaurantCashier"));
		stateChanged();
	}
	
	/**
	 * Received from a bank teller requesting a withdrawal of money from an account
	 * 
	 * @param bc the customer who wants to withdraw money
	 * @param money the amount of money to be withdrawn
	 * @param accountNumber the account number to be withdrawn from
	 * @param bt the teller who requested the withdrawal
	 */
	public void msgWithdrawMoney(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		requests.add(new Request("withdraw", accounts.get(accountNumber), money, bt, bc));
		log.add(new LoggedEvent("Received msgWithdrawMoney from BankTeller"));
		stateChanged();
	}
	
	/**
	 * Received from a restaurant cashier who needs to withdraw money
	 * at any time during the day
	 * 
	 * @param rc the restaurant cashier
	 * @param money the amount to be withdrawn
	 * @param accountNumber the account number of the account
	 */
	public void msgWithdrawMoney(RestaurantCashier rc, double money, int accountNumber){
		requests.add(new Request(rc, "withdraw", accounts.get(accountNumber), money));
		log.add(new LoggedEvent("Received msgWithdrawMoney from RestaurantCashier"));
		stateChanged();
	}
	
	/**
	 * Received from a bank teller requesting a loan of some amount
	 * 
	 * @param bc the customer who wants the loan
	 * @param money the amount of money that the customer wants
	 * @param accountNumber the customer's account number
	 * @param bt the teller who requested the loan
	 */
	public void msgLoanPlease(BankCustomer bc, double money, int accountNumber, BankTeller bt){
		requests.add(new Request("getLoan", accounts.get(accountNumber), money, bt, bc));
		log.add(new LoggedEvent("Received msgLoanPlease from BankTeller"));
		stateChanged();
	}

	/**
	 * Received from a bank teller who is being robber
	 * 
	 * @param bt the bank teller who is being robbed
	 * @param amount the amount of money that the robber demanded
	 */
	public void msgGiveAllMoney(BankTeller bt, double amount) {
		requests.add(new Request("robbery", null, amount, bt, null));
		log.add(new LoggedEvent("Received msgGiveAllMoney from BankTeller"));
		stateChanged();
	}
	/**
	 * The scheduler for the bank database
	 * 
	 * @return true if it has a request to perform
	 * @return false if it has no requests
	 */
	public boolean pickAndExecuteAnAction(){
		//If there are requests in the system
		if(!requests.isEmpty()){
			performBankAction(requests.get(0));
			return true;
		}
		//If there are no requests in the system
		return false;
	}
	
	//Actions
	/**
	 * Selects the appropriate action, performs it by changing the account's info
	 * and messages the bank teller with the appropriate action
	 * 
	 * @param r the request to be executed
	 */
	private void performBankAction(Request r){
		/**
		 * Opens a new account with an account number between 1 and 1000.
		 */
		if(r.type.equals("openAccount")){
			int AccountNumber = (int)((Math.random()*1000)+1);
			while(accounts.containsKey(AccountNumber)){
				AccountNumber = (int)((Math.random()*1000)+1);
			}
			Do("Creating Account Number " + AccountNumber);
			accounts.put(AccountNumber, new Account(r.bc, 0, AccountNumber));
			r.bt.msgAccountCreated(AccountNumber, r.bc);
			requests.remove(r);
			return;
		}
		/**
		 * Deposits the money into the appropriate account
		 * If the account has debt, half of the deposit goes to paying off the debt
		 */
		if(r.type.equals("deposit")){
			if(r.bc == null){
				totalMoney += r.amount;
				r.a.balance += r.amount;
				if(r.rc != null)
					r.rc.msgAddMoney(0-r.amount);
				requests.remove(r);
				return;
			}
			else{
				if(r.a.debt == 0){
					Do("Completed deposit of " + r.amount);
					r.a.balance += r.amount;
				}
				else{
					r.a.balance += (r.amount/2.0);
					r.a.debt -= (r.amount/2.0);
					if(r.a.debt < 0){
						r.a.balance -= r.a.debt;
						r.a.debt = 0;
					}
				}
				totalMoney += r.amount;
				r.bt.msgDepositDone(r.a.balance, r.bc);
				requests.remove(r);
			return;
			}
		}
		/**
		 * Withdraws some amount of money from the account. 
		 * If they have less than the amount, withdraws everything
		 */
		if(r.type.equals("withdraw")){
			if(r.bc == null){
				totalMoney -= r.amount;
				r.a.balance -= r.amount;
				if(r.rc != null)
					r.rc.msgAddMoney(r.amount);
				requests.remove(r);
				return;
			}
			else{
				if(r.a.balance > r.amount){
					r.a.balance -= r.amount;
					totalMoney -= r.amount;
				}
				else{
					r.amount = r.a.balance;
					totalMoney -= r.amount;
					r.a.balance = 0;
				}
				Do("Completed withdrawal of " + r.amount);
				r.bt.msgWithdrawDone(r.a.balance, r.amount, r.bc);
			}
			requests.remove(r);
		}
		/**
		 * If a customer has no debt and a quarter of the amount of the 
		 * loan, grants them a loan. Otherwise, denies the loan
		 */
		if(r.type.equals("getLoan")){
			if(r.a.debt == 0 && r.a.balance > r.amount/4){
				r.a.debt = r.amount;
				totalMoney -= r.amount;
				r.bt.msgLoanGranted(r.amount, r.a.debt, r.bc);
			}
			else{
				r.bt.msgRequestFailed(r.bc, "getLoan");
			}
			requests.remove(r);
		}
		/**
		 * Gives the teller who is being robbed the correct amount of money
		 */
		if(r.type.equals("robbery")){
			totalMoney -= r.amount;
			r.bt.msgHereIsMoney(r.amount);
			requests.remove(r);
		}
		/**
		 * If the money drops below some amount, then message the national
		 * branch to get more money
		 */
		if(totalMoney < 10000.0){
			timer.schedule(new TimerTask() {
				public void run() {
					totalMoney = 1000000.0;
				}
			},
				10000);
		}
}
	
	//Utilities
	
	/**
	 * Returns Bank Database
	 */
	public String toString(){
		return "Bank Database";
	}
	
	/**
	 * The class that is a request from the tellers.
	 * Contains a type, an amount, an account, a customer, and a teller
	 * 
	 * @author Joseph Boman
	 *
	 */
	public class Request{
		public String type;
		public double amount;
		public Account a;
		public BankCustomer bc;
		public BankTeller bt;
		public RestaurantCashier rc;
		Request(String type, Account a, double amount, BankTeller bt, BankCustomer bc){
			this.type = type;
			this.amount = amount;
			this.a = a;
			this.bt = bt;
			this.bc = bc;
		}
		public Request(BankTeller bt2, BankCustomer bc2) {
			type = "openAccount";
			bt = bt2;
			bc = bc2;
		}
		public Request(RestaurantCashier rc, String type, Account a, double amount){
			this.type = type;
			this.amount = amount;
			this.a = a;
			this.rc = rc;
			bc = null;
		}
	}
	
	/**
	 * The account class. Contains a balance, an account number, an owner, and
	 * a debt amount.
	 * @author Joseph
	 *
	 */
	public class Account{
		public double balance;
		public int accountNumber;
		public BankCustomer owner;
		public RestaurantCashier cashier;
		public double debt;
		Account(BankCustomer owner, double balance, int accountNumber){
			this.owner = owner;
			this.balance = balance;
			this.accountNumber = accountNumber;
		}
		Account(RestaurantCashier cashier, double balance, int accountNumber){
			this.cashier = cashier;
			this.balance = balance;
			this.accountNumber = accountNumber;
		}
	}
	
	//Used for testing
	public void addAccount(BankCustomer owner, double balance, int accountNumber){
		accounts.put(accountNumber, new Account(owner, balance, accountNumber));
		totalMoney += balance;
	}

	//Used to create restaurant accounts at program start
	//After all, the restaurants are well established companies with accounts
	public void addRestaurantAccount(RestaurantCashier cashier, double balance, int accountNumber){
		accounts.put(accountNumber, new Account(cashier, balance, accountNumber));
		totalMoney += balance;
	}
}
