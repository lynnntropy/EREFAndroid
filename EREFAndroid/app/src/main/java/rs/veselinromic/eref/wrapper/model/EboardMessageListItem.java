package rs.veselinromic.eref.wrapper.model;

public class EboardMessageListItem
{
    public String dateTime;
    public String person;
    public String url;

    public EboardMessageListItem(String dateTime, String person, String url)
    {
        this.dateTime = dateTime;
        this.person = person;
        this.url = url;
    }

    public String toString()
    {
        return dateTime + ": " + person + " | " + url;
    }
}
