package com.quantuminventions.api.restful.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.quantuminventions.api.model.VehicleTrip;
import com.quantuminventions.api.restful.json.VehicleTripRequest;
import com.quantuminventions.api.restful.json.VehicleTripResponse;

public class JsonDomainMapper {
	
	public static VehicleTrip jsonRequestToDomain(VehicleTripRequest vehicleTripReq) {
		if (vehicleTripReq == null) return null;
		
		VehicleTrip trip = new VehicleTrip();
		trip.setVehicleId(vehicleTripReq.getVehicleId());
		trip.setTripNo(vehicleTripReq.getTripNo());
		trip.setTripDuration(vehicleTripReq.getTripDuration());
		
		return trip;
	}
	
	
	public static VehicleTripResponse domainToJson(VehicleTrip vehicleTrip) {
		if (vehicleTrip == null) return null;
		
		VehicleTripResponse response = new VehicleTripResponse();
		response.setId(vehicleTrip.getId());
		response.setVehicleId(vehicleTrip.getVehicleId());
		response.setTripNo(vehicleTrip.getTripNo());
		response.setTripDuration(vehicleTrip.getTripDuration());
		response.setCreatedTime(vehicleTrip.getCreatedTime());
		
		return response;
	}
	
	public static List<VehicleTripResponse> domainToJson(List<VehicleTrip> vehicleTrips) {
		List<VehicleTripResponse> responses = new ArrayList<>();
		if (vehicleTrips == null || vehicleTrips.isEmpty()) {
			return responses;
		}
		
		vehicleTrips.stream()
		.filter(Objects::nonNull)
		.forEach(trip -> responses.add(JsonDomainMapper.domainToJson(trip)));
		
		return responses;
	}

}
