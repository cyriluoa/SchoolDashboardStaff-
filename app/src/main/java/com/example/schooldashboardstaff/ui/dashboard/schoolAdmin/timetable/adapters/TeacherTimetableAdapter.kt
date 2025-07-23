package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.timetable.TimetableGrid
import com.example.schooldashboardstaff.databinding.ItemTimetableCardBinding

class TeacherTimetableAdapter :
    ListAdapter<Pair<String, TimetableGrid>, TimetableCardViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<Pair<String, TimetableGrid>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, TimetableGrid>,
            newItem: Pair<String, TimetableGrid>
        ) = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String, TimetableGrid>,
            newItem: Pair<String, TimetableGrid>
        ) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTimetableCardBinding.inflate(inflater, parent, false)
        return TimetableCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableCardViewHolder, position: Int) {
        val (teacherId, grid) = getItem(position)
        val context = holder.binding.root.context

        holder.binding.tvTimetableTitle.text = "Teacher: $teacherId"
        holder.binding.tvSemester.text = "SEM 1"
        holder.binding.tvClassTeacher.text = ""
        holder.binding.tvTotalPeriods.text = "40 periods/week"

        for (period in 0 until 8) {
            for (day in 0 until 5) {
                val cell = holder.cellViews[day][period]
                val periodData = grid[day][period]

                val bigLabel = cell.findViewById<TextView>(R.id.tv_big_label)
                val smallLabel = cell.findViewById<TextView>(R.id.tv_small_label)
                val colorDot = cell.findViewById<View>(R.id.view_subject_color)

                if (periodData == null) {
                    bigLabel.text = "Free"
                    smallLabel.text = ""
                    colorDot.visibility = View.INVISIBLE
                } else {
                    bigLabel.text = periodData.className
                    smallLabel.text = periodData.subjectName
                    colorDot.visibility = View.VISIBLE
                    colorDot.background.setTint(periodData.colorInt)
                }
            }
        }
    }



}

