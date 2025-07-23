package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.schooldashboardstaff.databinding.RecyclerViewTimetableBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.SharedTimetableViewModel

class TeacherTimetableListFragment : Fragment() {

    private lateinit var adapter: TeacherTimetableAdapter
    private val sharedViewModel: SharedTimetableViewModel by activityViewModels()
    private lateinit var binding: RecyclerViewTimetableBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = RecyclerViewTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = TeacherTimetableAdapter()
        binding.recyclerView.adapter = adapter

        sharedViewModel.finalTimetable.observe(viewLifecycleOwner) { timetable ->
            adapter.submitList(timetable?.teacherSchedules?.toList() ?: emptyList())
        }
    }
}
