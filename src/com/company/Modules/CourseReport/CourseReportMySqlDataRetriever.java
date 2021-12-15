package com.company.Modules.CourseReport;

import com.company.Interfaces.CourseReportDataRetriever;
import com.company.Modules.InformationInput.InputData;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CourseReportMySqlDataRetriever implements CourseReportDataRetriever {
    private Connection sqlConnection;
    private String connectionUrlString;
    private String user;
    private String password;

    public CourseReportMySqlDataRetriever(String connectionUrlString, String user, String password) {
        this.connectionUrlString = connectionUrlString;
        this.user = user;
        this.password = password;
    }

    @Override
    public ArrayList<StudentReportData> BuildReport(InputData inputData) {
        ArrayList<StudentReportData> studentsData = getStudentsInformation(inputData);
        return studentsData;
    }

    private ArrayList<StudentReportData> getStudentsInformation(InputData inputData) {
        HashMap<String, StudentReportData> reportData = new HashMap<>();
        ArrayList<StudentReportData> studentsReportData = new ArrayList<>();
        ResultSet rs;
        try {
            sqlConnection = DriverManager.getConnection(connectionUrlString, user, password);
            Statement stmt = sqlConnection.createStatement();

            String sqlQuery = "SELECT sc.student_pin, " +
                    "c.name AS course," +
                    "c.total_time," +
                    "c.credit," +
                    "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                    "CONCAT(i.first_name, ' ',i.last_name) AS instructor_name " +
                    "FROM coursera.students_courses_xref AS sc " +
                    "INNER JOIN coursera.courses AS c ON sc.course_id = c.id " +
                    "INNER JOIN coursera.students AS s ON sc.student_pin = s.pin " +
                    "INNER JOIN coursera.instructors As i ON c.instructor_id=i.id " +
                    "WHERE sc.completion_date IS NOT NULL " +
                    "AND sc.completion_date BETWEEN CAST('" + inputData.getStartDate() + "' AS DATE) AND CAST('" + inputData.getEndDate() + "' AS DATE)";

            List<String> studentsPins = inputData.getStudentsPins();
            if (studentsPins != null && studentsPins.size() > 0) {
                String pins = String.join(",", studentsPins);
                sqlQuery += " AND s.pin IN (" + pins + ")";
            }


            rs = stmt.executeQuery(sqlQuery);

            while (rs.next()) {
                String studentPin = rs.getString("student_pin");

                CourseReportData courseReportData = new CourseReportData();
                courseReportData.setCourseName(rs.getString("course"));
                courseReportData.setCredit(rs.getInt("credit"));
                courseReportData.setInstructorName(rs.getString("instructor_name"));
                courseReportData.setTotalTime(rs.getInt("total_time"));

                StudentReportData studentReportData = null;

                if (reportData.containsKey(studentPin)) {
                    studentReportData = reportData.get(studentPin);
                    int allCredits = studentReportData.getOverallCredits();
                    studentReportData.setOverallCredits(allCredits + rs.getInt("credit"));
                } else {
                    studentReportData = new StudentReportData();
                    studentReportData.setStudentName(rs.getString("student_name"));
                    studentReportData.setOverallCredits(rs.getInt("credit"));
                }
                studentReportData.addCompletedCourses(courseReportData);

                reportData.put(studentPin, studentReportData);
            }

            reportData.forEach((k, v) -> {

                if (v.getOverallCredits() >= inputData.getMinAmountOfCredits()) {
                    studentsReportData.add(v);
                    System.out.println(k + " " + v.getStudentName() + " " + v.getOverallCredits());

                }
            });

            sqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentsReportData;
    }
}
