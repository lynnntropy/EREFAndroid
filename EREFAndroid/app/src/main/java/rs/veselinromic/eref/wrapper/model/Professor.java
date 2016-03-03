package rs.veselinromic.eref.wrapper.model;

import java.util.List;

public class Professor
{
    public String name;
    public List<String> positions;

    public String detailsUrl;

    public Professor(String name, List<String> positions, String detailsUrl)
    {
        this.name = name;
        this.positions = positions;
        this.detailsUrl = detailsUrl;
    }

    public String toString()
    {
        return String.format("Name: %s\nPositions: %s\nDetails page URL: %s", name, positions.toString(), detailsUrl);
    }
}
