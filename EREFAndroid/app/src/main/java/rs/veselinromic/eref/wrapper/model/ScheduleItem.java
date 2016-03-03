package rs.veselinromic.eref.wrapper.model;

//import java.time.DayOfWeek;

public class ScheduleItem
{
    String[] dayNames = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public int dayOfWeek;
    public String startTime;
    public String endTime;

    public String title;
    public String lecturer;
    public String roomNumber;

    public ScheduleItem(int dayOfWeek, String startTime, String endTime, String title, String lecturer, String roomNumber)
    {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.title = title;
        this.lecturer = lecturer;
        this.roomNumber = roomNumber;
    }

    public String toString()
    {
        return dayNames[dayOfWeek - 1] + " | " + startTime + " - " + endTime + " | " + title + " | " + lecturer + " | Room: " + roomNumber;
    }
}
