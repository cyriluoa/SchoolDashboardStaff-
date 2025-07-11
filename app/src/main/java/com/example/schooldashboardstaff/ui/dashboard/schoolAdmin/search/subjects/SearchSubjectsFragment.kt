package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.Subject
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
    private lateinit var classId: String
    private var grade: Int = 0
    private var periodsLeft: Int = 40

    private lateinit var assignedSubjectIdsSet : Set<String>

    private val selectedSubjects = mutableListOf<Subject>()

    companion object {
        fun newInstance(
            schoolId: String,
            classId: String,
            grade: Int = 0,
            periodsLeft: Int = 40,
            assignedSubjectIds: Array<String>
        ): SearchSubjectsFragment {
            return SearchSubjectsFragment().apply {
                arguments = bundleOf(
                    Constants.SCHOOL_ID_KEY to schoolId,
                    Constants.CLASS_ID_KEY to classId,
                    Constants.GRADE_KEY to grade,
                    Constants.MAX_PERIODS_KEY to periodsLeft,
                    Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY to assignedSubjectIds
                )
            }
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
        classId = arguments?.getString(Constants.CLASS_ID_KEY) ?: return
        grade = arguments?.getInt(Constants.GRADE_KEY) ?: 0
        periodsLeft = arguments?.getInt(Constants.MAX_PERIODS_KEY) ?: 40
        val stringArray = arguments?.getStringArray(Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY)
        assignedSubjectIdsSet = stringArray?.toSet() ?: emptySet()


        setupAdapter()
        setupListeners()

        searchViewModel.initSearch(schoolId, grade, assignedSubjectIdsSet)

        searchViewModel.subjects.observe(viewLifecycleOwner) {
            adapter.submitList(it.toList())
            adapter.setPeriodsLeft(periodsLeft)
        }

        assignViewModel.assignmentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Subjects assigned successfully", Toast.LENGTH_SHORT).show()
                requireActivity().finish() // ⬅️ This finishes SearchActivity, going back to previous screen
            }
        }

        assignViewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                assignViewModel.clearMessages()
            }
        }

        updatePeriodCounter()
    }

    private fun setupAdapter() {
        adapter = SearchSubjectAdapter(
            onSubjectSelected = { subject, isChecked ->
                if (isChecked) {
                    selectedSubjects.add(subject)
                    periodsLeft -= subject.periodCount
                } else {
                    selectedSubjects.remove(subject)
                    periodsLeft += subject.periodCount
                }

                updatePeriodCounter()
                adapter.setPeriodsLeft(periodsLeft)
            },
            canSelect = { subject ->
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
        )

        binding.rvSubjects.adapter = adapter
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
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

            assignViewModel.assignSubjectsToClass(
                schoolId = schoolId,
                classId = classId,
                selectedSubjects = selectedSubjects,
                periodsLeft = periodsLeft,
                existingAssignments = assignedSubjectIdsSet
            )
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

