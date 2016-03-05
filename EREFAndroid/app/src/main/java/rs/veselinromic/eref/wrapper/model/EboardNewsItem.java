package rs.veselinromic.eref.wrapper.model;

public class EboardNewsItem
{
    public String submitter;
    public String subject;
    public String dateTime;

    public String title;
    public String bodyText;
    public String bodyHtml;

    public EboardNewsItem(String submitter, String subject, String dateTime, String title, String bodyText, String bodyHtml)
    {
        this.submitter = submitter;
        this.subject = subject;
        this.dateTime = dateTime;
        this.title = title;
        this.bodyText = bodyText;
        this.bodyHtml = bodyHtml;
    }

    public String toString()
    {
        String truncatedBody;
        if (bodyText.length() > 15)
            truncatedBody = bodyText.substring(0, 15) + "...";
        else
            truncatedBody = bodyText;

        return submitter + "\n" + subject + "\n" + dateTime + "\n" + title + "\n" + truncatedBody;
    }
}
