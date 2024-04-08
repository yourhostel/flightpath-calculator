package com.aerocontrol.flightpath.repository;

import com.aerocontrol.flightpath.domain.Flight;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightRepository extends MongoRepository<Flight, Long> { }
