package com.quantuminventions.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quantuminventions.listeners.NoneEventListener;
import com.quantuminventions.listeners.StartEventListener;
import com.quantuminventions.listeners.StopEventListener;
import com.quantuminventions.listeners.VehicleEventListener;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;

@Service
public class EventHandler {
	
	private static final Logger log = LoggerFactory.getLogger(EventHandler.class);

	private static final Map<Event, VehicleEventListener> eventListenerMap = new ConcurrentHashMap<>();
	
	@Autowired
	public EventHandler(StartEventListener startEventListener,
			StopEventListener stopEventListener,
			NoneEventListener noneEventListener) {
		this.eventListenerMap.put(Event.START, startEventListener);
		this.eventListenerMap.put(Event.STOP, stopEventListener);
		this.eventListenerMap.put(Event.NONE, noneEventListener);
	}
	
	public void handle(VehicleEvent vehicleEvent) {
		log.info("Handle event: {}", vehicleEvent);
		vehicleEvent.notifyListeners(eventListenerMap);
	}
}
