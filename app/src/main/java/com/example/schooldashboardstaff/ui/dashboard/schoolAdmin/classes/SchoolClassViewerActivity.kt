package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.DisplaySubjectTeacher
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ActivitySchoolClassViewerBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.adapter.StudentAdapter
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.adapter.SubjectTeacherAdapter
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.populate.SchoolClassViewerViewModel
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolClassViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySchoolClassViewerBinding
    private lateinit var schoolClass: SchoolClass

    private val viewModel: SchoolClassViewerViewModel by viewModels()

    private lateinit var subjectAdapter: SubjectTeacherAdapter
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolClassViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get passed SchoolClass object
        schoolClass = intent.getParcelableExtra(Constants.CLASS_OBJECT_KEY)
            ?: return finish()

        // Init adapters
        subjectAdapter = SubjectTeacherAdapter()
        studentAdapter = StudentAdapter()

        // Set adapters to RecyclerViews
        binding.rvSubjectAssignments.apply {
            adapter = subjectAdapter
            layoutManager = LinearLayoutManager(this@SchoolClassViewerActivity)
        }

        binding.rvStudents.apply {
            adapter = studentAdapter
            layoutManager = LinearLayoutManager(this@SchoolClassViewerActivity)
        }

        // Load class data
        viewModel.loadClassDetails(schoolClass)

        observeData()
    }

    private fun observeData() {
        viewModel.classWithDetails.observe(this) { (classInfo, subjectTeacherList, studentList) ->
            binding.tvClassTitle.text = "Grade ${classInfo.grade} - Section ${classInfo.section}"
            binding.tvClassTeacherName.text = "Class Teacher: ${viewModel.classTeacherName ?: "Not assigned"}"
            val periodsAssigned = classInfo.maxPeriods - classInfo.periodsLeft
            binding.tvPeriodsInfo.text = "Periods Left: ${periodsAssigned}/${classInfo.maxPeriods}"
            binding.tvStudentInfo.text = "Students: ${classInfo.studentIds.size}/${classInfo.maxStudents}"

            // Submit lists
            subjectAdapter.submitList(subjectTeacherList)
            studentAdapter.submitList(studentList)

            binding.tvNoStudents.visibility = if (studentList.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}

