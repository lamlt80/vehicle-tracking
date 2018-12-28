package com.quantuminventions.listeners;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.repository.EventProcessingRepository;

@Component
public class StartEventListener implements VehicleEventListener {
	private static final Logger log = LoggerFactory.getLogger(StartEventListener.class);
	
	private EventProcessingRepository epRepository;
	
	@Autowired
	public StartEventListener(
			EventProcessingProperties epProperties,
			EventProcessingRepository epRepository) {
		this.epRepository = epRepository;
	}

	@Override
	@Transactional
	public void update(VehicleEvent vehicleEvent) {
		log.info("Insert START event: {}", vehicleEvent);
		epRepository.insertStartEvent(vehicleEvent);		
	}

}
