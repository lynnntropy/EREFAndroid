package rs.veselinromic.eref.wrapper;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import rs.veselinromic.eref.wrapper.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Wrapper
{
    private static String PROFILE_URL = "https://eref.vts.su.ac.rs/sr/default/studentsdata/index";
    private static String NEWS_URL = "https://eref.vts.su.ac.rs/sr";

    private static String EBOARD_NEWS_URL = "https://eref.vts.su.ac.rs/sr/default/eboard/news/noauth/1";
    private static String EBOARD_EXAMPLES_URL = "https://eref.vts.su.ac.rs/sr/default/eboard/examples/noauth/1";
    private static String EBOARD_RESULTS_URL = "https://eref.vts.su.ac.rs/sr/default/eboard/results/noauth/1";
    private static String EBOARD_FINISHINGWORK_URL = "https://eref.vts.su.ac.rs/sr/default/eboard/finishingwork/noauth/1";
    private static String EBOARD_MESSAGES_URL = "https://eref.vts.su.ac.rs/sr/default/eboard/listmessages";

    public static List<NewsItem> getNews() throws IOException
    {
        Document document = Network.getDocument(NEWS_URL);
        ArrayList<NewsItem> newsItems = new ArrayList<NewsItem>();

        for (Element newsPost : document.select(".posts-news > li"))
        {
            String title = newsPost.select("h1").text().trim();
            String contentText = newsPost.select(".posts-summary").text().trim();
            String contentHtml = newsPost.select(".posts-summary").html().trim();
            String date = newsPost.select(".posts-footer").text().trim();

            if (newsPost.select(".posts-content").size() > 0)
            {
                contentText += newsPost.select(".posts-content").text().trim();
                contentHtml += newsPost.select(".posts-content").html().trim();
            }

            // DEBUG
            Log.i("EREF Wrapper", "NEWS ITEM HTML\n" + contentHtml);

            newsItems.add(new NewsItem(date, title, contentText, contentHtml));
        }

        return newsItems;
    }

    public static UserProfile getUserProfile() throws IOException
    {
        Document document = Network.getDocument(PROFILE_URL);

        ArrayList<NameValuePair> data = new ArrayList<NameValuePair>();

        for (Element node : document.select("#student-data > table > tbody > tr"))
        {
            String description = node.select("td").get(0).text().trim();
            String value = node.select("td").get(1).text().trim();

            data.add(new NameValuePair(description, value));
        }

        Element creditTable = document.select("table").first();

        String generalCredit = creditTable.select("tr").get(1).select("td").get(1).text().trim();
        String tuitionCredit = creditTable.select("tr").last().select("td").get(1).text().trim();

        return new UserProfile(data, generalCredit, tuitionCredit);
    }

    public static List<EboardNewsItem> getEboardNews() throws IOException
    {
        Document document = Network.getDocument(EBOARD_NEWS_URL);
        ArrayList<EboardNewsItem> newsItems = new ArrayList<EboardNewsItem>();

        for(Element element : document.select(".eboard-post"))
        {
            String dateTime = "[ DateTime Unknown ]";

            Pattern dateTimePattern = Pattern.compile("Datum i vreme: (.*)");
            Matcher matcher = dateTimePattern.matcher(element.html());
            if (matcher.find())
            {
                dateTime = matcher.group(1).trim();
            }

            newsItems.add(new EboardNewsItem(
                    element.select(".professor-f").text().trim(),
                    element.select(".subjects-f").text().trim(),
                    dateTime,
                    element.select(".eboard-post-title").text().trim(),
                    element.select(".eboard-post-content").text().trim()
            ));
        }

        return newsItems;
    }

    public static List<EboardResultsItem> getEboardResults() throws IOException
    {
        Document document = Network.getDocument(EBOARD_RESULTS_URL);
        ArrayList<EboardResultsItem> resultsItems = new ArrayList<EboardResultsItem>();

        for(Element element : document.select(".eboard-post"))
        {
            String dateTime = "[ DateTime Unknown ]";

            Pattern dateTimePattern = Pattern.compile("Datum i vreme: (.*)");
            Matcher matcher = dateTimePattern.matcher(element.html());
            if (matcher.find())
            {
                dateTime = matcher.group(1).trim();
            }

            EboardAttachment eboardAttachment;

            if (element.select(".eboard-post-toolbar > li > a").size() > 0)
            {
                eboardAttachment =
                        new EboardAttachment(element.select(".eboard-post-toolbar > li > a").attr("href"), 100);
            }
            else
            {
                eboardAttachment = null;
            }

            resultsItems.add(new EboardResultsItem(
                    element.select(".professor-f").text().trim(),
                    element.select(".subjects-f").text().trim(),
                    dateTime,
                    element.select(".eboard-post-top > b").text(),
                    element.select(".eboard-post-title").text().trim(),
                    element.select(".eboard-post-content").text().trim(),
                    eboardAttachment
            ));
        }

        return resultsItems;
    }

    public static List<EboardExampleItem> getEboardExamples() throws IOException
    {
        Document document = Network.getDocument(EBOARD_EXAMPLES_URL);
        ArrayList<EboardExampleItem> exampleItems = new ArrayList<EboardExampleItem>();

        for(Element element : document.select(".eboard-post"))
        {
            String dateTime = "[ DateTime Unknown ]";

            Pattern dateTimePattern = Pattern.compile("Datum i vreme: (.*)");
            Matcher matcher = dateTimePattern.matcher(element.html());
            if (matcher.find())
            {
                dateTime = matcher.group(1).trim();
            }

            exampleItems.add(new EboardExampleItem(
                    element.select(".professor-f").text().trim(),
                    element.select(".subjects-f").text().trim(),
                    dateTime,
                    element.select(".eboard-post-content").text().trim(),
                    new EboardAttachment(element.select(".eboard-post-toolbar > li > a").attr("href"), 100)
            ));
        }

        return exampleItems;
    }

    public static List<EboardFinishingWorkItem> getFinishingWorkItems() throws IOException
    {
        Document document = Network.getDocument(EBOARD_FINISHINGWORK_URL);
        ArrayList<EboardFinishingWorkItem> finishingWorkItems = new ArrayList<EboardFinishingWorkItem>();

        for (Element element: document.select(".finishingwork-wrapper"))
        {
            ArrayList<EboardFinishingWorkItem.CommitteeMember> committeeMembers
                    =new ArrayList<EboardFinishingWorkItem.CommitteeMember>();

            for (Element committeeElement: element.select(".finishingwork-comission-member"))
            {
                committeeMembers.add(new EboardFinishingWorkItem.CommitteeMember(
                        committeeElement.select(".finishingwork-comission-member-name").text().trim(),
                        committeeElement.select("span").first().text().trim()
                ));
            }

            finishingWorkItems.add(
                    new EboardFinishingWorkItem(
                            element.select(".finishingwork-student-data").text().trim(),
                            element.select(".finishingwork-title").text().trim(),
                            committeeMembers
                    )
            );
        }

        return finishingWorkItems;
    }

    public static List<EboardMessageListItem> getEboardMessageList() throws IOException
    {
        Document document = Network.getDocument(EBOARD_MESSAGES_URL);
        ArrayList<EboardMessageListItem> eboardMessageListItems = new ArrayList<EboardMessageListItem>();

        for(Element element: document.select("#eboardContainer > .post"))
        {
            String dateTime = "[ DateTime Unknown ]";

            Pattern dateTimePattern = Pattern.compile("<h1>(.*) -");
            Matcher matcher = dateTimePattern.matcher(element.html());
            if (matcher.find())
            {
                dateTime = matcher.group(1).trim();
            }

            eboardMessageListItems.add(new EboardMessageListItem(
                    dateTime,
                    element.select(".postTitle").text().trim(),
                    element.select(".showMessage").attr("href")
            ));
        }

        return eboardMessageListItems;
    }
}
