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
public class NoneEventListener implements VehicleEventListener {

	private static final Logger log = LoggerFactory.getLogger(NoneEventListener.class);

	private EventProcessingRepository epRepository;

	@Autowired
	public NoneEventListener(
			EventProcessingProperties epProperties,
			EventProcessingRepository epRepository) {
		this.epRepository = epRepository;
	}

	@Override
	@Transactional
	public void update(VehicleEvent vehicleEvent) {
		log.info("Insert NONE event: {}", vehicleEvent);
		epRepository.insertNoneEvent(vehicleEvent);
	}
}
