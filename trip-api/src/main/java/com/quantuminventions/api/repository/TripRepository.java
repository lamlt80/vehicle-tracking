package com.quantuminventions.api.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.quantuminventions.api.model.VehicleTrip;

@Repository
public class TripRepository {

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	public TripRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	
	public VehicleTrip findById(long id) {
		String sql = "SELECT * FROM vehicle_trip WHERE id=:id";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("id", id);
		
		List<VehicleTrip> rsList = namedParameterJdbcTemplate.query(sql, params, new VehicleTripMapper());
		if (rsList.isEmpty()) {
			return null;
		} else {
			return rsList.get(0);
		}
	}
	
	public List<VehicleTrip> findByVehicleId(String vehicleId) {
		String sql = "SELECT * FROM vehicle_trip WHERE vehicle_id=:vehicleId";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
		
		return namedParameterJdbcTemplate.query(sql, params, new VehicleTripMapper());
	}
	
	public VehicleTrip findByVehicleIdAndTripNumber(String vehicleId, long tripNo) {
		String sql = "SELECT * FROM vehicle_trip WHERE vehicle_id=:vehicleId AND trip_no = :tripNo";
		
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", vehicleId);
		params.put("tripNo", tripNo);
		
		List<VehicleTrip> rsList = namedParameterJdbcTemplate.query(sql, params, new VehicleTripMapper());
		if (rsList.isEmpty()) {
			return null;
		} else {
			return rsList.get(0);
		}
	}

	public long insertTrip(VehicleTrip trip) {

		String sql = "INSERT INTO vehicle_trip (vehicle_id, trip_no, trip_duration, created_time) VALUES "
					+ "(:vehicleId, :tripNo, :tripDuration, :createdTime)";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("vehicleId", trip.getVehicleId());
		params.put("tripNo", trip.getTripNo());
		params.put("tripDuration", trip.getTripDuration());
		params.put("createdTime", LocalDateTime.now());
		
		SqlParameterSource paramSource = new MapSqlParameterSource(params);
		KeyHolder keyHolder = new GeneratedKeyHolder();

		namedParameterJdbcTemplate.update(sql, paramSource, keyHolder);

		return keyHolder.getKey().longValue();
	}
	
	private static final class VehicleTripMapper implements RowMapper<VehicleTrip> {
		
		public VehicleTrip mapRow(ResultSet rs, int rowNum) throws SQLException {
			if (rs.wasNull()) {
				return null;
			}
			
			VehicleTrip vehicleTrip = new VehicleTrip();
			vehicleTrip.setId(rs.getLong("id"));
			vehicleTrip.setVehicleId(rs.getString("vehicle_id"));
			vehicleTrip.setTripNo(rs.getInt("trip_no"));
			vehicleTrip.setTripDuration(rs.getString("trip_duration"));
			vehicleTrip.setCreatedTime(rs.getTimestamp("created_time").toLocalDateTime());
			
			return vehicleTrip;
		}
	}

}
