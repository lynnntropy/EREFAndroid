package rs.veselinromic.eref.wrapper.model;

public class Subject
{
    public int semesterNumber;
    public String title;
    public String ectsCount;
    public String pointCount;
    public String grade;

    public Subject(int semesterNumber, String title, String ectsCount, String pointCount, String grade)
    {
        this.semesterNumber = semesterNumber;
        this.title = title;
        this.ectsCount = ectsCount;
        this.pointCount = pointCount;
        this.grade = grade;
    }

    public String toString()
    {
        return String.format("Semester %s | %s | ECTS: %s | Current points: %s | Grade: %s",
                semesterNumber, title, ectsCount, pointCount, grade);
    }
}
