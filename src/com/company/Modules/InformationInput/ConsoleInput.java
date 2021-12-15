package com.company.Modules.InformationInput;

import com.company.Interfaces.DataInputing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsoleInput implements DataInputing {

    @Override
    public InputData HandleInput() throws Exception {

        System.out.print("Students to be included in the report: ");
        Scanner scan = new Scanner(System.in);

        List<String> studentsPins = new ArrayList<>();

        String input = scan.nextLine();
        if (input.isEmpty()) {
            System.out.println("All students");
        } else {
            studentsPins = Arrays.asList(input.split(",", -1));
        }
        System.out.print("Minimum credits required: ");
        Integer minAmountOfCredits = Integer.valueOf(scan.nextLine());

        System.out.print("Start day (yyyy-MM-dd): ");
        String inputDate = scan.nextLine();
        LocalDate startDate = parseDate(inputDate);

        System.out.print("End day (yyyy-MM-dd): ");
        inputDate = scan.nextLine();
        LocalDate endDate = parseDate(inputDate);

        System.out.print("File format (html or csv, press Enter for both):");
        String format = scan.nextLine();

        System.out.print("File path directory:");
        String filePath = scan.nextLine();

        InputData inputData = new InputData();
        inputData.setStudentsPins(studentsPins);
        inputData.setMinAmountOfCredits(minAmountOfCredits);
        inputData.setStartDate(startDate);
        inputData.setEndDate(endDate);
        inputData.setFormat(checkFormat(format.toLowerCase()));
        inputData.setFilePath(filePath);

        return inputData;
    }

    private static LocalDate parseDate(String inputDate) throws Exception {

        List<String> dateParts = Arrays.asList(inputDate.split("-", -1));
        if (dateParts.size() != 3) {
            throw new Exception("Invalid date format (yyyy-MM-dd)");
        }

        LocalDate date = LocalDate.parse(inputDate);
        return date;
    }

    private static Format checkFormat(String format) {
        switch (format) {
            case "html":
                return Format.HTML;
            case "csv":
                return Format.CSV;
            default:
                return Format.All;
        }
    }
}
