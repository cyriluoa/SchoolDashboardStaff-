package com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.data.model.DisplaySchool
import com.example.schooldashboardstaff.databinding.ItemSchoolBinding

class SchoolAdapter : ListAdapter<DisplaySchool, SchoolAdapter.SchoolViewHolder>(DiffCallback()) {

    var onDeleteClick: ((DisplaySchool) -> Unit)? = null

    var onAssignAdminClick: ((DisplaySchool) -> Unit)? = null

    var onEditClick: ((DisplaySchool) -> Unit)? = null


    inner class SchoolViewHolder(private val binding: ItemSchoolBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(displaySchool: DisplaySchool) {
            val school = displaySchool.school
            binding.tvSchoolName.text = school.name
            binding.tvSchoolLocation.text = school.location
            binding.tvGrades.text = "Grades : ${school.startingGrade} - ${school.finalGrade}"
            binding.tvAdminName.text = "Admin: ${displaySchool.adminUsernames.joinToString(", ")}"
            binding.btnDelete.setOnClickListener{
                onDeleteClick?.invoke(displaySchool)
            }

            binding.btnAddAdmin.setOnClickListener {
                onAssignAdminClick?.invoke(displaySchool)
            }

            binding.btnAddAdmin.setOnClickListener {
                onAssignAdminClick?.invoke(displaySchool)
            }

            binding.btnEdit.setOnClickListener {
                onEditClick?.invoke(displaySchool)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSchoolBinding.inflate(inflater, parent, false)
        return SchoolViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DiffCallback : DiffUtil.ItemCallback<DisplaySchool>() {
        override fun areItemsTheSame(oldItem: DisplaySchool, newItem: DisplaySchool): Boolean {
            return oldItem.school.id == newItem.school.id
        }

        override fun areContentsTheSame(oldItem: DisplaySchool, newItem: DisplaySchool): Boolean {
            return oldItem == newItem
        }
    }
}

