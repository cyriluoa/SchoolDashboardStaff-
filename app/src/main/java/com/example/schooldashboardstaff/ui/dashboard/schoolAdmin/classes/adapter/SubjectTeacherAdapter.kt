package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.data.model.DisplaySubjectTeacher
import com.example.schooldashboardstaff.databinding.ItemSubjectTeacherAssignmentBinding

class SubjectTeacherAdapter : ListAdapter<DisplaySubjectTeacher, SubjectTeacherAdapter.SubjectTeacherViewHolder>(
    object : DiffUtil.ItemCallback<DisplaySubjectTeacher>() {
        override fun areItemsTheSame(oldItem: DisplaySubjectTeacher, newItem: DisplaySubjectTeacher): Boolean {
            return oldItem.subject.id == newItem.subject.id
        }

        override fun areContentsTheSame(oldItem: DisplaySubjectTeacher, newItem: DisplaySubjectTeacher): Boolean {
            return oldItem == newItem
        }
    }
) {
    inner class SubjectTeacherViewHolder(
        private val binding: ItemSubjectTeacherAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DisplaySubjectTeacher) {
            binding.tvSubject.text = item.subject.name
            binding.tvTeacherFullName.text = item.teacher.fullName
            binding.tvTeacherUsername.text = item.teacher.username
            binding.tvPeriodCount.text = "Periods: ${item.subject.periodCount}"
            binding.card.strokeColor = item.subject.colorInt
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectTeacherViewHolder {
        val binding = ItemSubjectTeacherAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SubjectTeacherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectTeacherViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
