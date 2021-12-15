package com.company.Modules.InformationInput;

import java.time.LocalDate;
import java.util.List;

public class InputData {
    private List<String> studentsPins;
    private int minAmountOfCredits;
    private LocalDate startDate;
    private LocalDate endDate;
    private Format format;
    private String filePath;

    public List<String> getStudentsPins() {
        return studentsPins;
    }

    public void setStudentsPins(List<String> studentsPins) {
        this.studentsPins = studentsPins;
    }

    public int getMinAmountOfCredits() {
        return minAmountOfCredits;
    }

    public void setMinAmountOfCredits(int minAmountOfCredits) {
        this.minAmountOfCredits = minAmountOfCredits;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
