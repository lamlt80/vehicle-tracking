package com.quantuminventions.listeners;

import java.time.Duration;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;
import com.quantuminventions.model.VehicleTrip;
import com.quantuminventions.repository.EventProcessingRepository;

@Component
public class StopEventListener implements VehicleEventListener {
	
	private static final Logger log = LoggerFactory.getLogger(StopEventListener.class);
	
	private String webserviceURL;
	private final EventProcessingRepository epRepository;
	private final RestTemplate restTemplate;
	
	@Autowired
	public StopEventListener(
			EventProcessingProperties epProperties,
			EventProcessingRepository epRepository,
			RestTemplate restTemplate) {
		this.epRepository = epRepository;
		this.restTemplate = restTemplate;
		this.webserviceURL = (epProperties != null 
								&& epProperties.getEventProcessing() != null) ? 
								epProperties.getEventProcessing().getWebserviceURL() : "";
	}

	@Override
	public void update(VehicleEvent vehicleEvent) {
		
		long id = insertStopEvent(vehicleEvent);
		
		Optional<VehicleEvent> stopEvent = epRepository.findById(id);
		Optional<VehicleEvent> startEvent = 
				epRepository.findByVehicleIdAndEventAndTripNumber(
						stopEvent.get().getVehicleId(), 
						Event.START.name(), 
						stopEvent.get().getTripNo());
		
		if (startEvent.isPresent()) {
			log.info("startEvent: {}", startEvent);
			log.info("stopEvent: {}", stopEvent.get());
			
			Duration duration = Duration.between(startEvent.get().getEventTime(), stopEvent.get().getEventTime());
			
			VehicleTrip trip = new VehicleTrip();
			trip.setVehicleId(stopEvent.get().getVehicleId());
			trip.setTripNo(stopEvent.get().getTripNo());
			trip.setTripDuration(duration.toString());
			
			log.info("trip send to web service: {}", trip.toString());
			
		    restTemplate.postForObject(webserviceURL, trip, VehicleTrip.class);
		}
	}
	
	@Transactional
	private long insertStopEvent(VehicleEvent vehicleEvent) {
		log.info("Insert STOP event: {}", vehicleEvent);
		return epRepository.insertVehicleEvent(vehicleEvent);
	}
}
