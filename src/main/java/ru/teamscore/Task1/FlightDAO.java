package ru.teamscore.Task1;

import java.sql.*;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FlightDAO {
    private final String url = "jdbc:postgresql://localhost:5432/demo";
    private final String user = "postgres";
    private final String password = "";

    public List<String> getFlights(String airportCode, LocalDate date) {
        List<String> results = new ArrayList<>();
        String query = """
            SELECT
                f.flight_id,
                f.scheduled_departure as scheduled_departure_time,
                f.scheduled_arrival as scheduled_arrival_time,
                f.actual_departure as actual_departure_time,
                f.actual_arrival as actual_arrival_time,
                r.departure_airport, r.arrival_airport,
                aa.city as arrival_city, da.city as departure_city,
                ap.model as airplane_model, f.status
            FROM flights f
            JOIN routes r ON f.route_no = r.route_no
            JOIN airports aa ON r.arrival_airport = aa.airport_code
            JOIN airports da ON r.departure_airport = da.airport_code
            JOIN airplanes ap ON r.airplane_code = ap.airplane_code
            WHERE
                (
                    (r.departure_airport = ? AND f.scheduled_departure::date = ?)
                    OR (r.arrival_airport   = ? AND f.scheduled_arrival::date   = ?)
                )
            ORDER BY
                CASE
                    WHEN r.departure_airport = ? THEN f.scheduled_departure
                    ELSE f.scheduled_arrival
                END
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, airportCode.toUpperCase());
            stmt.setDate(2, Date.valueOf(date));
            stmt.setString(3, airportCode.toUpperCase());
            stmt.setDate(4, Date.valueOf(date));
            stmt.setString(5, airportCode.toUpperCase());

            ResultSet rs = stmt.executeQuery();

            // Время вылета/прилёта +
            // Аэропорт вылета + город вылета +
            // Аэропорт прилёта + город прилёта +
            // название самолёта + статус вылета
            results.add(String.format("%5s %25s %25s %30s %9s",
                    "TIME", "FROM", "TO", "AIRPLANE", "STATUS"));

            while (rs.next()) {
                String fromAirport = rs.getString("departure_airport");
                String fromCity = rs.getString("departure_city");
                String toAirport = rs.getString("arrival_airport");
                String toCity = rs.getString("arrival_city");
                String airplane = rs.getString("airplane_model");
                String status = rs.getString("status");

                OffsetDateTime time;
                if (fromAirport.equalsIgnoreCase(airportCode)) {
                    time = rs.getObject(
                            "actual_departure_time",
                            OffsetDateTime.class);
                    if (time == null) {
                        time = rs.getObject(
                                "scheduled_departure_time",
                                OffsetDateTime.class);
                    }
                } else {
                    time = rs.getObject(
                            "actual_arrival_time",
                            OffsetDateTime.class);
                    if (time == null) {
                        time = rs.getObject(
                                "scheduled_arrival_time",
                                OffsetDateTime.class);
                    }
                }


                results.add(
                        String.format(
                                "%5s %20s(%3s) %20s(%3s) %30s %9s",
                                time.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                                fromCity,
                                fromAirport,
                                toCity,
                                toAirport,
                                airplane,
                                status
                        ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }
}
