package rs.veselinromic.eref.wrapper.model;

public class EboardNewsItem
{
    public String submitter;
    public String subject;
    public String dateTime;

    public String title;
    public String body;

    public EboardNewsItem(String submitter, String subject, String dateTime, String title, String body)
    {
        this.submitter = submitter;
        this.subject = (subject == null || subject.trim().length() == 0) ? "Opšta informacija" : subject;
        this.dateTime = dateTime;
        this.title = title;
        this.body = body;
    }

    public String toString()
    {
        String truncatedBody;
        if (body.length() > 15)
            truncatedBody = body.substring(0, 15) + "...";
        else
            truncatedBody = body;

        return submitter + "\n" + subject + "\n" + dateTime + "\n" + title + "\n" + truncatedBody;
    }
}
