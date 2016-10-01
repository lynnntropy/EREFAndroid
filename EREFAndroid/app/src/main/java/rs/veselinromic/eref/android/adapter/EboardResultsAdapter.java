package rs.veselinromic.eref.android.adapter;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.wrapper.Network;
import rs.veselinromic.eref.wrapper.model.EboardExampleItem;
import rs.veselinromic.eref.wrapper.model.EboardResultsItem;

public class EboardResultsAdapter extends ArrayAdapter<EboardResultsItem>
{
    class GetFileTask extends AsyncTask<Void, Void, Void>
    {
        String url;
        File file;

        String filePath;

        public GetFileTask(String url, String filePath)
        {
            this.url = url;
            this.filePath = filePath;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            Log.i("GetFileTask", "Downloading attachment from URL: " + url);

            try
            {
                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = Network.httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();

                this.file = new File(filePath);
                InputStream is = httpEntity.getContent();
                FileOutputStream fos = new FileOutputStream(this.file);
                int inByte;
                while ((inByte = is.read()) != -1)
                    fos.write(inByte);
                is.close();
                fos.close();

                Log.i("GetFileTask", "Attachment downloaded!");
            }
            catch (IOException e)
            {
                Log.e("GetFileTask", "e", e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid)
        {
            Uri uri = Uri.fromFile(file);
            String mime = getMimeType(uri);

            Log.i("GetFileTask", "MIME type of file: " + mime);

            // Open file with user selected app
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

    Context context;
    List<EboardResultsItem> objects;


    public EboardResultsAdapter(Context context, List<EboardResultsItem> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_result, parent, false);

        final EboardResultsItem currentItem = this.objects.get(position);

        File myDirectory = new File(Environment.getExternalStorageDirectory(), "eref");
        myDirectory.mkdirs();

        final String filePath = myDirectory.getAbsolutePath() + "/" + currentItem.dateTime;

        TextView metadataTextView = (TextView) rowView.findViewById(R.id.metadataTextView);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        HtmlTextView body = (HtmlTextView) rowView.findViewById(R.id.body);
        final Button downloadButton = (Button) rowView.findViewById(R.id.download);

        metadataTextView.setText(String.format("Postavio/la: %s\nPredmet: %s\nDatum i vreme: %s",
                currentItem.submitter, currentItem.subject, currentItem.dateTime));

        titleTextView.setText(currentItem.title);
        body.setHtmlFromString(currentItem.body, true);

        File file = new File(filePath);
        if (!file.exists())
        {
            if (currentItem.attachment != null)
            {
                downloadButton.setText("Preuzmi");
                downloadButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                == PackageManager.PERMISSION_GRANTED)
                        {
                            String fullPath = "https://eref.vts.su.ac.rs" + currentItem.attachment.url;
                            new GetFileTask(fullPath, filePath).execute();

                            downloadButton.setEnabled(false);
                            downloadButton.setAlpha(0.5f);
                            downloadButton.setText("Preuzimanje u toku...");
                        }
                    }
                });
            }
            else
            {
                downloadButton.setEnabled(false);
                downloadButton.setAlpha(0.5f);
                downloadButton.setText("Bez Fajla");
            }
        }
        else
        {
            downloadButton.setText("Otvori");
            downloadButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED)
                    {
                        Uri uri = Uri.fromFile(new File((filePath)));
                        String mime = getMimeType(uri);

                        Log.i("GetFileTask", "MIME type of file: " + mime);

                        // Open file with user selected app
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(uri, mime);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(intent);
                    }
                }
            });
        }

        return rowView;
    }

    private String getMimeType(Uri uri)
    {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT))
        {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        }
        else
        {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

}