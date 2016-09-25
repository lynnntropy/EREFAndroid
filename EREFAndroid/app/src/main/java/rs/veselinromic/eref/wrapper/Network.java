package rs.veselinromic.eref.wrapper;

import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.CookieStore;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.conn.ssl.SSLConnectionSocketFactory;
import ch.boye.httpclientandroidlib.conn.ssl.SSLContextBuilder;
import ch.boye.httpclientandroidlib.conn.ssl.TrustStrategy;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.HttpClients;
import ch.boye.httpclientandroidlib.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class Network
{
    public static HttpClient httpClient = createHttpClient();

    public static HttpClient createHttpClient()
    {
        SSLContextBuilder builder = new SSLContextBuilder();

        try
        {
            builder.loadTrustMaterial(null, new TrustStrategy()
            {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException
                {
                    return true;
                }
            });

            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(builder.build(),
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            return HttpClients.custom()
                    .setSSLSocketFactory(sslSF)
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0")
                    .build();
        }
        catch(NoSuchAlgorithmException e){}
        catch(KeyStoreException e){}
        catch(KeyManagementException e){}

        return null;
    }

    public static Document getDocument(String url) throws IOException
    {
        System.out.println("Downloading document: " + url);

        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = Network.httpClient.execute(httpGet);

        System.out.println(httpResponse.getStatusLine().getStatusCode() + " " + httpResponse.getStatusLine().getReasonPhrase());

        BufferedReader bufferedReader = new BufferedReader
                (new InputStreamReader(httpResponse.getEntity().getContent()));

        String pageHtml = "";

        String line = "";
        while ((line = bufferedReader.readLine()) != null)
        {
            pageHtml += line;
        }

        EntityUtils.consume(httpResponse.getEntity());
        return Jsoup.parse(pageHtml);
    }
}
