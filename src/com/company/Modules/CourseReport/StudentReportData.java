package com.company.Modules.CourseReport;

import com.company.Modules.CourseReport.CourseReportData;

import java.util.ArrayList;
import java.util.List;

public class StudentReportData {
    private String studentName;
    private int overallCredits;
    private List<CourseReportData> courses;

    public StudentReportData() {
        courses = new ArrayList<>();
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getOverallCredits() {
        return overallCredits;
    }

    public void setOverallCredits(int overallCredits) {
        this.overallCredits = overallCredits;
    }

    public List<CourseReportData> getCourses() {
        return courses;
    }

    public void addCompletedCourses(CourseReportData course) {
        this.courses.add(course);
    }
}
