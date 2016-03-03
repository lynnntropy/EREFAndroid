package rs.veselinromic.eref.wrapper.model;

import java.util.List;

public class EboardFinishingWorkItem
{
    public static class CommitteeMember
    {
        public String name;
        public String position;

        public CommitteeMember(String name, String position)
        {
            this.name = name;
            this.position = position;
        }

        public String toString()
        {
            return name + " (" + position + ")";
        }
    }

    public String summary;
    public String workTitle;

    public List<CommitteeMember> committeeMembers;

    public EboardFinishingWorkItem(String summary, String workTitle, List<CommitteeMember> committeeMembers)
    {
        this.summary = summary;
        this.workTitle = workTitle;
        this.committeeMembers = committeeMembers;
    }

    public String toString()
    {
        String out = "";
        out += "Work Title: " + workTitle + '\n';
        out += "Summary: " + summary + '\n';

        for (CommitteeMember committeeMember: committeeMembers)
        {
            out += "Committee member: " + committeeMember.toString() + '\n';
        }

        return out;
    }
}
