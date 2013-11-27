package person.interfaces;

import java.util.ArrayList;
import java.util.List;

import person.Restaurant;
import person.SimEvent;
import person.Position;
import simcity.CityMap;
import agent.Role;
import market.Food;

public interface Person {
           
        int homeNumber = 0;
        
		public abstract CityMap getMap();
       
		public abstract void msgNewHour(int hour);//from the world timer
        
        public abstract void msgAtDest(int x, int y);

        public abstract void msgAtDest(Position destination); // From the gui. now we can send the correct entrance message to the location manager

        public abstract void msgFinishedEvent(Role r, List<Food> foodList, double change);
        
        public abstract void msgFinishedEvent(Role r); //The location manager will send this message as the persons role leaves the building
        
        public abstract void msgAddMoney(double money); //give money back to the person's wallet 

        public abstract void msgNewBalance(double money); //set the inBank value of wallet
        
        public abstract void msgAddEvent(SimEvent e); //add an event to the person's scheduler
        
        public abstract void msgReadyToWork(Role r); //notify a waiting person that they can send their role to work
        
        public abstract void msgGoOffWork(Role r, double pay); //return the person's role and have them continue with their day
        
        public abstract void setStateChanged();

		public abstract int getTime();
		
}