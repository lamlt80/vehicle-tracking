package com.quantuminventions.api.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quantuminventions.api.model.VehicleTrip;
import com.quantuminventions.api.repository.TripRepository;
import com.quantuminventions.api.service.TripService;

@Service
@Transactional
public class TripServiceImpl implements TripService {
	
	private final TripRepository tripRepository;
	
	@Autowired
	public TripServiceImpl(TripRepository tripRepository) {
		this.tripRepository = tripRepository;
	}

	public List<VehicleTrip> findByVehicleId(String vehicleId) {
		return tripRepository.findByVehicleId(vehicleId);
	}

	public VehicleTrip findByVehicleIdAndTripNumber(String vehicleId, long tripNo) {
		return tripRepository.findByVehicleIdAndTripNumber(vehicleId, tripNo);
	}

	public VehicleTrip insert(VehicleTrip vehicleTrip) {
		long id = tripRepository.insertTrip(vehicleTrip);
		return tripRepository.findById(id);
	}
}
