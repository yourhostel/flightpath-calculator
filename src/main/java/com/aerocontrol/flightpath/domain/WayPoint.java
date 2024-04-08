package com.aerocontrol.flightpath.domain;

public record WayPoint(
        double latitude, // Широта
        double longitude, // Довгота
         double altitude, // Висота прольоту м
        double speed // Швидкість прольоту м/с
) { }
