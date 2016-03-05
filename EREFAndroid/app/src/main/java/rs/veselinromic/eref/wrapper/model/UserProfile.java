package rs.veselinromic.eref.wrapper.model;

import java.util.List;

public class UserProfile
{
    public List<NameValuePair> userData;
    public String generalCredit;
    public String tuitionCredit;

    public UserProfile(List<NameValuePair> userData, String generalCredit, String tuitionCredit)
    {
        this.userData = userData;
        this.generalCredit = generalCredit;
        this.tuitionCredit = tuitionCredit;
    }

    public String toString()
    {
        String out = "";

        for(NameValuePair nameValuePair : userData)
        {
            out += nameValuePair.name + ": " + nameValuePair.value + "\n";
        }

        out += String.format("CREDIT INFORMATION\nGeneral credit: %s\nTuition credit: %s",
                generalCredit, tuitionCredit);

        return out;
    }
}
