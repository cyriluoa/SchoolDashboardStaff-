package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign




import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.databinding.DialogAssignTeachersToSubjectsBinding
import com.example.schooldashboardstaff.data.model.SchoolClass

import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AssignTeachersDialogFragment : DialogFragment() {

    private var _binding: DialogAssignTeachersToSubjectsBinding? = null
    private val binding get() = _binding!!

    private lateinit var schoolClass: SchoolClass
    private val sharedViewModel: SharedSchoolViewModel by activityViewModels()

    private val assignSubjectDialogViewModel: AssignSubjectDialogViewModel by viewModels()
    private lateinit var subjectAdapter: AssignSubjectAdapter



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DialogAssignTeachersToSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<SchoolClass>(KEY_SCHOOL_CLASS)?.let {
            schoolClass = it
        } ?: dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Setup adapter
        subjectAdapter = AssignSubjectAdapter(subjectAssignments = schoolClass.subjectAssignments,
            onAssignClick = { subject ->
            // 🔜 Will launch teacher picker later
            Log.d("AssignClick", "Clicked Assign for: ${subject.displayName}")
        })

        binding.rvSubjects.adapter = subjectAdapter
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())

        // 2. Fetch subjects
        val subjectIds = schoolClass.subjectAssignments.keys.toList()
        assignSubjectDialogViewModel.fetchSubjectsForClass(schoolClass.schoolId, subjectIds)

        // 3. Observe and submit list
        assignSubjectDialogViewModel.subjects.observe(viewLifecycleOwner) { subjectList ->
            Log.d("AssignTeachersDialogFragment", subjectList.toString())
            subjectAdapter.setSubjects(subjectList)
        }

        // 4. Cancel button
        binding.btnCancel.setOnClickListener { dismiss() }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val KEY_SCHOOL_CLASS = "school_class"

        fun newInstance(schoolClass: SchoolClass): AssignTeachersDialogFragment {
            return AssignTeachersDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_SCHOOL_CLASS, schoolClass)
                }
            }
        }
    }
}

