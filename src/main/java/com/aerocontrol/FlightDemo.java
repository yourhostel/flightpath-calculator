package com.aerocontrol;

import com.aerocontrol.flightpath.service.FlightVisualizationService;

public class FlightDemo {
    public static void main(String[] args) {
        FlightVisualizationService visualizationService = new FlightVisualizationService();
        visualizationService.displayFlightSegmentChart();
        visualizationService.displayAltitudeChart();
        visualizationService.displayFlightPathChart();
    }
}
