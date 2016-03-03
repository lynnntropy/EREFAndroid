package rs.veselinromic.eref.wrapper;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rs.veselinromic.eref.wrapper.model.Professor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProfessorFetcher
{
    private static String PROFESSOR_LIST_URL = "https://eref.vts.su.ac.rs/sr/default/professors/index";

    public static List<Professor> getProfessorList() throws IOException
    {
        Document document = Network.getDocument(PROFESSOR_LIST_URL);

        ArrayList<Professor> professors = new ArrayList<Professor>();

        for(Element professorElement: document.select("ul.professorsUL > li"))
        {
            String name = professorElement.select("span > a.professors").text().trim();
            String detailsUrl = professorElement.select("a.professors").first().attr("href");

            ArrayList<String> positions = new ArrayList<String>();

            for(Element position: professorElement.select("ul.professorCat > li"))
            {
                positions.add(position.text().trim());
            }

            professors.add(new Professor(name, positions, detailsUrl));
        }

        return professors;
    }

    public static Document getProfessorDetails(String detailsUrl) throws IOException
    {
        return Network.getDocument(detailsUrl);
    }
}
