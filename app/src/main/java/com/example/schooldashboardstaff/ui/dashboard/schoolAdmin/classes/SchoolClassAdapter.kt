package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.databinding.ItemSchoolClassBinding
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.SchoolClassState
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign.AssignTeachersDialogFragment
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SearchActivity

class SchoolClassAdapter : ListAdapter<SchoolClass, SchoolClassAdapter.ClassViewHolder>(DiffCallback()) {

    inner class ClassViewHolder(private val binding: ItemSchoolClassBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(schoolClass: SchoolClass) {
            binding.tvGradeSection.text = "Grade ${schoolClass.grade} - Section ${schoolClass.section}"

            // Apply state-based UI
            val state = schoolClass.state
            applyStateUi(state,binding)

            // Class Teacher
            binding.tvClassTeacher.text = schoolClass.classTeacherId?.let {
                "Class Teacher: $it"
            } ?: "Class Teacher: Not assigned"

            // Subjects assigned
            val periodsAssigned = schoolClass.maxPeriods - schoolClass.periodsLeft
            binding.tvSubjectsAssigned.text = "Subjects Assigned: ${schoolClass.subjectAssignments.size}"

            // Subject progress
            binding.tvPeriodsInfo.text = "Periods: $periodsAssigned / ${schoolClass.maxPeriods}"

            // Students
            val studentCount = schoolClass.studentIds.size
            binding.tvStudentsInfo.text = "Students: $studentCount / ${schoolClass.maxStudents}"

            binding.btnAssignSubjects.setOnClickListener {
                val context = binding.root.context
                val intent = SearchActivity.createIntentForClass(
                    context = context,
                    schoolId = schoolClass.schoolId,
                    classId = schoolClass.id,
                    grade = schoolClass.grade,
                    periodsLeft = schoolClass.periodsLeft,
                    subjectIds = schoolClass.subjectAssignments.keys.toTypedArray()
                )
                context.startActivity(intent)
            }

            binding.btnAssignTeachers.setOnClickListener {
                val context = binding.root.context
                val fragmentManager = (context as AppCompatActivity).supportFragmentManager

                val dialog = AssignTeachersDialogFragment.newInstance(schoolClass)
                dialog.show(fragmentManager, "AssignTeachersDialog")
            }

            binding.btnAssignClassTeacher.setOnClickListener {
                val context = binding.root.context
                val intent = SearchActivity.createIntentForAssignClassTeacher(
                    context = context,
                    schoolId = schoolClass.schoolId,
                    schoolClass = schoolClass
                )
                context.startActivity(intent)
            }



        }

        private fun applyStateUi(state: SchoolClassState, binding: ItemSchoolClassBinding) {
            val context = binding.root.context

            // Set color for card stroke and status text
            binding.classCard.strokeColor = ContextCompat.getColor(context, state.colorResId)
            binding.tvStability.text = state.label

            // Set separator line color
            binding.viewSeperator.setBackgroundColor(ContextCompat.getColor(context, state.colorResId))

            // Set button tints
            val buttons = listOf(
                binding.btnAssignStudents,
                binding.btnAssignTeachers,
                binding.btnAssignClassTeacher,
                binding.btnAssignSubjects,
                binding.btnEdit,
                binding.btnDelete
            )

            buttons.forEach { btn ->
                btn.setColorFilter(ContextCompat.getColor(context, state.colorResId), android.graphics.PorterDuff.Mode.SRC_IN)
            }

            // Reset all to GONE
            buttons.forEach { it.visibility = View.GONE }

            // Apply visibility logic based on state
            when (state) {
                SchoolClassState.UNSTABLE_SUBJECTS_NOT_ASSIGNED -> {
                    // Show: Assign Subjects, Edit, Delete
                    listOf(binding.btnAssignSubjects, binding.btnEdit, binding.btnDelete).forEach {
                        it.visibility = View.VISIBLE
                    }
                }
                SchoolClassState.UNSTABLE_SUBJECTS_ASSIGNED -> {
                    // Show: Assign Subjects, Edit, Delete
                    listOf(binding.btnAssignSubjects, binding.btnEdit, binding.btnDelete).forEach {
                        it.visibility = View.VISIBLE
                    }
                }
                SchoolClassState.UNSTABLE_NO_PERIODS_LEFT -> {
                    // Show: Assign Teachers, Edit, Delete
                    listOf(binding.btnAssignTeachers, binding.btnEdit, binding.btnDelete).forEach {
                        it.visibility = View.VISIBLE
                    }
                }

                SchoolClassState.UNSTABLE_TEACHERS_ASSIGNED -> {
                    // Show: Assign Class Teacher, Edit, Delete
                    listOf(binding.btnAssignClassTeacher, binding.btnEdit, binding.btnDelete).forEach {
                        it.visibility = View.VISIBLE
                    }
                }
                SchoolClassState.UNSTABLE_CLASS_TEACHER_ASSIGNED -> {
                    // Show: Assign Teachers, Edit, Delete
                    listOf(binding.btnAssignStudents, binding.btnEdit, binding.btnDelete).forEach {
                        it.visibility = View.VISIBLE
                    }
                }
                SchoolClassState.STABLE_STUDENTS_PRESENT,
                SchoolClassState.STABLE_CLASS_FULL -> {
                    // Show all buttons
                    buttons.forEach { it.visibility = View.VISIBLE }
                }
            }

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
