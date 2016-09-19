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
import rs.veselinromic.eref.wrapper.model.NewsItem;

public class NewsAdapter extends ArrayAdapter<NewsItem>
{
    Context context;
    List<NewsItem> objects;

    public NewsAdapter(Context context, List<NewsItem> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_news, parent, false);

        NewsItem currentNewsItem = this.objects.get(position);

        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        TextView dateTextView = (TextView) rowView.findViewById(R.id.metadataTextView);
        HtmlTextView bodyHtmlTextView = (HtmlTextView) rowView.findViewById(R.id.bodyHtmlTextView);

        titleTextView.setText(currentNewsItem.title);
        dateTextView.setText(currentNewsItem.date);
//        bodyHtmlTextView.setHtmlFromString(currentNewsItem.contentHtml, new HtmlTextView.RemoteImageGetter());
        bodyHtmlTextView.setHtmlFromString(currentNewsItem.contentHtml, true);

        return rowView;
    }
}
