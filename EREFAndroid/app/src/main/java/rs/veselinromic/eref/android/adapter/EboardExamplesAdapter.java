package rs.veselinromic.eref.android.adapter;

import android.Manifest;
import android.app.Application;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.http.HttpResponseCache;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.wrapper.Network;
import rs.veselinromic.eref.wrapper.model.EboardExampleItem;
import rs.veselinromic.eref.wrapper.model.EboardNewsItem;

public class EboardExamplesAdapter extends ArrayAdapter<EboardExampleItem>
{
    class GetFileTask extends AsyncTask<Void, Void, Void>
    {
        String url;
        String filename;

        File file;

        public GetFileTask(String url, String filename)
        {
            this.url = url;
            this.filename = filename;
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

                String path =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()
                        + "/" + filename;

                this.file = new File(path);
                InputStream is = httpEntity.getContent();
                FileOutputStream fos = new FileOutputStream(this.file);
                int inByte;
                while((inByte = is.read()) != -1)
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
//            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        }
    }

    Context context;
    List<EboardExampleItem> objects;

    public EboardExamplesAdapter(Context context, List<EboardExampleItem> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_klisei, parent, false);

        final EboardExampleItem currentItem = this.objects.get(position);

        TextView submitter = (TextView) rowView.findViewById(R.id.submitter);
        TextView dateTime = (TextView) rowView.findViewById(R.id.dateTime);
        TextView title = (TextView) rowView.findViewById(R.id.title);
        TextView filename  = (TextView) rowView.findViewById(R.id.filename);
        final Button downloadButton = (Button) rowView.findViewById(R.id.download);

        submitter.setText("Postavio: " + currentItem.submitter);
        dateTime.setText(currentItem.dateTime);
        title.setText("Kliše za: " + currentItem.subject);
        filename.setText(currentItem.filename);

        downloadButton.setText(String.format("Preuzmi (%s)", currentItem.attachment.downloadCount));
        downloadButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED)
                {
                    String fullPath = "https://eref.vts.su.ac.rs" + currentItem.attachment.url;
                    new GetFileTask(fullPath, currentItem.filename).execute();

                    downloadButton.setEnabled(false);
                    downloadButton.setText("Preuzimanje u toku...");
                }
            }
        });

        return rowView;
    }

    private String getMimeType(Uri uri) {
        String mimeType = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            ContentResolver cr = context.getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

}
