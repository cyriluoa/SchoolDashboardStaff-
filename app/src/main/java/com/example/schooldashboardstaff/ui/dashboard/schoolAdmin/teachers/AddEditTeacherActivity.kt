package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.databinding.ActivityAddEditTeacherBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SearchActivity
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class AddEditTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTeacherBinding
    private lateinit var school: School

    private val addEditSchoolViewModel: AddEditTeacherViewModel by viewModels()

    private lateinit var subjectPickerLauncher: ActivityResultLauncher<Intent>
    private var selectedSubjectIds: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve school object
        school = intent.getParcelableExtra("school") ?: run {
            Toast.makeText(this, "No school data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Register result launcher
        subjectPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedIds = result.data?.getStringArrayListExtra(Constants.RESULT_SELECTED_SUBJECT_IDS)
                selectedSubjectIds = selectedIds ?: emptyList()

                Toast.makeText(this, "Selected ${selectedSubjectIds.size} subjects", Toast.LENGTH_SHORT).show()
            }
        }

        setupListeners()
        setupUsernameValidation()
    }

    private fun setupListeners() {
        // Handle Assign Subjects button
        binding.btnAssignSubjects.setOnClickListener {
            val maxPeriodsText = binding.etTeacherMaxPeriods1.text.toString().trim()
            val maxPeriods = maxPeriodsText.toIntOrNull()

            if (maxPeriods == null || maxPeriods <= 0) {
                Toast.makeText(
                    this,
                    "Please enter a valid max periods value before assigning subjects",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            val intent = SearchActivity.createIntentForTeacher(
                context = this,
                schoolId = school.id,
                periodsLeft = maxPeriods,
                user = null // null for creation
            )
            subjectPickerLauncher.launch(intent)

        }

        // Handle Submit button (for now it's just a placeholder)
        binding.btnSubmitTeacher.setOnClickListener {
            Toast.makeText(this, "Submit logic not implemented yet", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupUsernameValidation() {
        val usernameEditText = binding.etTeacherUsername
        val statusTextView = binding.tvTeacherUsernameStatus

        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val username = s.toString().trim()
                if (username.isEmpty()) {
                    addEditSchoolViewModel.clearUsernameAvailabilityCheck()
                } else {
                    addEditSchoolViewModel.checkUsernameAvailability(username)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        addEditSchoolViewModel.isUsernameAvailable.observe(this) { isAvailable ->
            when (isAvailable) {
                true -> {
                    statusTextView.text = "✅ Username is available"
                    statusTextView.setTextColor(ContextCompat.getColor(this, R.color.green))
                }
                false -> {
                    statusTextView.text = "❌ Username is already taken"
                    statusTextView.setTextColor(ContextCompat.getColor(this, R.color.red))
                }
                null -> {
                    statusTextView.text = ""
                }
            }
        }
    }
}
