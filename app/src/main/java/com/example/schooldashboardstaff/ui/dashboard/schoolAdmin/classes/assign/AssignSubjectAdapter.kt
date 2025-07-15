package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign

import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.databinding.ItemAssignSubjectBinding

class AssignSubjectAdapter(
    private val subjectAssignments: Map<String, String>,
    private val onAssignClick: (Subject) -> Unit
) : RecyclerView.Adapter<AssignSubjectAdapter.SubjectViewHolder>() {

    private val subjects = mutableListOf<Subject>()

    inner class SubjectViewHolder(private val binding: ItemAssignSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            val isAssigned = subjectAssignments[subject.id] != ""

            // Update icon
            val iconRes = if (isAssigned) R.drawable.ic_green_circle else R.drawable.ic_red_circle
            binding.ivCompleteCheck.setImageResource(iconRes)

            // Update border color
            val strokeColorRes = if (isAssigned) R.color.green else R.color.red
            binding.subjectCard.setStrokeColor(ContextCompat.getColor(binding.root.context, strokeColorRes))

            // Update button style
            if (isAssigned) {
                binding.btnAssignTeacher.text = "Unassign"
                binding.btnAssignTeacher.setBackgroundResource(R.drawable.green_button_background)
                binding.ivClassIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )

                binding.ivTeacherIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.green),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvSubjectName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.green)
                )

            } else {
                binding.btnAssignTeacher.text = "Assign"
                binding.btnAssignTeacher.setBackgroundResource(R.drawable.red_button_background)
                binding.ivClassIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.primary_red),
                    PorterDuff.Mode.SRC_IN
                )

                binding.ivTeacherIcon.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.primary_red),
                    PorterDuff.Mode.SRC_IN
                )
                binding.tvSubjectName.setTextColor(
                    ContextCompat.getColor(binding.root.context, R.color.primary_red)
                )
            }

            // Labels
            binding.tvSubjectName.text = subject.displayName
            binding.tvTeacherCount.text = "${subject.teacherIds.size} teachers"
            binding.tvClassCount.text = "${subject.classIds.size} classes"

            // Click handling
            binding.btnAssignTeacher.setOnClickListener {
                onAssignClick(subject)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAssignSubjectBinding.inflate(inflater, parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    override fun getItemCount(): Int = subjects.size

    fun setSubjects(newSubjects: List<Subject>) {
        subjects.clear()
        subjects.addAll(newSubjects)
        notifyDataSetChanged()
    }
}

