package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.databinding.ItemSchoolClassBinding
import com.example.schooldashboardstaff.data.model.SchoolClass

class SchoolClassAdapter : ListAdapter<SchoolClass, SchoolClassAdapter.ClassViewHolder>(DiffCallback()) {

    inner class ClassViewHolder(private val binding: ItemSchoolClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schoolClass: SchoolClass) {
            binding.tvGradeSection.text = "Grade ${schoolClass.grade} - Section ${schoolClass.section}"

            // Stability status
            if (schoolClass.isStable) {
                binding.tvStability.text = "🟢 Stable"
                binding.classCard.strokeColor = ContextCompat.getColor(binding.root.context, R.color.green)
            } else {
                binding.tvStability.text = "🔴 Unstable"
                binding.classCard.strokeColor = ContextCompat.getColor(binding.root.context, R.color.red)
            }

            // Class Teacher
            binding.tvClassTeacher.text = schoolClass.classTeacherId?.let {
                "Class Teacher: $it" // Replace with name if available
            } ?: "Class Teacher: Not assigned"

            // Subjects assigned
            val subjectsAssigned = schoolClass.subjectAssignments.size
            binding.tvSubjectsAssigned.text = "Subjects Assigned: $subjectsAssigned"

            // Subject progress
            binding.tvSubjectsInfo.text = "Subjects: $subjectsAssigned / ${schoolClass.maxSubjects}"

            // Students
            val studentCount = schoolClass.studentIds.size
            binding.tvStudentsInfo.text = "Students: $studentCount / ${schoolClass.maxStudents}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSchoolClassBinding.inflate(inflater, parent, false)
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<SchoolClass>() {
        override fun areItemsTheSame(oldItem: SchoolClass, newItem: SchoolClass): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SchoolClass, newItem: SchoolClass): Boolean {
            return oldItem == newItem
        }
    }
}
