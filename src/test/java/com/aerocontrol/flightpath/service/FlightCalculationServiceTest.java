package com.aerocontrol.flightpath.service;

import com.aerocontrol.flightpath.domain.AirplaneCharacteristics;
import com.aerocontrol.flightpath.domain.TemporaryPoint;
import com.aerocontrol.flightpath.domain.WayPoint;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Log4j2
public class FlightCalculationServiceTest {

    private PlaneCalculationService service;
    private AirplaneCharacteristics characteristics;

    @BeforeEach
    void setUp() {
        service = new PlaneCalculationService();
        characteristics = new AirplaneCharacteristics(250, 10, 20, 5);
    }

    @Test
    void testSingleSegment() {
        WayPoint start = new WayPoint(0, 0, 1000, 250);
        WayPoint end = new WayPoint(0.03, 0.03, 1000, 250);

        List<TemporaryPoint> singleSegment = service
                .calculateSegment(start, end, characteristics);

        log.info("Single segment points count: {}", singleSegment.size());
        log.info("Single segment points count: {}", singleSegment);
        assertFalse(singleSegment.isEmpty(), "The single segment should not be empty");
    }

}

