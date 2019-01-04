package com.quantuminventions.repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;

public class EventProcessingRepositoryTest {
	
	private static EmbeddedDatabase db;
	
	private static EventProcessingRepository eventRepository;
	private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private VehicleEvent startEvent;
	private VehicleEvent stopEvent;
	
	@BeforeClass
	public static void classIni() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("create-db-test.sql")
				.build();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(db);
		eventRepository = new EventProcessingRepository(namedParameterJdbcTemplate);
	}
	
	@AfterClass
	public static void destroy() {
		db.shutdown();
	}
	
	@Before
	public void methodIni() {
		startEvent = new VehicleEvent();
		startEvent.setVehicleId("V1");
		startEvent.setEvent(Event.START);
		startEvent.setEventTime(LocalDateTime.now());
		
		stopEvent = new VehicleEvent();
		stopEvent.setVehicleId("V1");
		stopEvent.setEvent(Event.STOP);
		stopEvent.setEventTime(LocalDateTime.now().plusMinutes(5));
	}
	
	@Test
    public void insertStartEventTest() throws Exception {
		deleteAll();
		long id = eventRepository.insertVehicleEvent(startEvent);
		Optional<VehicleEvent> insertedEvent = eventRepository.findById(id);
		
		Assert.assertTrue(insertedEvent.isPresent());
		Assert.assertEquals("V1", insertedEvent.get().getVehicleId());
		Assert.assertEquals(Event.START, insertedEvent.get().getEvent());
		
		Assert.assertEquals(1, insertedEvent.get().getTripNo());
		
    }
	
	@Test
    public void insertStopEventTest() throws Exception {
		deleteAll();
		long id = eventRepository.insertVehicleEvent(stopEvent);
		Optional<VehicleEvent> insertedEvent = eventRepository.findById(id);
		
		Assert.assertTrue(insertedEvent.isPresent());
		Assert.assertEquals("V1", insertedEvent.get().getVehicleId());
		Assert.assertEquals(Event.STOP, insertedEvent.get().getEvent());
		
		Assert.assertEquals(0, insertedEvent.get().getTripNo());
		
    }
	
	@Test
    public void insert2StopEventTest() throws Exception {
		deleteAll();
		long id = eventRepository.insertVehicleEvent(stopEvent);
		Optional<VehicleEvent> insertedEvent = eventRepository.findById(id);
		
		id = eventRepository.insertVehicleEvent(stopEvent);
		insertedEvent = eventRepository.findById(id);
		
		Assert.assertTrue(insertedEvent.isPresent());
		Assert.assertEquals("V1", insertedEvent.get().getVehicleId());
		Assert.assertEquals(Event.STOP, insertedEvent.get().getEvent());
		
		Assert.assertEquals(0, insertedEvent.get().getTripNo());
    }
	
	@Test
    public void insertStartStopEventsTest() throws Exception {
		deleteAll();
		long id = eventRepository.insertVehicleEvent(startEvent);
		Optional<VehicleEvent> insertedStartEvent = eventRepository.findById(id);
		
		id = eventRepository.insertVehicleEvent(stopEvent);
		Optional<VehicleEvent> insertedStopEvent = eventRepository.findById(id);
		
		Assert.assertTrue(insertedStartEvent.isPresent());
		Assert.assertEquals("V1", insertedStartEvent.get().getVehicleId());
		Assert.assertEquals(Event.START, insertedStartEvent.get().getEvent());
		
		Assert.assertTrue(insertedStopEvent.isPresent());
		Assert.assertEquals("V1", insertedStopEvent.get().getVehicleId());
		Assert.assertEquals(Event.STOP, insertedStopEvent.get().getEvent());
		
		Assert.assertEquals(1, insertedStopEvent.get().getTripNo());
		Assert.assertEquals(insertedStartEvent.get().getVehicleId(), insertedStopEvent.get().getVehicleId());
		Assert.assertEquals(insertedStartEvent.get().getTripNo(), insertedStopEvent.get().getTripNo());
    }
	
	@Test
    public void insertNoneEventTest() throws Exception {
		deleteAll();
		VehicleEvent noneEvent = stopEvent;
		noneEvent.setEvent(Event.NONE);
		long id = eventRepository.insertVehicleEvent(noneEvent);
		Optional<VehicleEvent> insertedEvent = eventRepository.findById(id);
		
		Assert.assertTrue(insertedEvent.isPresent());
		Assert.assertEquals("V1", insertedEvent.get().getVehicleId());
		Assert.assertEquals(Event.NONE, insertedEvent.get().getEvent());
    }
	
	@Test
    public void findEventHasMaxTripNoTestWithData() throws Exception {
		deleteAll();
		eventRepository.insertVehicleEvent(startEvent);
		eventRepository.insertVehicleEvent(startEvent);
		
		Optional<VehicleEvent> maxTripNoEvent = eventRepository.findEventHasMaxTripNo("V1", Event.START.name());
		Assert.assertTrue(maxTripNoEvent.isPresent());
		Assert.assertEquals("V1", maxTripNoEvent.get().getVehicleId());
		Assert.assertEquals(Event.START, maxTripNoEvent.get().getEvent());
		
		Assert.assertEquals(2, maxTripNoEvent.get().getTripNo());
    }
	
	@Test
    public void findEventHasMaxTripNoTestNoData() throws Exception {
		deleteAll();
		Optional<VehicleEvent> maxTripNoEvent = eventRepository.findEventHasMaxTripNo("V1", Event.START.name());
		
		Assert.assertTrue(!maxTripNoEvent.isPresent());
	}
	
	@Test
    public void findByVehicleIdAndEventTestWithData() throws Exception {
		deleteAll();
		eventRepository.insertVehicleEvent(startEvent);
		
		List<Optional<VehicleEvent>> maxTripNoEvents = eventRepository.findByVehicleIdAndEvent("V1", Event.START.name());
		Assert.assertFalse(maxTripNoEvents.isEmpty());
		Assert.assertTrue(maxTripNoEvents.get(0).isPresent());
		Assert.assertEquals("V1", maxTripNoEvents.get(0).get().getVehicleId());
		Assert.assertEquals(Event.START, maxTripNoEvents.get(0).get().getEvent());
		
		Assert.assertEquals(1, maxTripNoEvents.get(0).get().getTripNo());
    }
	
	@Test
    public void findByVehicleIdAndEventTestNoData() throws Exception {
		deleteAll();
		List<Optional<VehicleEvent>> maxTripNoEvents = eventRepository.findByVehicleIdAndEvent("V1", Event.START.name());
		
		Assert.assertTrue(maxTripNoEvents.isEmpty());
	}
	
	private void deleteAll() {
		String sql = "DELETE FROM vehicle_event";
		Map<String, Object> params = new HashMap<>();
		namedParameterJdbcTemplate.update(sql, params);
	}

}
