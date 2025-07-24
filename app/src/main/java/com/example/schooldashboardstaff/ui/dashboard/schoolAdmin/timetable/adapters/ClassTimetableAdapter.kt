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
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.swap.SwapViewModel

class ClassTimetableAdapter(
    private val viewModel: SwapViewModel
) : ListAdapter<Pair<String, TimetableGrid>, TimetableCardViewHolder>(DiffCallback) {

    private var selectedPosition: Triple<String, Int, Int>? = null
    private var swappabilityGrid: Array<Array<Boolean?>>? = null

    private var selectedClassId: String? = null

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

    fun updateSwappabilityGrid(grid: Array<Array<Boolean?>>?) {
        swappabilityGrid = grid
        notifyDataSetChanged() // Redraw all items to reflect green/red status
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimetableCardViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTimetableCardBinding.inflate(inflater, parent, false)
        return TimetableCardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimetableCardViewHolder, position: Int) {
        val (classId, grid) = getItem(position)

        for (day in 0 until 5) {
            for (period in 0 until 8) {
                val cell = holder.cellViews[day][period]
                val periodData = grid[day][period]

                val bigLabel = cell.findViewById<TextView>(R.id.tv_big_label)
                val smallLabel = cell.findViewById<TextView>(R.id.tv_small_label)
                val colorDot = cell.findViewById<View>(R.id.view_subject_color)

                // Update content
                if (periodData == null) {
                    bigLabel.text = "Free"
                    smallLabel.text = ""
                    colorDot.visibility = View.INVISIBLE
                } else {
                    bigLabel.text = periodData.subjectName
                    smallLabel.text = "${periodData.teacherUsername} (${periodData.teacherFullName})"
                    colorDot.visibility = View.VISIBLE
                    colorDot.background.setTint(periodData.colorInt)
                }

                // Update border for selected cell
                val isSelected = selectedPosition == Triple(classId, day, period)
                if (isSelected) {
                    cell.setBackgroundResource(R.drawable.period_cell_selected)
                } else if (classId == selectedClassId) {
                    when (swappabilityGrid?.getOrNull(day)?.getOrNull(period)) {
                        true -> cell.setBackgroundResource(R.drawable.period_cell_swappable)
                        false -> cell.setBackgroundResource(R.drawable.period_cell_unswappable)
                        null -> cell.setBackgroundResource(R.drawable.period_cell_bg)
                    }
                } else {
                    cell.setBackgroundResource(R.drawable.period_cell_bg)
                }

                // Long click to select
                cell.setOnLongClickListener {
                    val newSelection = Triple(classId, day, period)

                    selectedPosition = if (selectedPosition == newSelection) {
                        selectedClassId = null
                        viewModel.clearSelection()
                        null
                    } else {
                        val periodObj = grid[day][period]
                        if (periodObj != null) {
                            selectedClassId = classId
                            viewModel.updateSelectedCell(classId, day, period)
                        }
                        newSelection
                    }

                    notifyDataSetChanged()
                    true
                }
            }
        }

        // Set title once
        if (grid[0][0] != null) {
            holder.binding.tvTimetableTitle.text = "${grid[0][0]?.className}'s Timetable"
        }
    }
}

