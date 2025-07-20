package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.teachers

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.FragmentSearchTeachersBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SharedSearchViewModel
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchTeachersFragment : Fragment() {

    private var _binding: FragmentSearchTeachersBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SharedSearchViewModel by activityViewModels()

    private lateinit var adapter: SearchTeacherAdapter
    private lateinit var schoolId: String
    private lateinit var subject: Subject
    private lateinit var schoolClass: SchoolClass

    private var selectedTeacher: User? = null

    companion object {
        fun newInstance(schoolId: String, subject: Subject, schoolClass: SchoolClass): SearchTeachersFragment {
            return SearchTeachersFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.SCHOOL_ID_KEY, schoolId)
                    putParcelable(Constants.SUBJECT_OBJECT_KEY, subject)
                    putParcelable(Constants.CLASS_OBJECT_KEY, schoolClass)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchTeachersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve arguments
        schoolId = requireArguments().getString(Constants.SCHOOL_ID_KEY).orEmpty()
        subject = requireArguments().getParcelable(Constants.SUBJECT_OBJECT_KEY)!!
        schoolClass = requireArguments().getParcelable(Constants.CLASS_OBJECT_KEY)!!

        // Setup adapter
        adapter = SearchTeacherAdapter { teacher ->
            selectedTeacher = teacher
        }

        searchViewModel.initSearchTeachersForAssign(schoolId, subject)

        binding.rvTeachers.adapter = adapter
        binding.rvTeachers.layoutManager = LinearLayoutManager(requireContext())

        // Observe LiveData from ViewModel
        searchViewModel.searchResults.observe(viewLifecycleOwner) { teachers ->
            adapter.submitList(teachers)
        }

        // Button listeners
        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }

        binding.btnAssignTeachers.setOnClickListener {
            selectedTeacher?.let { teacher: User ->
                val resultIntent = Intent().apply {
                    putExtra("subjectId", subject.id)
                    putExtra("teacherId", teacher.uid)
                }
                requireActivity().setResult(Activity.RESULT_OK, resultIntent)
                requireActivity().finish()
            } ?: Toast.makeText(requireContext(), "Please select a teacher", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

