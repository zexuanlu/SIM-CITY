package person;

/* Restaurant is a specified location so that the person can call the right methods for the host 
 * expect a specified location class for all places on the map (i.e. Bus, Bank, Market)
 * */


public class Restaurant extends Location{
	
	private Host host;
	
	Restaurant(Host host){
		this.host = host;
	}
}
