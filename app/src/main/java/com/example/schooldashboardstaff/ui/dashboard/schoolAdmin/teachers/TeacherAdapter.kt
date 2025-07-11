package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ItemTeacherBinding



class TeacherAdapter : ListAdapter<User, TeacherAdapter.TeacherViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeacherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTeacherBinding.inflate(inflater, parent, false)
        return TeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TeacherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TeacherViewHolder(
        private val binding: ItemTeacherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teacher: User) {
            binding.teacher = teacher

            val context = binding.root.context
            val assigned = teacher.assignedPeriods ?: 0
            val max = teacher.maxPeriods ?: Int.MAX_VALUE

            val isClassTeacher = teacher.isClassTeacher == true
            val isFull = assigned >= max

            val strokeColor = when {
                isClassTeacher && isFull -> ContextCompat.getColor(context, R.color.darker_blue)
                isClassTeacher && !isFull -> ContextCompat.getColor(context, R.color.blue)
                !isClassTeacher && isFull -> ContextCompat.getColor(context, R.color.primary_red_dark)
                else -> ContextCompat.getColor(context, R.color.primary_red)
            }

            binding.teacherCard.strokeColor = strokeColor
            binding.executePendingBindings()
        }

    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
