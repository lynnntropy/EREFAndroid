package rs.veselinromic.eref.wrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rs.veselinromic.eref.wrapper.model.Subject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SubjectsManager
{
    static String SUBJECTS_URL = "https://eref.vts.su.ac.rs/sr/default/subjects/index";

    public static List<Subject> getSubjects() throws IOException
    {
        Document document = Network.getDocument(SUBJECTS_URL);
        ArrayList<Subject> subjects = new ArrayList<Subject>();

        for(Element subjectRow: document.select("table.tableYear > tbody > tr"))
        {
            // Skip empty row used for padding.
            if (subjectRow.select("td").size() <= 1) continue;

            String semester = subjectRow.select("td").get(0).text().trim();
            if (semester.equals("Semestar")) continue; // Skip the header row.

            String title = subjectRow.select("td").get(2).text().trim();
            String ectsCount = subjectRow.select("td").get(3).text().trim();
            String pointCount = subjectRow.select("td").get(8).text().trim();
            String grade = subjectRow.select("td").get(9).text().trim();

            subjects.add(new Subject(semester, title, ectsCount, pointCount, grade));
        }

        return subjects;
    }
}
