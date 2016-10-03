package rs.veselinromic.eref.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import org.sufficientlysecure.htmltextview.HtmlTextView

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.wrapper.model.NameValuePair
import rs.veselinromic.eref.wrapper.model.NewsItem

class UserProfileListAdapter(internal var context: Context, internal var objects: List<NameValuePair>) :
        ArrayAdapter<NameValuePair>(context, -1, objects)
{
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = layoutInflater.inflate(R.layout.row_profile, parent, false)

        val currentItem = this.objects[position]

        val nameView = rowView.findViewById(R.id.keyTextView) as TextView
        val valueView = rowView.findViewById(R.id.valueTextView) as TextView

        nameView.text = currentItem.name
        valueView.text = currentItem.value

        return rowView
    }
}