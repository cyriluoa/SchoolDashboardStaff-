package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.databinding.ItemSubjectSearchBinding

class SearchSubjectAdapter(
    private val onSubjectSelected: (Subject, Boolean) -> Unit,
    private val canSelect : (Subject) -> Boolean
) : ListAdapter<Subject, SearchSubjectAdapter.SubjectViewHolder>(DiffCallback()) {

    private var selectedSubjects = mutableSetOf<String>()
    private var periodsLeft = 40

    fun setPeriodsLeft(newLeft: Int) {
        periodsLeft = newLeft
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSubjectSearchBinding.inflate(inflater, parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class SubjectViewHolder(
        private val binding: ItemSubjectSearchBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subject: Subject) {
            val isSelected = selectedSubjects.contains(subject.id)

            binding.subject = subject
            binding.selected = isSelected
            binding.executePendingBindings() // 💡 Make sure to trigger data binding

            // Styling
            binding.subjectCard.strokeColor = subject.colorInt

            // Disable if not enough periods
            binding.cbSelectSubject.isEnabled = isSelected || subject.periodCount <= periodsLeft

            binding.cbSelectSubject.setOnCheckedChangeListener(null) // Avoid infinite loops

            binding.cbSelectSubject.setOnCheckedChangeListener(null)

            binding.cbSelectSubject.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    if (!canSelect(subject)) {
                        // ❌ Not enough periods left
                        binding.cbSelectSubject.isChecked = false // Reset manually
                        return@setOnCheckedChangeListener
                    }
                    selectedSubjects.add(subject.id)
                } else {
                    selectedSubjects.remove(subject.id)
                }

                onSubjectSelected(subject, isChecked)
            }


            binding.subjectCard.setOnClickListener {
                val newChecked = !binding.cbSelectSubject.isChecked
                binding.cbSelectSubject.isChecked = newChecked
            }
        }

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
