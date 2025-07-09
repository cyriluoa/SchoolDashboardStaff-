package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.databinding.ItemSubjectBinding

class SubjectAdapter : ListAdapter<Subject, SubjectAdapter.SubjectViewHolder>(DiffCallback()) {

    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            binding.tvSubjectName.text = subject.displayName
            binding.tvGrade.text = "Grade ${subject.grade}"

            // Assigned teachers
            binding.tvTeachersAssigned.text = "Teachers: ${subject.teacherIds.size}"

            // Assigned classes
            binding.tvClassesAssigned.text = "Classes: ${subject.classIds.size}"

            // Styling
            binding.subjectCard.strokeColor = subject.colorInt


            // Optional click listener if needed:
            // binding.root.setOnClickListener { ... }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem == newItem
        }
    }
}
