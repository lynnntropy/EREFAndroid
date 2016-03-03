package rs.veselinromic.eref.wrapper.model;

public class EboardResultsItem
{
    public String submitter;
    public String subject;
    public String dateTime;
    public String type;

    public String title;
    public String body;
    public EboardAttachment attachment;

    public EboardResultsItem(String submitter, String subject, String dateTime, String type, String title, String body, EboardAttachment attachment)
    {
        this.submitter = submitter;
        this.subject = subject;
        this.dateTime = dateTime;
        this.type = type;
        this.title = title;
        this.body = body;
        this.attachment = attachment;
    }

    public String toString()
    {
        String truncatedBody;
        if (body.length() > 15)
            truncatedBody = body.substring(0, 15) + "...";
        else
            truncatedBody = body;

        return submitter + "\n"
                + subject + "\n"
                + dateTime + "\n"
                + type + "\n"
                + title + "\n"
                + truncatedBody + "\n"
                + ((attachment != null) ? attachment.toString() : "");
    }

}
