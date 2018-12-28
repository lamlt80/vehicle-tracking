package com.quantuminventions.api.restful.json;

import java.io.Serializable;
import java.time.LocalDateTime;

public class VehicleTripResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String vehicleId;
	private int tripNo;
	private String tripDuration;
	private LocalDateTime createdTime;

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
		return "VehicleTripResponse [id=" + id + ", vehicleId=" + vehicleId + ", tripNo=" + tripNo + ", tripDuration="
				+ tripDuration + ", createdTime=" + createdTime + "]";
	}
	
}
