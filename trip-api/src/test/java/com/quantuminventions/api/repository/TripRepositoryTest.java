package com.quantuminventions.api.repository;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import com.quantuminventions.api.model.VehicleTrip;

public class TripRepositoryTest {
	
	private static EmbeddedDatabase db;
			
	private static TripRepository tripRepository;
	private static NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	private VehicleTrip trip;
	
	@BeforeClass
	public static void classIni() {
		final EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		db = builder
				.setType(EmbeddedDatabaseType.HSQL)
				.addScript("create-db.sql")
				.build();
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(db);
		tripRepository = new TripRepository(namedParameterJdbcTemplate);
	}
	
	@AfterClass
	public static void destroy() {
		db.shutdown();
	}
	
	@Before
	public void init() {
		trip = new VehicleTrip();
		trip.setVehicleId("V1");
		trip.setTripNo(1);
		trip.setTripDuration("1H");
	}
	
	
	@Test
    public void insertStartEventTest() throws Exception {
		
		long id = tripRepository.insertTrip(trip);
		VehicleTrip vehicleTrip = tripRepository.findById(id);
		
		Assert.assertNotNull(vehicleTrip);
		Assert.assertEquals("V1", vehicleTrip.getVehicleId());
		Assert.assertEquals(1, vehicleTrip.getTripNo());
		Assert.assertEquals("1H", vehicleTrip.getTripDuration());
		
		deleteAll();
    }
	
	
	@Test
	public void findByVehicleIdAndTripNumberTest() {
		deleteAll();
		tripRepository.insertTrip(trip);
		
		VehicleTrip vehicleTrip = tripRepository.findByVehicleIdAndTripNumber("V1", 1);
		Assert.assertNotNull(vehicleTrip);
		Assert.assertEquals("V1", vehicleTrip.getVehicleId());
		Assert.assertEquals(1, vehicleTrip.getTripNo());
		Assert.assertEquals("1H", vehicleTrip.getTripDuration());
	}
	
	
	private void deleteAll() {
		String sql = "DELETE FROM vehicle_trip";
		Map<String, Object> params = new HashMap<>();
		namedParameterJdbcTemplate.update(sql, params);
	}

}
