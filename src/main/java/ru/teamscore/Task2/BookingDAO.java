package ru.teamscore.Task2;

import java.sql.*;
import java.time.LocalDate;

public class BookingDAO {
    private final String url = "jdbc:postgresql://localhost:5432/demo";
    private final String user = "postgres";
    private final String password = "";

    public void updatePrice(
            int flightNumber,
            String fareConditions,
            LocalDate bookDate,
            double newPrice) {
        String updateTicketPriceQuery = """
                UPDATE segments s
                SET price = ?
                FROM flights f
                JOIN tickets t ON TRUE
                JOIN bookings b ON b.book_ref = t.book_ref
                WHERE s.flight_id = f.flight_id
                    AND s.ticket_no = t.ticket_no
                    AND f.flight_id = ?
                    AND b.book_date::date = ?
                    AND s.fare_conditions = ?;
                """;

        String updateTotalAmountQuery = """
                UPDATE bookings b
                SET total_amount = sub.sum_amount
                FROM (
                    SELECT t.book_ref, SUM(s.price) AS sum_amount
                    FROM tickets t
                    JOIN segments s ON t.ticket_no = s.ticket_no
                    JOIN flights f ON f.flight_id = s.flight_id
                    WHERE f.flight_id = ?
                        AND s.fare_conditions = ?
                    GROUP BY t.book_ref
                ) sub
                WHERE b.book_ref = sub.book_ref
                """;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            try (PreparedStatement psUpdatePrice =
                         conn.prepareStatement(updateTicketPriceQuery);
                 PreparedStatement psUpdateTotal =
                         conn.prepareStatement(updateTotalAmountQuery)) {
                psUpdatePrice.setDouble(1, newPrice);
                psUpdatePrice.setInt(2, flightNumber);
                psUpdatePrice.setDate(3, Date.valueOf(bookDate));
                psUpdatePrice.setString(4, fareConditions);
                psUpdatePrice.executeUpdate();

                psUpdateTotal.setInt(1, flightNumber);
                psUpdateTotal.setString(2, fareConditions);
                psUpdateTotal.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
