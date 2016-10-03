package rs.veselinromic.eref.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.RelativeLayout
import android.widget.TextView

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.android.Util
import rs.veselinromic.eref.wrapper.model.Subject

class SubjectsAdapter(internal var context: Context, internal var objects: MutableList<Subject>) :
        ArrayAdapter<Subject>(context, -1, objects)
{
    init
    {
        insertHeaders()
    }

    private fun insertHeaders()
    {
        for (i in 0..objects[objects.size - 1].semesterNumber - 1)
        {
            for (j in objects.indices)
            {
                if (j == 0 || objects[j].semesterNumber > objects[j - 1].semesterNumber && objects[j - 1].semesterNumber != 0)
                {
                    if (objects[j].semesterNumber != 0)
                    {
                        val year = Math.floor((objects[j].semesterNumber / 2.1f).toDouble()).toInt() + 1

                        objects.add(j, Subject(0, String.format("%d. semestar (%d. godina)", objects[j].semesterNumber, year), null, null, null))
                    }
                }
            }
        }
    }

    override fun addAll(collection: Collection<Subject>)
    {
        this.objects.addAll(collection)
    }

    override fun clear()
    {
        this.objects.clear()
    }

    override fun notifyDataSetChanged()
    {
        super.notifyDataSetChanged()
        insertHeaders()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        if (objects[position].semesterNumber == 0)
        {
            val rowView = layoutInflater.inflate(R.layout.row_list_header, parent, false)


            val textView = rowView.findViewById(R.id.textView) as TextView
            textView.text = objects[position].title

            return rowView
        }
        else
        {
            val rowView = layoutInflater.inflate(R.layout.row_subject, parent, false)

            val relativeLayout = rowView.findViewById(R.id.layout) as RelativeLayout

            val currentSubject = this.objects[position]

            val subjectName = rowView.findViewById(R.id.subjectName) as TextView
            val points = rowView.findViewById(R.id.points) as TextView
            val grade = rowView.findViewById(R.id.grade) as TextView

            subjectName.text = Util.toTitleCase(currentSubject.title)
            points.text = currentSubject.pointCount

            if (currentSubject.grade.contains("Nije"))
            {
                grade.text = "/"
                grade.alpha = 0.4f

                if (currentSubject.pointCount != "-" && Integer.parseInt(currentSubject.pointCount) < 30)
                {
                    relativeLayout.setBackgroundColor(context.resources.getColor(R.color.subject_failing))
                }
                else if (currentSubject.pointCount == "-")
                {
                    relativeLayout.alpha = 0.5f
                }
            }
            else
            {
                grade.text = currentSubject.grade
                relativeLayout.setBackgroundColor(context.resources.getColor(R.color.subject_passed))
            }

            return rowView
        }
    }
}
