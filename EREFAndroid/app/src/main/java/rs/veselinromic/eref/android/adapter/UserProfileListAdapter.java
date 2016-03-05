package rs.veselinromic.eref.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.wrapper.model.NameValuePair;
import rs.veselinromic.eref.wrapper.model.NewsItem;

public class UserProfileListAdapter extends ArrayAdapter<NameValuePair>
{
    Context context;
    List<NameValuePair> objects;

    public UserProfileListAdapter(Context context, List<NameValuePair> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_profile, parent, false);

        NameValuePair currentItem = this.objects.get(position);

        TextView nameView = (TextView) rowView.findViewById(R.id.keyTextView);
        TextView valueView = (TextView) rowView.findViewById(R.id.valueTextView);

        nameView.setText(currentItem.name);
        valueView.setText(currentItem.value);

        return rowView;
    }
}