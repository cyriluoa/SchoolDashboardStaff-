package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.timetable.TimetableGrid
import com.example.schooldashboardstaff.databinding.ItemTimetableCardBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.SharedTimetableViewModel
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.swap.SwapViewModel

class ClassTimetableAdapter(
    private val viewModel: SwapViewModel,
    private val timetableViewModel: SharedTimetableViewModel
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

                cell.setOnClickListener {
                    val selected = selectedPosition
                    if (selected != null && classId == selected.first) {
                        val (selClassId, selDay, selPeriod) = selected

                        // Don't act if clicked on same cell again
                        if (selDay == day && selPeriod == period) return@setOnClickListener

                        val swappable = swappabilityGrid?.getOrNull(day)?.getOrNull(period)

                        if (swappable == true) {
                            // ✅ Swappable — show dialog
                            AlertDialog.Builder(holder.itemView.context)
                                .setTitle("Confirm Swap")
                                .setMessage("Do you want to swap the selected period with this one?")
                                .setPositiveButton("Yes") { _, _ ->
                                    // 🔄 Perform the swap logic
                                    viewModel.performSwap(selClassId, selDay, selPeriod, day, period)?.let {
                                        timetableViewModel.setFinalTimetable(it)
                                    }

                                    // Clear selection after swap
                                    selectedPosition = null
                                    selectedClassId = null
                                    viewModel.clearSelection()
                                    notifyDataSetChanged()
                                }
                                .setNegativeButton("Cancel", null)
                                .show()
                        } else if (swappable == false) {
                            // ❌ Not swappable — show toast
                            Toast.makeText(
                                holder.itemView.context,
                                "Can't swap with this period due to teacher conflict.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

            }
        }

        // Set title once
        if (grid[0][0] != null) {
            holder.binding.tvTimetableTitle.text = "${grid[0][0]?.className}'s Timetable"
        }
    }
}

