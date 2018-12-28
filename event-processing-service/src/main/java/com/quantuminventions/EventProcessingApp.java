package com.quantuminventions;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.client.RestTemplate;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.threads.QueueReader;
import com.quantuminventions.threads.QueueWriter;

@SpringBootApplication
public class EventProcessingApp {
	
	public static void main(String[] args) {
		SpringApplication.run(EventProcessingApp.class, args);
	}
	
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
		return new NamedParameterJdbcTemplate(dataSource);
	}
	
	@Autowired
	private EventProcessingProperties epProperties;
	
	@Autowired
	private QueueWriter writer;
	
	@Autowired
	private QueueReader reader;
	
	@Bean
	public Queue<VehicleEvent> queue() {
		return new ConcurrentLinkedDeque<>();
	}
	
	@EventListener(ApplicationReadyEvent.class)
	public void startEventProcessing() {
		final Runnable writerRunner = writer::write;
		final Runnable readerRunner = reader::read;
		
		int writeInterval = epProperties.getEventQueue().getWriter().getInterval();
		int readInterval = epProperties.getEventQueue().getReader().getInterval();
		
		final ScheduledExecutorService writerExecutorService = Executors.newSingleThreadScheduledExecutor();
		writerExecutorService.scheduleAtFixedRate(writerRunner, 0, writeInterval, TimeUnit.SECONDS);
		
		final ScheduledExecutorService readerExecutorService = Executors.newSingleThreadScheduledExecutor();
		readerExecutorService.scheduleAtFixedRate(readerRunner, 0, readInterval, TimeUnit.SECONDS);
	}
}
