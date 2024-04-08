package com.aerocontrol.flightpath.service;

import com.aerocontrol.flightpath.domain.AirplaneCharacteristics;
import com.aerocontrol.flightpath.domain.TemporaryPoint;
import com.aerocontrol.flightpath.domain.WayPoint;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
public class PlaneCalculationService {

    public static final double EARTH_RADIUS = 6_371_000;

    /**
     * Розраховує маршрут для літака на основі його характеристик та наданих контрольних точок.
     *
     * @param characteristics Характеристики літака.
     * @param wayPoints Список контрольних точок.
     * @return Список проміжних точок маршруту, що включає географічні координати, швидкість та напрямок на кожній з точок.
     * @throws IllegalArgumentException якщо список контрольних точок містить менше двох елементів.
     * Метод проходить через весь список контрольних точок, розраховує сегменти між кожною парою сусідніх точок
     * і генерує проміжні точки маршруту. Це дає можливість створити деталізований маршрут руху літака.
     */
    public List<TemporaryPoint> calculateRoute(AirplaneCharacteristics characteristics,
                                               List<WayPoint> wayPoints) {
        log.info("Розрахунок маршруту для літака з характеристиками: {}", characteristics);
        List<TemporaryPoint> route = new ArrayList<>();
        if (wayPoints.size() < 2) {
            throw new IllegalArgumentException("Для розрахунку маршруту потрібно щонайменше дві контрольні точки.");
        }

        for (int i = 0; i < wayPoints.size() - 1; i++) {
            WayPoint start = wayPoints.get(i);
            WayPoint end = wayPoints.get(i + 1);

            List<TemporaryPoint> segmentPoints = calculateSegment(start, end, characteristics);
            route.addAll(segmentPoints);
        }

        return route;
    }

    /**
     * Розраховує сегмент маршруту між двома контрольними точками.
     *
     * @param start Початкова точка сегмента.
     * @param end Кінцева точка сегмента.
     * @param characteristics Характеристики літака.
     * @return Список проміжних точок для даного сегмента маршруту.
     * Метод використовує лінійну інтерполяцію для розрахунку проміжних
     * точок маршруту на основі відстані між контрольними точками
     * та зміни швидкості та висоти між ними. Швидкість між точками
     * змінюється лінійно від початкової до кінцевої швидкості.
     */
    public List<TemporaryPoint> calculateSegment(WayPoint start, WayPoint end,
                                                 AirplaneCharacteristics characteristics) {
        log.debug("Calculating segment from {} to {} for characteristics {}", start, end, characteristics);
        List<TemporaryPoint> segmentPoints = new ArrayList<>();
        double distance = calculateDistance(start, end);
        double course = calculateCourse(start, end);

        // Розрахунок максимального часу польоту на основі дистанції та максимальної швидкості
        // double flightTime = distance / characteristics.maxSpeed();

        // Розрахунок максимального часу польоту на основі дистанції та середньої швидкості точок сегмента
        double flightTime = distance / ((start.speed() + end.speed()) / 2);

        // Початкові умови
        double elapsedTime = 0; // витрачений час
        double currentSpeed = start.speed();
        double currentAltitude = start.altitude();

        // Розрахунок етапів зміни
        double maxAcceleration = characteristics.maxAcceleration();
        // Розрахунок прискорення до середньої швидкості з максимальним прискоренням літака
        double speedChange = Math.min((end.speed() - start.speed()) / flightTime, maxAcceleration);

        double altitudeChange = (end.altitude() - start.altitude()) / flightTime; // Швидкість зміни висоти

        while (elapsedTime <= flightTime) {
            // Додаємо точку що секунди
            segmentPoints.add(new TemporaryPoint(
                    interpolateLatitude(start.latitude(), end.latitude(), elapsedTime, flightTime),
                    interpolateLongitude(start.longitude(), end.longitude(), elapsedTime, flightTime),
                    currentAltitude,
                    currentSpeed,
                    course
            ));

            elapsedTime += 1; // Збільшуємо час, що минув, на крок часу
            currentSpeed += speedChange; // Оновлюємо поточну швидкість
            currentAltitude += altitudeChange; // Оновлюємо поточну висоту
        }

        // Додаємо кінцеву точку для точності
        segmentPoints.add(new TemporaryPoint(end.latitude(),
                end.longitude(),
                end.altitude(),
                end.speed(),
                course));

        return segmentPoints;
    }

