package rs.veselinromic.eref.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import rs.veselinromic.eref.android.R;
import rs.veselinromic.eref.android.Util;
import rs.veselinromic.eref.wrapper.model.Subject;

public class SubjectsAdapter extends ArrayAdapter<Subject>
{
    Context context;
    List<Subject> objects;

    public SubjectsAdapter(Context context, List<Subject> objects)
    {
        super(context, -1, objects);

        this.context = context;
        this.objects = objects;

        insertHeaders();
    }

    private void insertHeaders()
    {
        for (int i = 0; i < objects.get(objects.size() - 1).semesterNumber; i++)
        {
            for (int j = 0; j < objects.size(); j++)
            {
                if (j == 0 || (objects.get(j).semesterNumber > objects.get(j - 1).semesterNumber && objects.get(j - 1).semesterNumber != 0))
                {
                    if (objects.get(j).semesterNumber != 0)
                    {
                        int year = ((int) Math.floor(objects.get(j).semesterNumber / 2.1f)) + 1;

                        objects.add(j, new Subject(0, String.format("%d. semestar (%d. godina)", objects.get(j).semesterNumber, year), null, null, null));
                    }
                }
            }
        }
    }

    @Override
    public void addAll(Collection<? extends Subject> collection)
    {
        this.objects.addAll(collection);
    }

    @Override
    public void clear()
    {
        this.objects.clear();
    }

    @Override
    public void notifyDataSetChanged()
    {
        super.notifyDataSetChanged();
        insertHeaders();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        if (objects.get(position).semesterNumber == 0)
        {
            View rowView = layoutInflater.inflate(R.layout.row_list_header, parent, false);



            TextView textView = (TextView) rowView.findViewById(R.id.textView);
            textView.setText(objects.get(position).title);

            return rowView;
        }
        else
        {
            View rowView = layoutInflater.inflate(R.layout.row_subject, parent, false);

            RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.layout);

            Subject currentSubject = this.objects.get(position);

            TextView subjectName = (TextView) rowView.findViewById(R.id.subjectName);
            TextView points = (TextView) rowView.findViewById(R.id.points);
            TextView grade = (TextView) rowView.findViewById(R.id.grade);

            subjectName.setText(Util.toTitleCase(currentSubject.title));
            points.setText(currentSubject.pointCount);

            if (currentSubject.grade.contains("Nije"))
            {
                grade.setText("/");
                grade.setAlpha(0.4f);

                if (!currentSubject.pointCount.equals("-") && Integer.parseInt(currentSubject.pointCount) < 30)
                {
                    relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.subject_failing));
                }
                else if (currentSubject.pointCount.equals("-"))
                {
                    relativeLayout.setAlpha(0.5f);
                }
            }
            else
            {
                grade.setText(currentSubject.grade);
                relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.subject_passed));
            }

            return rowView;
        }
    }



}
