package rs.veselinromic.eref.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import org.sufficientlysecure.htmltextview.HtmlTextView

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.wrapper.model.NewsItem

class NewsAdapter(internal var context: Context, internal var objects: List<NewsItem>) :
        ArrayAdapter<NewsItem>(context, -1, objects)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = layoutInflater.inflate(R.layout.row_news, parent, false)

        val currentNewsItem = this.objects[position]

        val titleTextView = rowView.findViewById(R.id.titleTextView) as TextView
        val dateTextView = rowView.findViewById(R.id.metadataTextView) as TextView
        val bodyHtmlTextView = rowView.findViewById(R.id.bodyHtmlTextView) as HtmlTextView

        titleTextView.text = currentNewsItem.title
        dateTextView.text = currentNewsItem.date
        bodyHtmlTextView.setHtmlFromString(currentNewsItem.contentHtml, true)

        return rowView
    }
}
