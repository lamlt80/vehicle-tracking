package com.quantuminventions.api.restful.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quantuminventions.api.model.VehicleTrip;
import com.quantuminventions.api.restful.json.VehicleTripRequest;
import com.quantuminventions.api.restful.json.VehicleTripResponse;
import com.quantuminventions.api.restful.mapper.JsonDomainMapper;
import com.quantuminventions.api.service.TripService;

@RestController
@RequestMapping("/trip")
public class TripController {
	
	private static final Logger log = LoggerFactory.getLogger(TripController.class);
	
	private final TripService tripService;
	
	@Autowired
	public TripController(TripService tripService) {
		this.tripService = tripService;
	}
	
	@GetMapping("/{vehicleId}")
	public ResponseEntity<List<VehicleTripResponse>> findByVehicleId(@NotBlank @PathVariable String vehicleId) {
		log.info("Find trips for vehicle with id: {}", vehicleId);
		List<VehicleTrip> vehicleTrips = tripService.findByVehicleId(vehicleId);
		
		return ResponseEntity.ok(JsonDomainMapper.domainToJson(vehicleTrips));
	}
	
	@GetMapping("/{vehicleId}/{tripNo}")
	public ResponseEntity<VehicleTripResponse> findByVehicleIdAndTripNumber(
			@NotBlank @PathVariable String vehicleId, @NotNull @PathVariable long tripNo) {
		log.info("Find trip for vehicle with id {} and trip number: {}", vehicleId, tripNo);
		VehicleTrip vehicleTrip = tripService.findByVehicleIdAndTripNumber(vehicleId, tripNo);
		
		return ResponseEntity.ok(JsonDomainMapper.domainToJson(vehicleTrip));
	}
	
	@PostMapping
	public ResponseEntity<VehicleTripResponse> addTrip(@NotNull @Valid @RequestBody VehicleTripRequest vehicleTripReq) {
		log.info("Add new trip with info: {}", vehicleTripReq);
		VehicleTrip vehicleTrip = tripService.insert(JsonDomainMapper.jsonRequestToDomain(vehicleTripReq));
		
		return ResponseEntity.ok(JsonDomainMapper.domainToJson(vehicleTrip));
	}

}
