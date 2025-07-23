package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters

import android.view.View

import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.databinding.ItemTimetableCardBinding

class TimetableCardViewHolder(
    val binding: ItemTimetableCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    val cellViews: Array<Array<View>> = Array(5) { row ->
        Array(8) { col ->
            val resId = binding.root.resources.getIdentifier(
                "cell_${col}_${row}", "id", binding.root.context.packageName
            )
            binding.root.findViewById(resId)
        }
    }
}
