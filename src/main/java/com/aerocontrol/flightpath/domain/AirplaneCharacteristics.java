package com.aerocontrol.flightpath.domain;

public record AirplaneCharacteristics(
        double maxSpeed, // Максимальна швидкість м/с
        double maxAcceleration, // Максимальне прискорення м/с^2
        double altitudeChangeSpeed, // Швидкість зміни висоти м/с
        double courseChangeSpeed // Швидкість зміни курсу град/с
) { }
