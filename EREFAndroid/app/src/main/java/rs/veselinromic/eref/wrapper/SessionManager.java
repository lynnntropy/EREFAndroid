package rs.veselinromic.eref.wrapper;


import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SessionManager
{
    class Session
    {
        String sessionId;
    }

    static String LOGIN_ENDPOINT = "https://eref.vts.su.ac.rs/sr/default/users/login";
    static String HOMEPAGE = "https://eref.vts.su.ac.rs/sr";

    public static void authenticate(String username, String password) throws IOException
    {
        HttpPost httpPost = new HttpPost(LOGIN_ENDPOINT);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("username", username));
        nvps.add(new BasicNameValuePair("password", password));
        httpPost.setEntity(new UrlEncodedFormEntity(nvps));
        HttpResponse httpResponse = Network.httpClient.execute(httpPost);

        System.out.println(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());

        EntityUtils.consume(httpResponse.getEntity());
    }

    public static boolean isAuthenticated() throws IOException
    {
        Document homepageDocument = Network.getDocument(HOMEPAGE);
        return homepageDocument.select("a[href=/sr/default/users/logout]").size() > 0;
    }
}
