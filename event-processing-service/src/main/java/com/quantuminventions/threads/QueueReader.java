package com.quantuminventions.threads;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.handler.EventHandler;
import com.quantuminventions.model.VehicleEvent;

@Component
public class QueueReader {
	private static final Logger log = LoggerFactory.getLogger(QueueReader.class);
	
	private final Queue<VehicleEvent> queue;
	private final EventProcessingProperties epProperties;
	private static ExecutorService executor;
	private EventHandler eventHandler;
	
	@Autowired
	public QueueReader(Queue<VehicleEvent> queue, EventProcessingProperties epProperties,
			EventHandler eventHandler) {
		this.queue = queue;
		this.epProperties = epProperties;
		this.eventHandler = eventHandler;
		
		executor = Executors.newFixedThreadPool(this.epProperties.getEventProcessing().getThreadPoolSize());
	}
	
	public void read() {
		VehicleEvent vehicleEvent = queue.poll();
		
		while (vehicleEvent != null) {
			log.info("polled event: {}", vehicleEvent);
			
			final VehicleEvent eventToProcess = vehicleEvent;
			Runnable eventRunnable = () -> eventHandler.handle(eventToProcess);
			executor.execute(eventRunnable);
			
			vehicleEvent = queue.poll();
		}
	}
}
