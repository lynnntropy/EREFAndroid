package rs.veselinromic.eref.wrapper.model;

public class EboardAttachment
{
    String url;
    int downloadCount;

    public EboardAttachment(String url, int downloadCount)
    {
        this.url = url;
        this.downloadCount = downloadCount;
    }

    public String toString()
    {
        return url + " (" + downloadCount + " downloads)";
    }
}
