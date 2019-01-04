package com.quantuminventions.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.quantuminventions.model.VehicleEvent;
import com.quantuminventions.model.VehicleEvent.Event;

@Repository
public class EventProcessingRepository {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	public EventProcessingRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	private static String INSERT_START_EVENT_SQL = "INSERT INTO vehicle_event (vehicle_id, event, event_time, trip_no) VALUES "
			+ "(:vehicleId, :event, :eventTime, (SELECT ISNULL(MAX(trip_no), '0') + 1 FROM vehicle_event WHERE vehicle_id = :vehicleId AND event = :event))";
	
	private static String INSERT_STOP_EVENT_SQL = "INSERT INTO vehicle_event (vehicle_id, event, event_time, trip_no) VALUES "
			+ "(:vehicleId, :event, :eventTime, "
			+ "(SELECT MAX(ve1.trip_no) FROM vehicle_event ve1 WHERE ve1.vehicle_id = :vehicleId and ve1.event = 'START' "
			+ "AND NOT EXISTS (SELECT trip_no FROM vehicle_event ve2 WHERE ve2.vehicle_id = :vehicleId and ve2.event = 'STOP' AND ve1.trip_no = ve2.trip_no))"
			+ ")";
	
	private static String INSERT_NONE_EVENT_SQL = "INSERT INTO vehicle_event (vehicle_id, event, event_time) VALUES (:vehicleId, :event, :eventTime)";
	
	private static Map<Event, String> eventSQLMap = new ConcurrentHashMap<>();
	private static RowMapper<Optional<VehicleEvent>> rowMapper;
	
	static {
		eventSQLMap.put(Event.START, INSERT_START_EVENT_SQL);
		eventSQLMap.put(Event.STOP, INSERT_STOP_EVENT_SQL);
		eventSQLMap.put(Event.NONE, INSERT_NONE_EVENT_SQL);
		
		rowMapper = (resultSet, rowNum) -> {
			if (resultSet.wasNull()) {
				return Optional.empty();
			}
			
			VehicleEvent vehicleEvent = new VehicleEvent();
			vehicleEvent.setId(resultSet.getLong("id"));
			vehicleEvent.setVehicleId(resultSet.getString("vehicle_id"));
			vehicleEvent.setEvent(Event.valueOf(resultSet.getString("event")));
			vehicleEvent.setEventTime((resultSet.getTimestamp("event_time").toLocalDateTime()));
			vehicleEvent.setTripNo(resultSet.getInt("trip_no"));
			
			return Optional.of(vehicleEvent);
		};
	}
	
	public Optional<VehicleEvent> findById(long id) {
		String sql = "SELECT * FROM vehicle_event WHERE id=:id";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, rowMapper);
		if (rsList.isEmpty()) {
			return Optional.empty();
		} else {
			return rsList.get(0);
		}
	}
	
	public List<Optional<VehicleEvent>> findByVehicleIdAndEvent(String vehicleId, String event) {
		String sql = "SELECT * FROM vehicle_event WHERE vehicle_id=:vehicleId AND event=:event";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
        params.put("event", event);
        
        return namedParameterJdbcTemplate.query(sql, params, rowMapper);
	}
	
	public Optional<VehicleEvent> findByVehicleIdAndEventAndTripNumber(String vehicleId, String event, long tripNo) {
		String sql = "SELECT * FROM vehicle_event WHERE vehicle_id=:vehicleId AND event=:event AND trip_no=:tripNo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
        params.put("event", event);
        params.put("tripNo", tripNo);
        
        List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, rowMapper);
		if (rsList.isEmpty()) {
			return Optional.empty();
		} else {
			return rsList.get(0);
		}
	}
	
	public Optional<VehicleEvent> findEventHasMaxTripNo(String vehicleId, String event) {
		String sql = "SELECT * FROM vehicle_event WHERE trip_no = (SELECT ISNULL(MAX(trip_no), '-1') "
					+ "FROM vehicle_event WHERE vehicle_id=:vehicleId AND event=:event)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
        params.put("event", event);
        
        List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, rowMapper);
		if (rsList.isEmpty()) {
			return Optional.empty();
		} else {
			return rsList.get(0);
		}
	}

	public long insertVehicleEvent(VehicleEvent vehicleEvent) {
		String sql = eventSQLMap.get(vehicleEvent.getEvent());
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", vehicleEvent.getVehicleId());
        params.put("event", vehicleEvent.getEvent().name());
        params.put("eventTime", vehicleEvent.getEventTime());
        
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
        
        return keyHolder.getKey().longValue();
	}

}
