package com.quantuminventions.model;

import java.io.Serializable;

public class VehicleTrip implements Serializable {
	private static final long serialVersionUID = 1L;

	private String VehicleId;
	private long tripNo;
	private String tripDuration;

	public String getVehicleId() {
		return VehicleId;
	}

	public void setVehicleId(String vehicleId) {
		VehicleId = vehicleId;
	}

	public long getTripNo() {
		return tripNo;
	}

	public void setTripNo(long tripNo) {
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
		return "VehicleTrip [VehicleId=" + VehicleId + ", tripNo=" + tripNo + ", tripDuration=" + tripDuration + "]";
	}
}
