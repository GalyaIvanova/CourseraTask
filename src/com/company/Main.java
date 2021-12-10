package com.company;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

//TODO: Go to row 15 to change the jdbc user and password.

public class Main {
    public static void main(String[] args) throws Exception {

        DataInputing dataInputing =new ConsoleInput();
        InputData inputData = dataInputing.HandleInput();
        CourseReportDataRetriever courseReportDataRetriever = new CourseReportMySqlDataRetriever("jdbc:mysql://localhost/coursera", "root", "password123!");
        ArrayList<StudentReportData> reportData = courseReportDataRetriever.BuildReport(inputData);

        if(inputData.getFormat()==Format.HTML || inputData.getFormat()==Format.All){
            StudentCoursesExporter studentCoursesExporter = new StudentCoursesExporterHTML();
            studentCoursesExporter.Export(reportData, inputData.getFilePath()+"report.html");
        }
        if(inputData.getFormat()==Format.CSV || inputData.getFormat()==Format.All) {
            StudentCoursesExporter studentCoursesExporter = new StudentCoursesExporterCSV();
            studentCoursesExporter.Export(reportData, inputData.getFilePath()+"report.csv");
        }
    }

}

class InputData {
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

enum Format{
    All,
    HTML,
    CSV
}

interface CourseReportDataRetriever {
    ArrayList<StudentReportData> BuildReport(InputData inputData);
}

interface StudentCoursesExporter{
    void Export(ArrayList<StudentReportData> studentsReportData, String filePath);
}
interface DataInputing{
    InputData HandleInput() throws Exception;
}

class ConsoleInput implements DataInputing{

    @Override
    public InputData HandleInput() throws Exception {

            System.out.print("Students to be included in the report: ");
            Scanner scan = new Scanner(System.in);

            List<String> studentsPins = new ArrayList<>();

            String input=scan.nextLine();
            if(input.isEmpty()){
                System.out.println("All students");
            }else {
                studentsPins = Arrays.asList(input.split(",",-1));
            }
            System.out.print("Minimum credits required: ");
            Integer minAmountOfCredits=Integer.valueOf(scan.nextLine()) ;

            System.out.print("Start day (yyyy-MM-dd): ");
            String inputDate = scan.nextLine();
            LocalDate startDate = parseDate(inputDate);

            System.out.print("End day (yyyy-MM-dd): ");
            inputDate = scan.nextLine();
            LocalDate endDate = parseDate(inputDate);

            System.out.print("File format (html or csv, press Enter for both):");
            String format= scan.nextLine();

            System.out.print("File path directory:");
            String filePath= scan.nextLine();

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

        List<String> dateParts = Arrays.asList(inputDate.split("-",-1));
        if (dateParts.size() != 3) {
            throw new Exception("Invalid date format (yyyy-MM-dd)");
        }

        LocalDate date = LocalDate.parse(inputDate);
        return date;
    }
    private static Format checkFormat(String format){
        switch (format){
            case "html": return Format.HTML;
            case "csv": return Format.CSV;
            default: return Format.All;
        }
    }
}

class CourseReportMySqlDataRetriever implements  CourseReportDataRetriever{
    private Connection sqlConnection;
    private String connectionUrlString;
    private String user;
    private String password;

    public CourseReportMySqlDataRetriever(String connectionUrlString, String user, String password) {
        this.connectionUrlString=connectionUrlString;
        this.user=user;
        this.password=password;
    }

    @Override
    public ArrayList<StudentReportData> BuildReport(InputData inputData) {
        ArrayList<StudentReportData> studentsData=getStudentsInformation(inputData);
        return studentsData;
    }

  private ArrayList<StudentReportData> getStudentsInformation(InputData inputData){
        HashMap<String,StudentReportData> reportData=new HashMap<>();
        ArrayList<StudentReportData> studentsReportData=new ArrayList<>();
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
                    "AND sc.completion_date BETWEEN CAST('"+inputData.getStartDate() +"' AS DATE) AND CAST('"+ inputData.getEndDate()+"' AS DATE)";

            List<String> studentsPins = inputData.getStudentsPins();
            if (studentsPins != null && studentsPins.size() > 0) {
                String pins = String.join(",", studentsPins);
                sqlQuery += " AND s.pin IN (" + pins  + ")";
            }


            rs=stmt.executeQuery(sqlQuery);

            while (rs.next()){
                String studentPin=rs.getString("student_pin");

                CourseReportData courseReportData=new CourseReportData();
                courseReportData.setCourseName(rs.getString("course"));
                courseReportData.setCredit(rs.getInt("credit"));
                courseReportData.setInstructorName(rs.getString("instructor_name"));
                courseReportData.setTotalTime(rs.getInt("total_time"));

                StudentReportData studentReportData=null;

                if(reportData.containsKey(studentPin)){
                    studentReportData = reportData.get(studentPin);
                    int allCredits=studentReportData.getOverallCredits();
                    studentReportData.setOverallCredits(allCredits+rs.getInt("credit"));
                }
                else {
                    studentReportData = new StudentReportData();
                    studentReportData.setStudentName(rs.getString("student_name"));
                    studentReportData.setOverallCredits(rs.getInt("credit"));
                }
                studentReportData.addCompletedCourses(courseReportData);

                reportData.put(studentPin,studentReportData);
            }

            reportData.forEach((k, v) -> {

                if(v.getOverallCredits()>=inputData.getMinAmountOfCredits()){
                    studentsReportData.add(v);
                    System.out.println(k +" "+v.getStudentName() + " " + v.getOverallCredits());

                }
            });

            sqlConnection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentsReportData;
    }
}

class CourseReportData {
    private String courseName;
    private int credit;
    private int totalTime;
    private String instructorName;

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public int getCredit() {
        return credit;
    }

    public void setCredit(int credit) {
        this.credit = credit;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(int totalTime) {
        this.totalTime = totalTime;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }
}

class StudentReportData {
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

    public void addCompletedCourses(CourseReportData course){
        this.courses.add(course);
    }
}

class StudentCoursesExporterHTML implements StudentCoursesExporter{
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
                for (int j = 0; j < courses.size(); j++){
                    CourseReportData course = courses.get(j);
                    tableRowsHtml += "<tr><td></td><td>" + course.getCourseName() + "</td><td>" + course.getTotalTime() + "</td><td>" + course.getCredit() + "</td><td>" + course.getInstructorName() + "</td></tr>";
                }
            }

            htmlTemplate = htmlTemplate.replace("{0}", tableRowsHtml);

            writer.write(htmlTemplate);
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}

class StudentCoursesExporterCSV implements StudentCoursesExporter{
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
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}



