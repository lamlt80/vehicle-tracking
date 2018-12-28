package com.quantuminventions.listeners;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;
import com.quantuminventions.repository.EventProcessingRepository;

public class NoneEventListenerTest {
	private static EmbeddedDatabase db;
	
	private static EventProcessingProperties epProperties;
	private static EventProcessingRepository epRepository;
	private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private VehicleEvent noneEvent;
	
	private static NoneEventListener noneEventListener;
	
	@BeforeClass
	public static void classIni() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("create-db-test.sql")
				.build();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(db);
		epRepository = new EventProcessingRepository(namedParameterJdbcTemplate);
		
		epProperties  = new EventProcessingProperties();
		noneEventListener = new NoneEventListener(epProperties, epRepository);
	}
	
	@AfterClass
	public static void destroy() {
		db.shutdown();
	}
	
	@Before
	public void methodIni() {
		noneEvent = new VehicleEvent();
		noneEvent.setVehicleId("V1");
		noneEvent.setEvent(Event.NONE);
		noneEvent.setEventTime(LocalDateTime.now());
	}
	
	@Test
	public void updateMethodTest() {
		noneEventListener.update(noneEvent);
		List<Optional<VehicleEvent>> rsList = epRepository.findByVehicleIdAndEvent("V1", Event.NONE.name());
		
		Assert.assertFalse(rsList.isEmpty());
		Assert.assertEquals("V1", rsList.get(0).get().getVehicleId());
		Assert.assertEquals(Event.NONE, rsList.get(0).get().getEvent());
	}
}
