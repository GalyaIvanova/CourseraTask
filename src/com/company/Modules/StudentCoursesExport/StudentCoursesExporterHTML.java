package com.company.Modules.StudentCoursesExport;

import com.company.Interfaces.StudentCoursesExporter;
import com.company.Modules.CourseReport.CourseReportData;
import com.company.Modules.CourseReport.StudentReportData;
//import com.company.StudentReportData;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StudentCoursesExporterHTML implements StudentCoursesExporter {
    private static String htmlTemplate = "<table border=\"1\">\n" +
            "\t<thead>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td>Student</td>\n" +
            "\t\t\t<td>Total Credit</td>\n" +
            "\t\t\t<td></td>\n" +
            "\t\t\t<td></td>\n" +
            "\t\t\t<td></td>\n" +
            "\t\t</tr>\n" +
            "\t\t<tr>\n" +
            "\t\t\t<td></td>\n" +
            "\t\t\t<td>Course Name</td>\n" +
            "\t\t\t<td>Time</td>\n" +
            "\t\t\t<td>Credit</td>\n" +
            "\t\t\t<td>Instructor</td>\n" +
            "\t\t</tr>\n" +
            "\t</thead>\n" +
            "\t<tbody>\n" +
            "\t\t{0}\n" +
            "\t</tbody>\n" +
            "</table>";

    @Override
    public void Export(ArrayList<StudentReportData> studentsReportData, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            String tableRowsHtml = "";
            for (int i = 0; i < studentsReportData.size(); i++) {
                StudentReportData studentInfo = studentsReportData.get(i);
                tableRowsHtml += "<tr><td>" + studentInfo.getStudentName() + "</td><td>" + studentInfo.getOverallCredits() + "</td><td></td><td></td><td></td></tr>";

                List<CourseReportData> courses = studentInfo.getCourses();
                for (int j = 0; j < courses.size(); j++) {
                    CourseReportData course = courses.get(j);
                    tableRowsHtml += "<tr><td></td><td>" + course.getCourseName() + "</td><td>" + course.getTotalTime() + "</td><td>" + course.getCredit() + "</td><td>" + course.getInstructorName() + "</td></tr>";
                }
            }

            htmlTemplate = htmlTemplate.replace("{0}", tableRowsHtml);

            writer.write(htmlTemplate);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
