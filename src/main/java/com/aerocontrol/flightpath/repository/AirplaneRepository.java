package com.aerocontrol.flightpath.repository;

import com.aerocontrol.flightpath.domain.Airplane;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirplaneRepository extends MongoRepository<Airplane, Long> { }
