package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.databinding.ActivityAddEditSubjectBinding

class AddEditSubjectActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditSubjectBinding
    private lateinit var viewModel: AddEditSubjectViewModel
    private var selectedColor: Int = R.color.primary_red

    private lateinit var school: School

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditSubjectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve school
        school = intent.getParcelableExtra("school") ?: run {
            Toast.makeText(this, "No school data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        viewModel = ViewModelProvider(this)[AddEditSubjectViewModel::class.java]

        setUpGradeSpinner()
        setUpObservers()
        setUpColorPicker()
        setUpSubmitButton()
        setUpCancelButton()
    }

    private fun setUpGradeSpinner() {
        val grades = (school.startingGrade..school.finalGrade).map { "Grade $it" }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, grades)
        binding.spinnerGrade.setAdapter(adapter)
    }

    private fun setUpObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.statusMessage.observe(this) { message ->
            message?.let {
                binding.tvStatusMessage.text = it
                binding.tvStatusMessage.visibility = View.VISIBLE
                viewModel.clearStatusMessage()
            }
        }
    }

    private fun setUpColorPicker() {
        binding.btnOpenColorPicker.setOnClickListener {
            val dialog = ColorPickerDialogFragment(initialColor = selectedColor) { color ->
                selectedColor = color
                val hex = String.format("#%06X", 0xFFFFFF and color)
                binding.colorPreview.setBackgroundColor(color)
                binding.tvHelperText.text = "Selected color: $hex"
            }
            dialog.show(supportFragmentManager, "ColorPickerDialog")
        }
    }

    private fun setUpSubmitButton() {
        binding.btnSubmitSubject.setOnClickListener {
            val name = binding.etSubjectName.text.toString().trim()
            val gradeText = binding.spinnerGrade.text.toString().removePrefix("Grade ").trim()
            val grade = gradeText.toIntOrNull()

            if (name.isEmpty() || grade == null) {
                Toast.makeText(this, "Please enter valid subject name and grade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val displayName = "$name - Grade $grade"
            val subject = Subject(
                name = name.uppercase(),
                displayName = displayName,
                grade = grade,
                colorResId = selectedColor
            )

            viewModel.addSubject(subject, school.id)
            finish()
        }
    }

    private fun setUpCancelButton() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}


