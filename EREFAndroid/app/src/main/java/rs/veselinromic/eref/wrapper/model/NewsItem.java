package rs.veselinromic.eref.wrapper.model;

public class NewsItem
{
    public String date;
    public String title;
    public String contentText;
    public String contentHtml;

    public NewsItem(String date, String title, String contentText, String contentHtml)
    {
        this.date = date;
        this.title = title;
        this.contentText = contentText;
        this.contentHtml = contentHtml;
    }

    public String toString()
    {
        return date + " | " + title + ": " + contentText.substring(0, 10) + "...";
    }
}
