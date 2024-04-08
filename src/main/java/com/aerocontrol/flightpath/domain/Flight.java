package com.aerocontrol.flightpath.domain;

import java.util.List;

public record Flight(
        Long number, // Номер польоту
        List<WayPoint> wayPoints, // Список вузлових точок
        List<TemporaryPoint> passedPoints// Список пройдених точок
) { }
