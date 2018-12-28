package com.quantuminventions.api.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class VehicleTrip implements Serializable {
	private static final long serialVersionUID = 1239630930779624194L;

	private long id;
	private String vehicleId;
	private int tripNo;
	private String tripDuration;
	private LocalDateTime createdTime;

	public VehicleTrip() {
	}

	public VehicleTrip(String vehicleId, int tripNo, String tripDuration) {
		super();
		this.vehicleId = vehicleId;
		this.tripNo = tripNo;
		this.tripDuration = tripDuration;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

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

	public LocalDateTime getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(LocalDateTime createdTime) {
		this.createdTime = createdTime;
	}

	@Override
	public String toString() {
		return "VehicleTrip [vehicleId=" + vehicleId + ", tripNo=" + tripNo + ", tripDuration=" + tripDuration + "]";
	}
}
