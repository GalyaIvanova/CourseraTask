package com.company.Interfaces;

import com.company.Modules.CourseReport.StudentReportData;

import java.util.ArrayList;

public interface StudentCoursesExporter {
    void Export(ArrayList<StudentReportData> studentsReportData, String filePath);
}
