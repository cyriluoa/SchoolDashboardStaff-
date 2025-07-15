package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.FragmentSearchSubjectsBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SharedSearchViewModel
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchSubjectsFragment : Fragment() {

    private var _binding: FragmentSearchSubjectsBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel: SharedSearchViewModel by activityViewModels()
    private val assignViewModel: AssignSubjectViewModel by viewModels()

    private lateinit var adapter: SearchSubjectAdapter

    private lateinit var schoolId: String
    private var classId: String? = null
    private var grade: Int? = null
    private var periodsLeft: Int = 40

    private var user: User? = null
    private var isTeacherMode = false

    private var assignedSubjectIdsSet: Set<String> = emptySet()

    private val selectedSubjects = mutableListOf<Subject>()

    companion object {
        fun newInstanceForClass(
            schoolId: String,
            classId: String,
            grade: Int,
            periodsLeft: Int,
            assignedSubjectIds: Array<String>
        ) = SearchSubjectsFragment().apply {
            arguments = bundleOf(
                Constants.SEARCH_TYPE_KEY to Constants.SEARCH_SUBJECTS,
                Constants.SCHOOL_ID_KEY to schoolId,
                Constants.CLASS_ID_KEY to classId,
                Constants.GRADE_KEY to grade,
                Constants.MAX_PERIODS_KEY to periodsLeft,
                Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY to assignedSubjectIds
            )
        }

        fun newInstanceForTeacher(
            schoolId: String,
            user: User?
        ) = SearchSubjectsFragment().apply {
            arguments = bundleOf(
                Constants.SEARCH_TYPE_KEY to Constants.SEARCH_SUBJECTS_FOR_TEACHER,
                Constants.SCHOOL_ID_KEY to schoolId,
                Constants.USER_OBJECT_INTENT_KEY to user
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        schoolId = arguments?.getString(Constants.SCHOOL_ID_KEY) ?: return
        periodsLeft = arguments?.getInt(Constants.MAX_PERIODS_KEY) ?: 40

        // Check if it's teacher mode
        isTeacherMode = arguments?.getString(Constants.SEARCH_TYPE_KEY) == Constants.SEARCH_SUBJECTS_FOR_TEACHER

        if (isTeacherMode) {
            user = arguments?.getParcelable(Constants.USER_OBJECT_INTENT_KEY)
            assignedSubjectIdsSet = user?.subjectToClassMap?.keys?.toSet() ?: emptySet()
            searchViewModel.initSearchSubjectsForTeacher(schoolId, user)
            binding.tvPeriodsLeft.visibility = View.GONE
        } else {
            classId = arguments?.getString(Constants.CLASS_ID_KEY)
            grade = arguments?.getInt(Constants.GRADE_KEY)
            val stringArray = arguments?.getStringArray(Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY)
            assignedSubjectIdsSet = stringArray?.toSet() ?: emptySet()
            if (grade != null) {
                searchViewModel.initSearchSubjectsForClass(schoolId, grade!!, assignedSubjectIdsSet)
            }
            updatePeriodCounter()
        }

        setupAdapter()
        setupListeners()

        searchViewModel.subjects.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
            adapter.setPeriodsLeft(periodsLeft)
        }

        assignViewModel.assignmentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Subjects assigned successfully", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }

        assignViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                assignViewModel.clearMessages()
            }
        }

    }

    private fun setupAdapter() {
        adapter = SearchSubjectAdapter(
            isTeacherMode = isTeacherMode,
            onSubjectSelected = { subject, isChecked ->
                if (isChecked) {
                    selectedSubjects.add(subject)
                    if (!isTeacherMode) periodsLeft -= subject.periodCount
                } else {
                    selectedSubjects.remove(subject)
                    if (!isTeacherMode) periodsLeft += subject.periodCount
                }

                if (!isTeacherMode) {
                    updatePeriodCounter()
                    adapter.setPeriodsLeft(periodsLeft)
                }
            },
            canSelect = { subject ->
                if (isTeacherMode) {
                    true
                } else {
                    if (periodsLeft >= subject.periodCount) true
                    else {
                        Toast.makeText(
                            requireContext(),
                            "Not enough periods left for ${subject.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        false
                    }
                }
            }
        )


        binding.rvSubjects.adapter = adapter
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())

        // 👇 Observe and update list
        searchViewModel.subjects.observe(viewLifecycleOwner) { subjects ->
            adapter.submitList(subjects.toList()) {
                adapter.setPeriodsLeft(periodsLeft)
                adapter.setSelectedSubjectIds(assignedSubjectIdsSet)
            }
        }
    }


    private fun setupListeners() {
        binding.btnCancel.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnAssignSubjects.setOnClickListener {
            if (selectedSubjects.isEmpty()) {
                Toast.makeText(requireContext(), "No subjects selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isTeacherMode) {
                val subjectToClassMap = selectedSubjects.associate { it.id to listOf("") } // classId will be set later

                val resultIntent = Intent().apply {
                    putExtra(Constants.RESULT_SUBJECT_TO_CLASS_MAP, HashMap(subjectToClassMap))
                }

                requireActivity().setResult(AppCompatActivity.RESULT_OK, resultIntent)
                requireActivity().finish()
            } else {
                assignViewModel.assignSubjectsToClass(
                    schoolId = schoolId,
                    classId = classId!!,
                    selectedSubjects = selectedSubjects,
                    periodsLeft = periodsLeft,
                    existingAssignments = assignedSubjectIdsSet
                )
            }

        }
    }

    private fun updatePeriodCounter() {
        binding.tvPeriodsLeft.text = "Periods Left: $periodsLeft"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


