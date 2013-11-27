package person;

import resident.ApartmentTenantRole;

public class Apartment extends Location{
	
	private ApartmentTenantRole host;
	int homeNumber;
	public Apartment(String n, ApartmentTenantRole atr, Position p, int hn, LocationType type){
		super(n, type, p);
		this.host = atr;
		this.homeNumber = hn;
	}
	public ApartmentTenantRole getHost(){ return host; }
	public void setHost(ApartmentTenantRole h){ this.host = h; }
	public int getNumber(){ return this.homeNumber; }
}
