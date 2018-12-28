package com.quantuminventions.api.service;

import java.util.List;

import com.quantuminventions.api.model.VehicleTrip;

public interface TripService {
	
	List<VehicleTrip> findByVehicleId(String vehicleId);
	
	VehicleTrip findByVehicleIdAndTripNumber(String vehicleId, long tripNo);
	
	VehicleTrip insert(VehicleTrip vehicleTrip);

}
