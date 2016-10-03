package rs.veselinromic.eref.android.adapter

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.TextView

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

import ch.boye.httpclientandroidlib.HttpEntity
import ch.boye.httpclientandroidlib.HttpResponse
import ch.boye.httpclientandroidlib.client.methods.HttpGet
import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.wrapper.Network
import rs.veselinromic.eref.wrapper.model.EboardExampleItem

class EboardExamplesAdapter(internal var context: Context, internal var objects: List<EboardExampleItem>) :
        ArrayAdapter<EboardExampleItem>(context, -1, objects)
{
    internal inner class GetFileTask(var url: String, var filePath:

    String) : AsyncTask<Void, Void, Void>()
    {
        var file: File? = null

        override fun doInBackground(vararg params: Void): Void?
        {
            Log.i("GetFileTask", "Downloading attachment from URL: " + url)

            try
            {
                val httpGet = HttpGet(url)
                val httpResponse = Network.httpClient.execute(httpGet)
                val httpEntity = httpResponse.entity

                this.file = File(filePath)
                val inputStream = httpEntity.content
                val outputStream = FileOutputStream(this.file)
                var inByte: Int

                while (true)
                {
                    inByte = inputStream.read()

                    if (inByte != -1)
                    {
                        outputStream.write(inByte)
                    }
                    else
                    {
                        break
                    }
                }

                inputStream.close()
                outputStream.close()

                Log.i("GetFileTask", "Attachment downloaded!")
            }
            catch (e: IOException)
            {
                Log.e("GetFileTask", "e", e)
            }

            return null
        }

        override fun onPostExecute(aVoid: Void?)
        {
            val uri = Uri.fromFile(file)
            val mime = getMimeType(uri)

            Log.i("GetFileTask", "MIME type of file: " + mime)

            // Open file with user selected app
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(uri, mime)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            context.startActivity(intent)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = layoutInflater.inflate(R.layout.row_klisei, parent, false)

        val currentItem = this.objects[position]

        val myDirectory = File(Environment.getExternalStorageDirectory(), "eref")
        myDirectory.mkdirs()

        val filePath = myDirectory.absolutePath + "/" + currentItem.filename

        val title = rowView.findViewById(R.id.titleTextView) as TextView
        val metadataTextView = rowView.findViewById(R.id.metadataTextView) as TextView
        val body = rowView.findViewById(R.id.body) as TextView
        val downloadButton = rowView.findViewById(R.id.download) as Button
        title.text = "Kliše za: " + currentItem.subject
        metadataTextView.text = String.format("Postavio/la: %s\nDatum i vreme: %s",
                                              currentItem.submitter, currentItem.dateTime)
        body.text = currentItem.filename

        val file = File(filePath)
        if (!file.exists())
        {
            downloadButton.text = String.format("Preuzmi (%s)", currentItem.attachment.downloadCount)
            downloadButton.setOnClickListener {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    val fullPath = "https://eref.vts.su.ac.rs" + currentItem.attachment.url
                    GetFileTask(fullPath, filePath).execute()

                    downloadButton.isEnabled = false
                    downloadButton.text = "Preuzimanje u toku..."
                }
            }
        }
        else
        {
            downloadButton.text = "Otvori"
            downloadButton.setOnClickListener {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {

                    val uri = Uri.fromFile(File(filePath))
                    val mime = getMimeType(uri)

                    Log.i("GetFileTask", "MIME type of file: " + mime)

                    // Open file with user selected app
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(uri, mime)
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    context.startActivity(intent)
                }
            }
        }

        return rowView
    }

    private fun getMimeType(uri: Uri): String
    {
        var mimeType: String? = null
        if (uri.scheme == ContentResolver.SCHEME_CONTENT)
        {
            val cr = context.applicationContext.contentResolver
            mimeType = cr.getType(uri)
        }
        else
        {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase())
        }
        return mimeType
    }
}