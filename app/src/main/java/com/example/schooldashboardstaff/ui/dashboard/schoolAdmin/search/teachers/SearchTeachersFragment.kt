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
    private var subject: Subject? = null
    private lateinit var schoolClass: SchoolClass

    private var selectedTeacher: User? = null

    companion object {
        fun newInstance(schoolId: String, subject: Subject, schoolClass: SchoolClass): SearchTeachersFragment {
            return SearchTeachersFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.SEARCH_TYPE_KEY, Constants.SEARCH_TEACHERS)
                    putString(Constants.SCHOOL_ID_KEY, schoolId)
                    putParcelable(Constants.SUBJECT_OBJECT_KEY, subject)
                    putParcelable(Constants.CLASS_OBJECT_KEY, schoolClass)
                }
            }
        }

        fun newInstanceForClassTeacher(schoolId: String, schoolClass: SchoolClass): SearchTeachersFragment {
            return SearchTeachersFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.SEARCH_TYPE_KEY, Constants.SEARCH_CLASS_TEACHER)
                    putString(Constants.SCHOOL_ID_KEY, schoolId)
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

        val args = requireArguments()
        val searchType = args.getString(Constants.SEARCH_TYPE_KEY) ?: Constants.SEARCH_TEACHERS
        schoolId = args.getString(Constants.SCHOOL_ID_KEY).orEmpty()
        schoolClass = args.getParcelable(Constants.CLASS_OBJECT_KEY)
            ?: throw IllegalArgumentException("Missing class object for teacher search")
        subject = args.getParcelable(Constants.SUBJECT_OBJECT_KEY) // optional in class teacher flow

        // Setup adapter
        adapter = SearchTeacherAdapter { teacher ->
            selectedTeacher = teacher
        }

        binding.rvTeachers.adapter = adapter
        binding.rvTeachers.layoutManager = LinearLayoutManager(requireContext())

        // 🔁 Trigger correct VM search
        if (searchType == Constants.SEARCH_CLASS_TEACHER) {
            searchViewModel.initSearchClassTeacherCandidates(schoolId, schoolClass)
        } else {
            subject?.let {
                searchViewModel.initSearchTeachersForAssign(schoolId, it)
            } ?: run {
                Toast.makeText(requireContext(), "Missing subject for teacher assignment", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
                return
            }
        }

        // 🔁 Observe teachers list
        searchViewModel.searchResults.observe(viewLifecycleOwner) { teachers ->
            adapter.submitList(teachers)
        }

        // Cancel button
        binding.btnCancel.setOnClickListener {
            requireActivity().finish()
        }

        // Assign button
        binding.btnAssignTeachers.setOnClickListener {
            selectedTeacher?.let { teacher ->
                val resultIntent = Intent().apply {
                    when (requireArguments().getString(Constants.SEARCH_TYPE_KEY)) {
                        Constants.SEARCH_CLASS_TEACHER -> {
                            putExtra(Constants.SCHOOL_ID_KEY, schoolId)
                            putExtra(Constants.CLASS_OBJECT_KEY, schoolClass)
                            putExtra(Constants.USER_OBJECT_INTENT_KEY, teacher)
                        }
                        else -> {
                            subject?.let { putExtra("subjectId", it.id) }
                            putExtra("teacherId", teacher.uid)
                        }
                    }
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


