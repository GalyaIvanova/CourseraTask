package com.company.Modules.StudentCoursesExport;

import com.company.Interfaces.StudentCoursesExporter;
import com.company.Modules.CourseReport.CourseReportData;
import com.company.Modules.CourseReport.StudentReportData;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class StudentCoursesExporterCSV implements StudentCoursesExporter {
    @Override
    public void Export(ArrayList<StudentReportData> studentsReportData, String filePath) {
        try (PrintWriter writer = new PrintWriter(filePath)) {
            StringBuilder sb = new StringBuilder();

            // CSV header part
            sb.append("Student");
            sb.append(',');
            sb.append("Credits");
            sb.append('\n');

            sb.append(",");
            sb.append("Course Name");
            sb.append(',');
            sb.append("Time");
            sb.append(',');
            sb.append("Credit");
            sb.append(',');
            sb.append("Instructor");
            sb.append('\n');

            // CSV 'body' part
            for (int i = 0; i < studentsReportData.size(); i++) {
                StudentReportData studentInfo = studentsReportData.get(i);

                sb.append(studentInfo.getStudentName());
                sb.append(',');
                sb.append(studentInfo.getOverallCredits());
                sb.append('\n');

                List<CourseReportData> courses = studentInfo.getCourses();
                for (int j = 0; j < courses.size(); j++) {
                    CourseReportData course = courses.get(j);

                    sb.append(",");
                    sb.append(course.getCourseName());
                    sb.append(',');
                    sb.append(course.getTotalTime());
                    sb.append(',');
                    sb.append(course.getCredit());
                    sb.append(',');
                    sb.append(course.getInstructorName());
                    sb.append('\n');
                }
            }
            writer.write(sb.toString());
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
