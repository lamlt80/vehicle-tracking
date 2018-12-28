package com.quantuminventions.model;

import java.time.LocalDateTime;
import java.util.Map;

import com.quantuminventions.listeners.VehicleEventListener;

public class VehicleEvent {

	private long id;
	private String vehicleId;
	private Event event;
	private LocalDateTime eventTime;
	private int tripNo;	

	public VehicleEvent() {
	}

	public VehicleEvent(String vehicleId, Event event, LocalDateTime eventTime) {
		super();
		this.vehicleId = vehicleId;
		this.event = event;
		this.eventTime = eventTime;
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

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public LocalDateTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalDateTime eventTime) {
		this.eventTime = eventTime;
	}

	public int getTripNo() {
		return tripNo;
	}

	public void setTripNo(int tripNo) {
		this.tripNo = tripNo;
	}

	public enum Event {
		START, 
		NONE, 
		STOP
	}
	
	@Override
	public String toString() {
		return "VehicleEvent [vehicleId=" + vehicleId 
				+ ", event=" + event + ", eventTime=" + eventTime
				+ ", tripNo=" + tripNo + "]";
	}

	public boolean isStartEvent() {
		return this.event == Event.START;
	}
	
	public boolean isStopEvent() {
		return this.event == Event.STOP;
	}
	
	public boolean isNoneEvent() {
		return this.event == Event.NONE;
	}
	
	public void notifyListeners(Map<Event, VehicleEventListener> eventListenerMap) {
		VehicleEventListener listener = eventListenerMap.get(this.event);
		
		listener.update(this);
	}
}
