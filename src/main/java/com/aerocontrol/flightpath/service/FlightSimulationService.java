package com.aerocontrol.flightpath.service;

import com.aerocontrol.flightpath.repository.AirplaneRepository;
import com.aerocontrol.utils.EX;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
@Log4j2
@Service
@AllArgsConstructor
public class FlightSimulationService {

    private final AirplaneRepository airplaneRepository;
    private final PlaneCalculationService planeCalculationService;

    @Scheduled(fixedRate = 1000)
    public void simulateFlight() {
       throw EX.NI;
    }
}
