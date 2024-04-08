package com.aerocontrol.flightpath.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Airplane {
    private Long id;
    private AirplaneCharacteristics characteristics;
    private TemporaryPoint position;
    private List<Flight> flights;

}
