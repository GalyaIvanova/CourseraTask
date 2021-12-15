package com.company;

import com.company.Interfaces.CourseReportDataRetriever;
import com.company.Interfaces.DataInputing;
import com.company.Interfaces.StudentCoursesExporter;
//import com.company.Modules.DataInputing;
import com.company.Modules.CourseReport.CourseReportMySqlDataRetriever;
import com.company.Modules.CourseReport.StudentReportData;
import com.company.Modules.InformationInput.ConsoleInput;
import com.company.Modules.InformationInput.Format;
import com.company.Modules.InformationInput.InputData;
import com.company.Modules.StudentCoursesExport.StudentCoursesExporterCSV;
import com.company.Modules.StudentCoursesExport.StudentCoursesExporterHTML;


import java.util.*;

//TODO: Go to row 15 to change the jdbc user and password.

public class Main {
    public static void main(String[] args) throws Exception {

        DataInputing dataInputing =new ConsoleInput();
        InputData inputData = dataInputing.HandleInput();
        CourseReportDataRetriever courseReportDataRetriever = new CourseReportMySqlDataRetriever("jdbc:mysql://localhost/coursera", "root", "password123!");
        ArrayList<StudentReportData> reportData = courseReportDataRetriever.BuildReport(inputData);

        if(inputData.getFormat()== Format.HTML || inputData.getFormat()==Format.All){
            StudentCoursesExporter studentCoursesExporter = new StudentCoursesExporterHTML();
            studentCoursesExporter.Export(reportData, inputData.getFilePath()+"report.html");
        }
        if(inputData.getFormat()==Format.CSV || inputData.getFormat()==Format.All) {
            StudentCoursesExporter studentCoursesExporter = new StudentCoursesExporterCSV();
            studentCoursesExporter.Export(reportData, inputData.getFilePath()+"report.csv");
        }
    }

}



