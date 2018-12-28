package com.quantuminventions.api.restful.json;

import java.io.Serializable;

public class VehicleTripRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private String vehicleId;
	private int tripNo;
	private String tripDuration;

	public String getVehicleId() {
		return vehicleId;
	}

	public void setVehicleId(String vehicleId) {
		this.vehicleId = vehicleId;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public String getTripDuration() {
		return tripDuration;
	}

	public void setTripDuration(String tripDuration) {
		this.tripDuration = tripDuration;
	}

	@Override
	public String toString() {
		return "VehicleTripRequest [vehicleId=" + vehicleId + ", tripNo=" + tripNo + 
				", tripDuration=" + tripDuration+ "]";
	}
}
