package com.company.Interfaces;

import com.company.Modules.InformationInput.InputData;
import com.company.Modules.CourseReport.StudentReportData;

import java.util.ArrayList;

public interface CourseReportDataRetriever {
    ArrayList<StudentReportData> BuildReport(InputData inputData);
}
