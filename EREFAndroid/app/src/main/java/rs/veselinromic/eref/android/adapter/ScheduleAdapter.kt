package rs.veselinromic.eref.android.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import org.jetbrains.anko.find

import rs.veselinromic.eref.android.R
import rs.veselinromic.eref.android.Util
import rs.veselinromic.eref.wrapper.model.ScheduleItem
import rs.veselinromic.eref.wrapper.model.Subject


class ScheduleAdapter(context: Context, objects: List<ScheduleItem>) : ArrayAdapter<ScheduleItem>(context, -1, objects)
{
    init
    {
        insertHeaders()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
    {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val currentItem = getItem(position)

        if (currentItem.dayOfWeek == 0)
        {
            val rowView = layoutInflater.inflate(R.layout.row_list_header, parent, false)

            val text = rowView.findViewById(R.id.textView) as TextView
            text.text = currentItem.title

            return rowView
        }
        else
        {
            val rowView = layoutInflater.inflate(R.layout.row_schedule, parent, false)

            val itemName = rowView.findViewById(R.id.itemName) as TextView
            itemName.text = Util.toTitleCase(currentItem.title)

            val time = rowView.findViewById(R.id.time) as TextView
            time.text = currentItem.startTime + " ~ " + currentItem.endTime

            val lecturer = rowView.findViewById(R.id.lecturer) as TextView
            lecturer.text = currentItem.lecturer

            val room = rowView.findViewById(R.id.room) as TextView
            room.text = "Prostorija " + currentItem.roomNumber

            return rowView
        }
    }

    private fun insertHeaders()
    {
        for (i in 1..getItem(count - 1).dayOfWeek - 1)
        {
            for (j in 0..(count - 1))
            {
                if (j == 0 || getItem(j).dayOfWeek > getItem(j - 1).dayOfWeek && getItem(j - 1).dayOfWeek != 0)
                {
                    if (getItem(j).dayOfWeek != 0)
                    {
                        insert(ScheduleItem(0, null, null, ScheduleItem.dayNamesLocal[getItem(j).dayOfWeek - 1], null, null), j)
                    }
                }
            }
        }
    }
}
