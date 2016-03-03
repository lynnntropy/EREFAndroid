package rs.veselinromic.eref.wrapper.model;

/**
 * Created by Veselin on 2016-03-02.
 */
public class EboardExampleItem
{
    public String submitter;
    public String subject;
    public String dateTime;

    public String filename;
    public EboardAttachment attachment;

    public EboardExampleItem(String submitter, String subject, String dateTime, String filename, EboardAttachment attachment)
    {
        this.submitter = submitter;
        this.subject = subject;
        this.dateTime = dateTime;
        this.filename = filename;
        this.attachment = attachment;
    }

    public String toString()
    {
        return submitter + "\n"
                + subject + "\n"
                + dateTime + "\n"
                + filename + "\n"
                + attachment.toString();
    }
}
