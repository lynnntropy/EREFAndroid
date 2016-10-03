package rs.veselinromic.eref.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import org.sufficientlysecure.htmltextview.HtmlTextView

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.wrapper.model.EboardNewsItem
import rs.veselinromic.eref.wrapper.model.NewsItem

class EboardNewsAdapter (internal var context: Context, internal var objects: List<EboardNewsItem>):
        ArrayAdapter<EboardNewsItem>(context, -1, objects)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = layoutInflater.inflate(R.layout.row_eboard_news, parent, false)

        val currentNewsItem = this.objects[position]

        val metadataTextView = rowView.findViewById(R.id.metadataTextView) as TextView
        val titleTextView = rowView.findViewById(R.id.titleTextView) as TextView
        val contentHtmlView = rowView.findViewById(R.id.contentHtmlTextView) as HtmlTextView

        if (currentNewsItem.subject == null || currentNewsItem.subject.trim { it <= ' ' } == "")
            currentNewsItem.subject = "Opšta Informacija"

        titleTextView.text = currentNewsItem.title
        metadataTextView.text = String.format("Postavio/la: %s\nPredmet: %s\nDatum i vreme: %s",
                                              currentNewsItem.submitter, currentNewsItem.subject, currentNewsItem.dateTime)
        contentHtmlView.setHtmlFromString(currentNewsItem.bodyHtml, true)

        return rowView
    }
}