    /**
     * Розраховує відстань між двома точками за їхніми географічними координатами.
     *
     * @param start Початкова точка шляху з географічними координатами широти та довготи.
     * @param end   Кінцева точка шляху з географічними координатами широти та довготи.
     * @return Відстань між початковою та кінцевою точками в метрах.
     * Формула використовує гаверсинуси для розрахунку відстаней на сферичній поверхні землі, що дозволяє
     * отримати високу точність вимірювань навіть на великі відстані. Результат може мати невелику
     * помилку через те, що Земля не є ідеальною сферою.
     */
    private double calculateDistance(WayPoint start, WayPoint end) {
        double latDistance = Math.toRadians(end.latitude() - start.latitude());
        double lonDistance = Math.toRadians(end.longitude() - start.longitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(start.latitude())) * Math.cos(Math.toRadians(end.latitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }

    /**
     * Розраховує курс руху від початкової точки до кінцевої.
     *
     * @param start Початкова точка шляху з географічними координатами широти та довготи.
     * @param end   Кінцева точка шляху з географічними координатами широти та довготи.
     * @return Курс від початкової до кінцевої точки у градусах відносно північного напрямку.
     * Метод використовує тригонометричні функції для обчислення напрямку (азимуту) руху.
     * Результат представляє собою кут між напрямком на північний полюс з початкової точки
     * та лінією, що з'єднує початкову та кінцеву точки. Кут відліковується за годинниковою стрілкою,
     * де 0 градусів відповідає північному напрямку, 90 градусів — східному, 180 градусів — південному,
     * та 270 градусів — західному.
     */
    private double calculateCourse(WayPoint start, WayPoint end) {
        double y = Math.sin(Math.toRadians(end.longitude() - start.longitude())) * Math.cos(Math.toRadians(end.latitude()));
        double x = Math.cos(Math.toRadians(start.latitude())) * Math.sin(Math.toRadians(end.latitude()))
                - Math.sin(Math.toRadians(start.latitude())) * Math.cos(Math.toRadians(end.latitude()))
                * Math.cos(Math.toRadians(end.longitude() - start.longitude()));

        return (Math.toDegrees(Math.atan2(y, x)) + 360) % 360;
    }

    /**
     * Виконує лінійну інтерполяцію широти між двома точками.
     *
     * @param startLat Широта початкової точки.
     * @param endLat Широта кінцевої точки.
     * @param currentPosition Поточна позиція на шляху від початкової до кінцевої точки.
     * @param totalDistance Загальна дистанція між початковою та кінцевою точками.
     * @return Широта інтерпольованої точки.
     * Метод розраховує проміжну широту на основі заданої поточної позиції, що є часткою від загальної дистанції.
     * Використано для визначення широти проміжної точки під час розрахунку маршруту.
     */
    private double interpolateLatitude(double startLat,
                                       double endLat,
                                       double currentPosition,
                                       double totalDistance) {
        return startLat + (endLat - startLat) * (currentPosition / totalDistance);
    }

    /**
     * Виконує лінійну інтерполяцію довготи між двома точками.
     *
     * @param startLon Довгота початкової точки.
     * @param endLon Довгота кінцевої точки.
     * @param currentPosition Поточна позиція на шляху від початкової до кінцевої точки.
     * @param totalDistance Загальна дистанція між початковою та кінцевою точками.
     * @return Довгота інтерпольованої точки.
     * Метод розраховує проміжну довготу на основі заданої поточної позиції, що є часткою від загальної дистанції.
     * Використано для визначення довготи проміжної точки під час розрахунку маршруту.
     */
    private double interpolateLongitude(double startLon,
                                        double endLon,
                                        double currentPosition,
                                        double totalDistance) {
        return startLon + (endLon - startLon) * (currentPosition / totalDistance);
    }

}



