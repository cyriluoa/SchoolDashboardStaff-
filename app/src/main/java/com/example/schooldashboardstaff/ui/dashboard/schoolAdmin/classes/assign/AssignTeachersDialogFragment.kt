package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.assign




import android.R
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.databinding.DialogAssignTeachersToSubjectsBinding
import com.example.schooldashboardstaff.data.model.SchoolClass

import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SearchActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AssignTeachersDialogFragment : DialogFragment() {

    private var _binding: DialogAssignTeachersToSubjectsBinding? = null
    private val binding get() = _binding!!

    private lateinit var schoolClass: SchoolClass
    private val sharedViewModel: SharedSchoolViewModel by activityViewModels()
    private val assignSubjectDialogViewModel: AssignSubjectDialogViewModel by viewModels()

    private lateinit var subjectAdapter: AssignSubjectAdapter
    private val updatedAssignments = mutableMapOf<String, String>()

    private val assignTeacherLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val subjectId = data?.getStringExtra("subjectId") ?: return@registerForActivityResult
                val teacherId = data.getStringExtra("teacherId") ?: return@registerForActivityResult

                updatedAssignments[subjectId] = teacherId
                subjectAdapter.updateAssignment(subjectId, teacherId)
            }
        }

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
        dialog?.window?.setBackgroundDrawableResource(R.color.transparent)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        arguments?.getParcelable<SchoolClass>(KEY_SCHOOL_CLASS)?.let {
            schoolClass = it
            updatedAssignments.putAll(it.subjectAssignments)
        } ?: dismiss()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subjectAdapter = AssignSubjectAdapter(
            initialAssignments  = updatedAssignments,
            onAssignClick = { subject ->
                val intent = SearchActivity.createIntentForAssignTeacher(
                    requireContext(),
                    schoolId = schoolClass.schoolId,
                    subject = subject,
                    schoolClass = schoolClass
                )
                assignTeacherLauncher.launch(intent)
            }
        )

        binding.rvSubjects.adapter = subjectAdapter
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())

        val subjectIds = schoolClass.subjectAssignments.keys.toList()
        assignSubjectDialogViewModel.fetchSubjectsForClass(schoolClass.schoolId, subjectIds)

        assignSubjectDialogViewModel.subjects.observe(viewLifecycleOwner) { subjectList ->
            subjectAdapter.setSubjects(subjectList)
        }

        assignSubjectDialogViewModel.assignmentSuccess.observe(viewLifecycleOwner) { success ->
            if (success) {
                Toast.makeText(requireContext(), "Assignment successful", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Assignment failed", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnDone.setOnClickListener {
            val finalMap = subjectAdapter.getUpdatedAssignments()
            finalMap.forEach { (subjectId, teacherId) ->
//                Log.d("FinalAssignments", "Subject ID: $subjectId -> Teacher ID: $teacherId")
            }

            val currentAssignments = schoolClass.subjectAssignments
            if (finalMap != currentAssignments) {
                val subjects = assignSubjectDialogViewModel.subjects.value.orEmpty()
                assignSubjectDialogViewModel.assignTeachersToSubjects(
                    schoolClass = schoolClass,
                    subjects = subjects,
                    updatedAssignments = finalMap
                )
            } else {
                Toast.makeText(requireContext(), "No changes to assign", Toast.LENGTH_SHORT).show()
            }
        }

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


