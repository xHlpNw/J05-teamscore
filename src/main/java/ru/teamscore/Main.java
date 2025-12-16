package ru.teamscore;

import ru.teamscore.Task1.FlightDAO;
import ru.teamscore.Task2.BookingDAO;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        FlightDAO dao = new FlightDAO();

        System.out.println("Task1");
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

        System.out.println("\nTask2");
        System.out.print("Введите номер рейса: ");
        int flightNo = scan.nextInt();
        scan.nextLine();

        System.out.print("Введите класс обслуживаня (Economy/Comfort/Business): ");
        String fareConditions = scan.nextLine();

        LocalDate bookDate = null;
        while (bookDate == null) {
            System.out.print("Введите дату бронирования (YYYY-MM-DD): ");
            String dateInput = scan.nextLine().trim();
            try {
                bookDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("Некорректный формат даты.");
            }
        }

        System.out.print("Введите новую цену: ");
        double price = scan.nextDouble();

        BookingDAO bookingDao = new BookingDAO();
        bookingDao.updatePrice(flightNo, fareConditions, bookDate, price);
        System.out.println("Данные обновлены");
    }
}
