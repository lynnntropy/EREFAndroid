package rs.veselinromic.eref.wrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rs.veselinromic.eref.wrapper.model.ScheduleItem;

import java.io.IOException;
//import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;

public class ScheduleManager
{
    static class ClassPeriod
    {
        public String startTime;
        public String endTime;

        public ClassPeriod(String startTime, String endTime)
        {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    static ClassPeriod[] classPeriods = {
            new ClassPeriod("08:00", "08:45"),
            new ClassPeriod("08:50", "09:35"),
            new ClassPeriod("09:45", "10:30"),
            new ClassPeriod("10:35", "11:20"),
            new ClassPeriod("11:30", "12:15"),
            new ClassPeriod("12:20", "13:05"),
            new ClassPeriod("13:10", "13:55"),
            new ClassPeriod("14:00", "14:45"),
            new ClassPeriod("14:50", "15:35"),
            new ClassPeriod("15:45", "16:30"),
            new ClassPeriod("16:35", "17:20"),
            new ClassPeriod("17:25", "18:10"),
            new ClassPeriod("18:15", "19:00"),
            new ClassPeriod("19:10", "19:55"),
            new ClassPeriod("20:00", "20:45")
    };

    private static String USER_SCHEDULE_URL = "https://eref.vts.su.ac.rs/sr/default/schedule";

    public static List<ScheduleItem> getSchedule() throws IOException
    {
        return getScheduleFromUrl(USER_SCHEDULE_URL);
    }

    public static List<ScheduleItem> getScheduleFromUrl(String url) throws IOException
    {
        ArrayList<ScheduleItem> scheduleItems = new ArrayList<ScheduleItem>();

        Document document = Network.getDocument(url);
        Element table = document.select("table.schedule").first();

        int currentDayOfWeek = 1;
        for (Element dayRow: table.select("tr.odd, tr.even"))
        {
            int currentPeriod = 0;
            for(Element cell: dayRow.select("td"))
            {
                if (cell.select(".schedule_days").size() > 0) // Skip the day label cell.
                {
                    // do nothing
                }
                else if (cell.hasClass("no_lecture"))
                {
                    currentPeriod++;
                }
                else if (cell.hasAttr("colspan"))
                {
                    int periodCount = Integer.parseInt(cell.attr("colspan"));

                    String startTime = classPeriods[currentPeriod].startTime;
                    String endTime = classPeriods[currentPeriod + (periodCount - 1)].endTime;

                    String title = cell.select("div").get(0).text().trim();
                    String lecturer = cell.select("div").get(1).text().split(": ")[1].trim();
                    String roomNumber = cell.select("div").get(2).text().split(": ")[1].trim();

                    scheduleItems.add(new ScheduleItem(
                            currentDayOfWeek,
                            startTime,
                            endTime,
                            title,
                            lecturer,
                            roomNumber
                    ));

                    currentPeriod += periodCount;
                }
            }

            currentDayOfWeek++;
        }

        return scheduleItems;
    }
}
