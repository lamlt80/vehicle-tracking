package com.quantuminventions.listeners;

import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.web.client.RestTemplate;

import com.quantuminventions.config.EventProcessingProperties;
import com.quantuminventions.config.EventProcessingProperties.EventProcessing;
import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;
import com.quantuminventions.model.VehicleTrip;
import com.quantuminventions.repository.EventProcessingRepository;

@RunWith(MockitoJUnitRunner.class)
public class StopEventListenerTest {
	
	private static EmbeddedDatabase db;

	@Mock
	private RestTemplate restTemplate;
	
	@Mock
	private EventProcessingProperties epProperties;
	
	@Mock
	private EventProcessingRepository epRepository;

	@InjectMocks
	private StopEventListener stopEventListener;

	private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	private static VehicleEvent stopEvent;
	
	@BeforeClass
	public static void classIni() {
		MockitoAnnotations.initMocks(StopEventListenerTest.class);
		
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("create-db-test.sql")
				.build();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(db);
	}
	
	@AfterClass
	public static void destroy() {
		db.shutdown();
	}

	@Before
	public void methodIni() {
		epProperties = new EventProcessingProperties();
		EventProcessing eventProcessing = new EventProcessing();
		eventProcessing.setWebserviceURL("http://localhost:8989/api/trip");
		epProperties.setEventProcessing(eventProcessing);

		epRepository = new EventProcessingRepository(namedParameterJdbcTemplate);
		stopEventListener = new StopEventListener(epProperties, epRepository, restTemplate);
		
		stopEvent = new VehicleEvent();
		stopEvent.setVehicleId("V1");
		stopEvent.setEvent(Event.STOP);
		stopEvent.setEventTime(LocalDateTime.now());
	}

	@Test
	public void updateMethodTestWithStopEventOnly() {
		
		LocalDateTime localDateTime = LocalDateTime.now();
		stopEvent.setEventTime(localDateTime);
		stopEventListener.update(stopEvent);
		List<Optional<VehicleEvent>> rsList = epRepository.findByVehicleIdAndEvent("V1", Event.STOP.name());

		Assert.assertFalse(rsList.isEmpty());
		
		VehicleEvent justInserted = rsList.stream()
				.filter(e -> e.get().getEventTime().getMinute() == localDateTime.getMinute())
				.findFirst()
				.get().get();
		
		Assert.assertEquals("V1", justInserted.getVehicleId());
		Assert.assertEquals(Event.STOP, justInserted.getEvent());
		Assert.assertEquals(0, justInserted.getTripNo());
	}

	@Test
	public void updateMethodTestWithStartAndStopEvents() {
		VehicleEvent startEvent = new VehicleEvent();
		startEvent.setVehicleId("V1");
		startEvent.setEvent(Event.START);
		startEvent.setEventTime(stopEvent.getEventTime().minusMinutes(30));
		epRepository.insertStartEvent(startEvent);
		
		VehicleTrip wsReturn = new VehicleTrip();
		wsReturn.setVehicleId("V1");
		wsReturn.setTripNo(1);
		wsReturn.setTripDuration(Duration.between(startEvent.getEventTime(), stopEvent.getEventTime()).toString());
		
		when(restTemplate.postForObject(
				Mockito.eq("http://localhost:8989/api/trip"), 
				Mockito.eq(VehicleTrip.class), 
				Mockito.eq(VehicleTrip.class)))
		.thenReturn(wsReturn);
		
		stopEvent.setEventTime(LocalDateTime.now().minusMinutes(10));
		stopEventListener.update(stopEvent);
		List<Optional<VehicleEvent>> rsList = epRepository.findByVehicleIdAndEvent("V1", Event.STOP.name());

		Assert.assertFalse(rsList.isEmpty());
		
		VehicleEvent justInserted = rsList.stream()
				.filter(e -> e.get().getEventTime().getMinute() == stopEvent.getEventTime().getMinute())
				.findFirst()
				.get().get();
		Assert.assertEquals("V1", justInserted.getVehicleId());
		Assert.assertEquals(Event.STOP, justInserted.getEvent());
		Assert.assertEquals(1, justInserted.getTripNo());
	}

}
