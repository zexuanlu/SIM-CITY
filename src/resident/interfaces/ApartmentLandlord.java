package resident.interfaces;

import resident.interfaces.ApartmentTenant;
import resident.interfaces.MaintenancePerson;

public interface ApartmentLandlord {

	public abstract void msgHereIsTheRent(MaintenancePerson maintenancePersonAgent, double rentCost);

	public abstract void msgHereIsTheRent(ApartmentTenant apartmentTenantAgent, double myMoney);
}
