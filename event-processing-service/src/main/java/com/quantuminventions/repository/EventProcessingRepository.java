package com.quantuminventions.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	
	public Optional<VehicleEvent> findById(long id) {
		String sql = "SELECT * FROM vehicle_event WHERE id=:id";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, new VehicleEventMapper());
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
        
		return namedParameterJdbcTemplate.query(sql, params, new VehicleEventMapper());
	}
	
	public Optional<VehicleEvent> findByVehicleIdAndEventAndTripNumber(String vehicleId, String event, long tripNo) {
		String sql = "SELECT * FROM vehicle_event WHERE vehicle_id=:vehicleId AND event=:event AND trip_no=:tripNo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
        params.put("event", event);
        params.put("tripNo", tripNo);
        
		List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, new VehicleEventMapper());
		if (rsList.isEmpty()) {
			return Optional.empty();
		} else {
			return rsList.get(0);
		}
	}
	
	public Optional<VehicleEvent> findEventHasMaxTripNo(String vehicleId, String event) {
		String sql = "SELECT * FROM vehicle_event WHERE trip_no = (SELECT ISNULL(MAX(trip_no), '-1') FROM vehicle_event WHERE vehicle_id=:vehicleId AND event=:event)";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
        params.put("event", event);
        
        List<Optional<VehicleEvent>> rsList = namedParameterJdbcTemplate.query(sql, params, new VehicleEventMapper());
		if (rsList.isEmpty()) {
			return Optional.empty();
		} else {
			return rsList.get(0);
		}
	}

	public long insertStartEvent(VehicleEvent startEvent) {
		
		String sql = "INSERT INTO vehicle_event (vehicle_id, event, event_time, trip_no) VALUES "
					+ "(:vehicleId, :event, :eventTime, (SELECT ISNULL(MAX(trip_no), '0') + 1 FROM vehicle_event WHERE vehicle_id = :vehicleId AND event = :event))";
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", startEvent.getVehicleId());
        params.put("event", startEvent.getEvent().name());
        params.put("eventTime", startEvent.getEventTime());
        
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
        
        return keyHolder.getKey().longValue();
	}
	
	public long insertStopEvent(VehicleEvent stopEvent) {
		String sql = "INSERT INTO vehicle_event (vehicle_id, event, event_time, trip_no) VALUES "
				+ "(:vehicleId, :event, :eventTime, "
					+ "(SELECT MAX(ve1.trip_no) FROM vehicle_event ve1 WHERE ve1.vehicle_id = :vehicleId and ve1.event = 'START' "
					+ "AND NOT EXISTS (SELECT trip_no FROM vehicle_event ve2 WHERE ve2.vehicle_id = :vehicleId and ve2.event = 'STOP' AND ve1.trip_no = ve2.trip_no))"
				+ ")";
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", stopEvent.getVehicleId());
        params.put("event", Event.STOP.name());
        params.put("eventTime", stopEvent.getEventTime());
        
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
        
        return keyHolder.getKey().longValue();
	}
	
	public long insertNoneEvent(VehicleEvent noneEvent) {
		String sql = "INSERT INTO vehicle_event (vehicle_id, event, event_time) VALUES (:vehicleId, :event, :eventTime)";
		
		Map<String, Object> params = new HashMap<String, Object>();
        params.put("vehicleId", noneEvent.getVehicleId());
        params.put("event", Event.NONE.name());
        params.put("eventTime", noneEvent.getEventTime());
        
        SqlParameterSource paramSource = new MapSqlParameterSource(params);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        
        namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);
        
        return keyHolder.getKey().longValue();
	}
	
	private static final class VehicleEventMapper implements RowMapper<Optional<VehicleEvent>> {
		
		public Optional<VehicleEvent> mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (rs.wasNull()) {
				return Optional.empty();
			}
			
			VehicleEvent vehicleEvent = new VehicleEvent();
			vehicleEvent.setId(rs.getLong("id"));
			vehicleEvent.setVehicleId(rs.getString("vehicle_id"));
			vehicleEvent.setEvent(Event.valueOf(rs.getString("event")));
			vehicleEvent.setEventTime((rs.getTimestamp("event_time").toLocalDateTime()));
			vehicleEvent.setTripNo(rs.getInt("trip_no"));
			
			return Optional.of(vehicleEvent);
		}
	}

}
