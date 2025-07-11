package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.annotation.SuppressLint
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

        setUpObservers()
        setUpColorPicker()
        setUpSubmitButton()
        setUpCancelButton()
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

    @SuppressLint("ResourceAsColor")
    private fun setUpSubmitButton() {
        binding.btnSubmitSubject.setOnClickListener {
            val name = binding.etSubjectName.text.toString().trim()
            val startGrade = binding.etGradeStart.text.toString().toIntOrNull()
            val endGrade = binding.etGradeEnd.text.toString().toIntOrNull()
            val periods = binding.etPeriodCount.text.toString().toIntOrNull()

            if (name.isEmpty() || startGrade == null || endGrade == null || periods == null) {
                Toast.makeText(this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (startGrade > endGrade) {
                Toast.makeText(this, "Start grade cannot be greater than end grade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (startGrade < school.startingGrade || endGrade > school.finalGrade) {
                Toast.makeText(this, "Grade range must be between ${school.startingGrade} and ${school.finalGrade}", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (periods !in 1..12) {
                Toast.makeText(this, "Please enter number of periods between 1 and 12", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val subjects = (startGrade..endGrade).map { grade ->
                val displayName = "$name - Grade $grade"
                Subject(
                    name = name.uppercase(),
                    displayName = displayName,
                    grade = grade,
                    colorInt = selectedColor,
                    periodCount = periods
                )
            }

            viewModel.addSubjectsBatch(subjects, school.id)
            finish()
        }
    }

    private fun setUpCancelButton() {
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }
}


