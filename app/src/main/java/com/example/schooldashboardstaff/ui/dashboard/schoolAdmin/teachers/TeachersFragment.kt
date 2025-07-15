package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers


import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.FragmentTeachersBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TeachersFragment : Fragment() {

    private var _binding: FragmentTeachersBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedSchoolViewModel by activityViewModels()
    private val teachersViewModel: TeachersViewModel by viewModels()

    private lateinit var adapter: TeacherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTeachersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupObservers()
        setupFab()
    }

    private fun setupRecyclerView() {
        adapter = TeacherAdapter()
        binding.rvTeachers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTeachers.adapter = adapter
    }

    private fun setupObservers() {
        sharedViewModel.school.observe(viewLifecycleOwner) { school ->
            school?.let {
                teachersViewModel.startListeningToTeachers(it.id)
            }
        }

        teachersViewModel.teachers.observe(viewLifecycleOwner) { teachers ->
            adapter.submitList(teachers)
        }

        teachersViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                teachersViewModel.clearError()
            }
        }
    }


    private fun setupFab() {
        binding.fabAddTeacher.setOnClickListener {
            sharedViewModel.school.value?.let { school ->
                val intent = Intent(requireContext(), AddEditTeacherActivity::class.java)
                intent.putExtra(Constants.SCHOOL_OBJECT_INTENT_KEY, school)
                startActivity(intent)
            } ?: run {
                Toast.makeText(requireContext(), "No school selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

