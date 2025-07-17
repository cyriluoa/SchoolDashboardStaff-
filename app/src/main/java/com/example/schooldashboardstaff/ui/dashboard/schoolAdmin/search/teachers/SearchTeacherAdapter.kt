package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.teachers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ItemTeacherSearchBinding

class SearchTeacherAdapter(
    private val onTeacherSelected: (User) -> Unit
) : ListAdapter<User, SearchTeacherAdapter.TeacherViewHolder>(DiffCallback()) {

    private var selectedUserId: String? = null

    inner class TeacherViewHolder(private val binding: ItemTeacherSearchBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(user: User) {
            binding.teacher = user

            val isSelected = user.uid == selectedUserId

            // Set class count from subjectToClassMap
            val classesSize = user.subjectToClassMap?.values
                ?.sumOf { classList ->
                    if (classList.isNotEmpty() && classList[0].isNotEmpty()) classList.size else 0
                } ?: 0

            binding.tvClassCount.text = "Classes: $classesSize"

            // Change stroke color if selected
            val context = binding.root.context
            val strokeColor = if (isSelected) {
                ContextCompat.getColor(context, R.color.green)
            } else {
                ContextCompat.getColor(context, R.color.primary_red)
            }
            binding.teacherCard.strokeColor = strokeColor

            binding.teacherCard.setOnClickListener {
                if (user.uid != selectedUserId) {
                    selectedUserId = user.uid
                    notifyDataSetChanged() // ✅ Triggers rebind for entire list (can be optimized)
                    onTeacherSelected(user)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTeacherSearchBinding.inflate(inflater, parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.uid == newItem.uid

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem == newItem
    }
}

