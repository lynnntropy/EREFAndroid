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
import rs.veselinromic.eref.wrapper.model.EboardNewsItem;
import rs.veselinromic.eref.wrapper.model.NewsItem;

public class EboardNewsAdapter extends ArrayAdapter<EboardNewsItem>
{
    Context context;
    List<EboardNewsItem> objects;

    public EboardNewsAdapter(Context context, List<EboardNewsItem> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = layoutInflater.inflate(R.layout.row_eboard_news, parent, false);

        EboardNewsItem currentNewsItem = this.objects.get(position);

        TextView metadataTextView = (TextView) rowView.findViewById(R.id.metadataTextView);
        TextView titleTextView = (TextView) rowView.findViewById(R.id.titleTextView);
        HtmlTextView contentHtmlView = (HtmlTextView) rowView.findViewById(R.id.contentHtmlTextView);

        if (currentNewsItem.subject == null || currentNewsItem.subject.trim().equals(""))
            currentNewsItem.subject = "Opšta Informacija";

        titleTextView.setText(currentNewsItem.title);
        metadataTextView.setText(String.format("Postavio/la: %s\nPredmet: %s\nDatum i vreme: %s",
                currentNewsItem.submitter, currentNewsItem.subject, currentNewsItem.dateTime));
        contentHtmlView.setHtmlFromString(currentNewsItem.bodyHtml, true);

        return rowView;
    }
}
