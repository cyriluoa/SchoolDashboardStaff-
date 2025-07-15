package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.data.model.UserRole
import com.example.schooldashboardstaff.databinding.ActivityAddEditTeacherBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.SearchActivity
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue


@AndroidEntryPoint
class AddEditTeacherActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditTeacherBinding
    private lateinit var school: School

    private val viewModel: AddEditTeacherViewModel by viewModels()

    private var selectedSubjectToClassMap: Map<String, String> = emptyMap()
    private var assignedPeriods: Int = 0

    private lateinit var subjectPickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditTeacherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve school
        school = intent.getParcelableExtra("school") ?: run {
            Toast.makeText(this, "No school data found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // Register result launcher
        subjectPickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val subjectMap = result.data?.getSerializableExtra(Constants.RESULT_SUBJECT_TO_CLASS_MAP) as? HashMap<String, String>
                val assigned = result.data?.getIntExtra(Constants.RESULT_TOTAL_ASSIGNED_PERIODS, 0) ?: 0

                selectedSubjectToClassMap = subjectMap ?: emptyMap()
                assignedPeriods = assigned

                Toast.makeText(this, "Assigned $assignedPeriods periods across ${selectedSubjectToClassMap.size} subjects", Toast.LENGTH_SHORT).show()
            }
        }

        setupListeners()
        setupObservers()
        setupUsernameValidation()
    }

    private fun setupListeners() {
        binding.btnAssignSubjects.setOnClickListener {
            val maxPeriodsText = binding.etTeacherMaxPeriods1.text.toString().trim()
            val maxPeriods = maxPeriodsText.toIntOrNull()

            if (maxPeriods == null || maxPeriods <= 0) {
                Toast.makeText(this, "Please enter a valid max periods value before assigning subjects", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val intent = SearchActivity.createIntentForTeacher(
                context = this,
                schoolId = school.id,
                periodsLeft = maxPeriods,
                user = null
            )
            subjectPickerLauncher.launch(intent)
        }

        binding.btnSubmitTeacher.setOnClickListener {
            val email = binding.etTeacherEmail.text.toString().trim()
            val password = binding.etTeacherPassword.text.toString().trim()
            val firstname = binding.etTeacherFirstName.text.toString().trim()
            val lastname = binding.etTeacherLastName.text.toString().trim()
            val username = binding.etTeacherUsername.text.toString().trim()
            val maxPeriods = binding.etTeacherMaxPeriods1.text.toString().trim().toIntOrNull()

            if (email.isEmpty() || password.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || username.isEmpty() || maxPeriods == null || maxPeriods <= 0) {
                Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedSubjectToClassMap.isEmpty()) {
                Toast.makeText(this, "Please assign at least one subject", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val teacherToCreate = User(
                uid = "",
                firstname = firstname,
                lastname = lastname,
                username = username,
                email = email,
                role = UserRole.TEACHER,
                schoolId = school.id,
                subjectToClassMap = selectedSubjectToClassMap,
                isClassTeacher = false,
                maxPeriods = maxPeriods,
                assignedPeriods = assignedPeriods
            )

            viewModel.createTeacherUser(email, password, teacherToCreate)
        }
    }

    private fun setupObservers() {
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnSubmitTeacher.isEnabled = !isLoading
        }

        viewModel.statusMessage.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                if (it.startsWith("✅")) {
                    finish()
                }
                viewModel.clearStatusMessage()
            }
        }
    }

    private fun setupUsernameValidation() {
        val usernameEditText = binding.etTeacherUsername
        val statusTextView = binding.tvTeacherUsernameStatus

        usernameEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val username = s.toString().trim()
                if (username.isEmpty()) {
                    viewModel.clearUsernameAvailabilityCheck()
                } else {
                    viewModel.checkUsernameAvailability(username)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        viewModel.isUsernameAvailable.observe(this) { isAvailable ->
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

