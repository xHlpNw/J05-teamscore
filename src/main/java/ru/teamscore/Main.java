package ru.teamscore;

import ru.teamscore.Task1.FlightDAO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        FlightDAO dao = new FlightDAO();

        System.out.print("Введите трёхбуквенный код аэропорта: ");
        String airportCode = scan.nextLine().trim();

        LocalDate date = null;
        while (date == null) {
            System.out.print("Введите дату (YYYY-MM-DD): ");
            String dateInput = scan.nextLine().trim();
            try {
                date = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректный формат даты.");
            }
        }

        List<String> flightsTable = dao.getFlights(airportCode, date);
        if (flightsTable.size() == 1) System.out.println("Рейсов не найдено");
        else {
            for (String flight : flightsTable) {
                System.out.println(flight);
            }
        }
    }
}
