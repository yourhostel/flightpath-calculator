package com.aerocontrol.flightpath.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TemporaryPoint{
    double latitude; // Широта
    double longitude; // Довгота
    double altitude; // Висота польоту м
    double speed; // Швидкість польоту м/с
    double course; // Курс град
}
