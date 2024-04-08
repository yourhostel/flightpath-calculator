package com.aerocontrol.flightpath.service;

import com.aerocontrol.flightpath.domain.AirplaneCharacteristics;
import com.aerocontrol.flightpath.domain.TemporaryPoint;
import com.aerocontrol.flightpath.domain.WayPoint;
import lombok.extern.log4j.Log4j2;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.markers.SeriesMarkers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Log4j2
public class FlightVisualizationService {
    private final PlaneCalculationService service = new PlaneCalculationService();
    private final AirplaneCharacteristics characteristics = new AirplaneCharacteristics(250, 10, 20, 5);
    private static final List<WayPoint> wayPoints = new ArrayList<>(Arrays.asList(
            new WayPoint(0, 0, 0, 0),
            new WayPoint(0.01, 0.01, 100, 80),
            new WayPoint(0.04, -0.02, 80, 150),
            new WayPoint(0.07, 0.03, 70, 160),
            new WayPoint(0.09, -0.04, 90, 50),
            new WayPoint(0.1, 0, 0, 0)
    ));
    private static final int CHART_WIDTH = 800;
    private static final int CHART_HEIGHT = 600;

    public void displayFlightSegmentChart() {

        List<TemporaryPoint> singleSegment = service
                .calculateRoute(characteristics, wayPoints);

        // Створення та заповнення масивів даних для графіка швидкості
        double[] times = new double[singleSegment.size()];
        double[] speeds = new double[singleSegment.size()];
        for (int i = 0; i < singleSegment.size(); i++) {
            times[i] = i; // Просте припущення, що кожна точка відповідає 1 секунді
            speeds[i] = singleSegment.get(i).getSpeed();
        }

        // Створення графіка
        XYChart chart = new XYChartBuilder()
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title("Speed over Time")
                .xAxisTitle("Time, s")
                .yAxisTitle("Speed, m/s")
                .build();

        // Додавання даних на графік
        XYSeries series = chart.addSeries("Speed", times, speeds);
        series.setMarker(SeriesMarkers.NONE);

        // Відображення графіка
        SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    public void displayAltitudeChart() {

        List<TemporaryPoint> singleSegment = service
                .calculateRoute(characteristics, wayPoints);

        // Заповнення масивів даних для графіка висоти
        double[] times = new double[singleSegment.size()];
        double[] altitudes = new double[singleSegment.size()];
        for (int i = 0; i < singleSegment.size(); i++) {
            times[i] = i;
            altitudes[i] = singleSegment.get(i).getAltitude(); // Отримуємо висоту з точки
        }

        XYChart chart = new XYChartBuilder()
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title("Altitude over Time")
                .xAxisTitle("Time, s")
                .yAxisTitle("Altitude, m")
                .build();

        XYSeries altitudeSeries = chart.addSeries("Altitude", times, altitudes);
        altitudeSeries.setMarker(SeriesMarkers.NONE);

        SwingWrapper<XYChart> sw = new SwingWrapper<>(chart);
        sw.displayChart();
    }

    public void displayFlightPathChart() {
        List<TemporaryPoint> flightPath = service.calculateRoute(characteristics, wayPoints);

        // Масиви для широти та довготи
        double[] latitudes = new double[flightPath.size()];
        double[] longitudes = new double[flightPath.size()];

        // Заповнення масивів даними широти та довготи
        for (int i = 0; i < flightPath.size(); i++) {
            latitudes[i] = flightPath.get(i).getLatitude();
            longitudes[i] = flightPath.get(i).getLongitude();
        }

        // Створення графіка траєкторії польоту
        XYChart flightPathChart = new XYChartBuilder()
                .width(CHART_WIDTH)
                .height(CHART_HEIGHT)
                .title("Flight Path")
                .xAxisTitle("Longitude")
                .yAxisTitle("Latitude")
                .build();

        XYSeries flightPathSeries = flightPathChart.addSeries("Flight Path", longitudes, latitudes);
        flightPathSeries.setMarker(SeriesMarkers.NONE);

        flightPathChart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Line);
        flightPathChart.getStyler().setChartTitleVisible(true);
        flightPathChart.getStyler().setLegendVisible(false);

        new SwingWrapper<>(flightPathChart).displayChart();
    }

}
