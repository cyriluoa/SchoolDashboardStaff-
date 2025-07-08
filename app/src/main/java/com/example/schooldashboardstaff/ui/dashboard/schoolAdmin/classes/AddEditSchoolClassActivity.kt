package com.example.schooldashboardstaff.ui.schoolclass

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.databinding.ActivityAddEditSchoolClassBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.AddEditSchoolClassViewModel
import com.example.schooldashboardstaff.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AddEditSchoolClassActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditSchoolClassBinding
    private lateinit var viewModel: AddEditSchoolClassViewModel
    private lateinit var currentSchool: School

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditSchoolClassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentSchool = intent.getParcelableExtra(Constants.SCHOOL_OBJECT_INTENT_KEY)
            ?: return finishWithError("School data missing")

        viewModel = ViewModelProvider(this)[AddEditSchoolClassViewModel::class.java]

        setupGradeDropdown()
        setupListeners()
        observeViewModel()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
            }
        })
    }

    private fun setupGradeDropdown() {
        val grades = (currentSchool.startingGrade..currentSchool.finalGrade).toList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.spinnerGrade.setAdapter(adapter)
    }

    private fun setupListeners() {
        binding.btnSubmitClass.setOnClickListener {
            val gradeStr = binding.spinnerGrade.text.toString().trim()
            val maxStudentsStr = binding.etMaxStudents.text.toString().trim()
            val maxSubjectsStr = binding.etMaxSubjects.text.toString().trim()

            if (gradeStr.isEmpty() || maxStudentsStr.isEmpty() || maxSubjectsStr.isEmpty()) {
                showToast("Please fill all fields")
                return@setOnClickListener
            }

            val grade = gradeStr.toIntOrNull()
            val maxStudents = maxStudentsStr.toIntOrNull()
            val maxSubjects = maxSubjectsStr.toIntOrNull()

            if (grade == null || maxStudents == null || maxSubjects == null) {
                showToast("Invalid input in number fields")
                return@setOnClickListener
            }

            binding.progressBar.visibility = View.VISIBLE
            binding.tvStatusMessage.visibility = View.GONE

            viewModel.addSchoolClass(
                schoolId = currentSchool.id,
                grade = grade,
                maxStudents = maxStudents,
                maxSubjects = maxSubjects
            )
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun observeViewModel() {
        viewModel.addStatus.observe(this) { result ->
            binding.progressBar.visibility = View.GONE

            if (result.isSuccess) {
                binding.tvStatusMessage.apply {
                    text = "✅ Class created successfully"
                    setTextColor(ContextCompat.getColor(context, R.color.green))
                    visibility = View.VISIBLE
                }
                Toast.makeText(this, "Class created!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                val errorMessage = result.exceptionOrNull()?.message ?: "Unknown error"
                binding.tvStatusMessage.apply {
                    text = "❌ Failed to create class: $errorMessage"
                    setTextColor(ContextCompat.getColor(context, R.color.primary_red))
                    visibility = View.VISIBLE
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun finishWithError(message: String): Nothing {
        showToast(message)
        finish()
        throw IllegalStateException(message)
    }


}
