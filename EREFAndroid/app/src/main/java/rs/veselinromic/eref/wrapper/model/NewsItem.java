package rs.veselinromic.eref.wrapper.model;

public class NewsItem
{
    public String date;
    public String title;
    public String content;

    public NewsItem(String date, String title, String content)
    {
        this.date = date;
        this.title = title;
        this.content = content;
    }

    public String toString()
    {
        return date + " | " + title + ": " + content.substring(0, 10) + "...";
    }
}
