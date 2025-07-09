package com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.databinding.ActivityAddEditSchoolBinding
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditSchoolActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddEditSchoolBinding
    private val addEditSchoolViewModel: AddEditSchoolViewModel by viewModels()

    private var mode: String = Constants.MODE_ADD
    private var schoolId: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddEditSchoolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mode = intent.getStringExtra(Constants.MODE_KEY) ?: "add_school"

        Log.d("AESACTIVITY",mode)


        if (mode == Constants.MODE_ASSIGN) {
            schoolId = intent.getStringExtra(Constants.SCHOOL_ID_KEY)
            setupAssignAdminMode()
        }

        if(mode == Constants.MODE_EDIT) {
            schoolId = intent.getStringExtra(Constants.SCHOOL_ID_KEY)
            populateSchoolEditFields()
        }

        setupUsernameValidation()
        setupRegisterSchoolButton()
        observeViewModel()
    }


    private fun setupAssignAdminMode() {
        // Hide school form
        binding.llSchoolForm.visibility = View.GONE

        // Update UI text
        binding.tvWelcome.text = "Assign Admin"
        binding.tvSubtitle.text = "Enter details of the new administrator"
        binding.btnRegisterSchool.text = "Assign Admin"
    }

    @SuppressLint("SetTextI18n")
    private fun populateSchoolEditFields() {
        binding.tvWelcome.text = "Edit School"
        binding.tvSubtitle.text = "Update school details"
        binding.btnRegisterSchool.text = "Save Changes"

        // Hide admin fields
        binding.llAdminForm.visibility = View.GONE

        Log.d("populateSchoolEditFields", "hi")

        // Pre-fill school fields
        val sName = this.intent.getStringExtra(Constants.SCHOOL_FIELD_NAME_KEY)
        if (sName != null) {
            Log.d("populateSchoolEditFields", sName)
        }
        else{
            Log.d("populateSchoolEditFields", "name is null")
        }

        binding.etSchoolName.setText(intent.getStringExtra(Constants.SCHOOL_FIELD_NAME_KEY))
        binding.etSchoolLocation.setText(intent.getStringExtra(Constants.SCHOOL_FIELD_LOCATION_KEY))
        binding.etGradeStart.setText(intent.getIntExtra(Constants.SCHOOL_FIELD_GRADE_START_KEY, 1).toString())
        binding.etGradeEnd.setText(intent.getIntExtra(Constants.SCHOOL_FIELD_GRADE_END_KEY, 12).toString())
    }


    private fun setupUsernameValidation() {
        val usernameEditText = binding.etAdminUsername1
        val statusTextView = binding.tvUsernameStatus

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

    private fun setupRegisterSchoolButton() {
        binding.btnRegisterSchool.setOnClickListener {
            when (mode) {
                Constants.MODE_ASSIGN -> handleAssignAdmin()
                Constants.MODE_EDIT -> handleEditSchool()
                else -> handleRegisterSchoolWithAdmin()
            }
        }
    }

    private fun handleAssignAdmin() {
        val adminFirstName = binding.etAdminFirstName1.text.toString().trim()
        val adminLastName = binding.etAdminLastName1.text.toString().trim()
        val adminEmail = binding.etAdminEmail1.text.toString().trim()
        val adminUsername = binding.etAdminUsername1.text.toString().trim()
        val adminPassword = binding.etAdminPassword1.text.toString().trim()

        if (adminFirstName.isEmpty() || adminLastName.isEmpty() ||
            adminEmail.isEmpty() || adminUsername.isEmpty() || adminPassword.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all admin fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (schoolId == null) {
            Toast.makeText(this, "School ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        addEditSchoolViewModel.assignAdminToSchool(
            schoolId = schoolId!!,
            adminFirstName = adminFirstName,
            adminLastName = adminLastName,
            adminEmail = adminEmail,
            adminUsername = adminUsername,
            adminPassword = adminPassword
        )
    }

    private fun handleRegisterSchoolWithAdmin() {
        val schoolName = binding.etSchoolName.text.toString().trim()
        val location = binding.etSchoolLocation.text.toString().trim()
        val startingGrade = binding.etGradeStart.text.toString().trim()
        val finalGrade = binding.etGradeEnd.text.toString().trim()

        val adminFirstName = binding.etAdminFirstName1.text.toString().trim()
        val adminLastName = binding.etAdminLastName1.text.toString().trim()
        val adminEmail = binding.etAdminEmail1.text.toString().trim()
        val adminUsername = binding.etAdminUsername1.text.toString().trim()
        val adminPassword = binding.etAdminPassword1.text.toString().trim()

        if (schoolName.isEmpty() || location.isEmpty() ||
            startingGrade.isEmpty() || finalGrade.isEmpty() ||
            adminFirstName.isEmpty() || adminLastName.isEmpty() ||
            adminEmail.isEmpty() || adminUsername.isEmpty() || adminPassword.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        if(startingGrade.toInt() < Constants.GRADE_LOWER_LIMIT){
            Toast.makeText(this, "Starting grade must be at least 1", Toast.LENGTH_SHORT).show()
            return
        }

        if(finalGrade.toInt() > Constants.GRADE_UPPER_LIMIT){
            Toast.makeText(this, "Final grade must be at most 12", Toast.LENGTH_SHORT).show()
            return
        }
        if(startingGrade > finalGrade){
            Toast.makeText(this, "Starting grade must be greater than final grade", Toast.LENGTH_SHORT).show()
            return
        }


        addEditSchoolViewModel.registerSchoolWithAdmin(
            schoolName = schoolName,
            location = location,
            startingGrade = startingGrade.toInt(),
            finalGrade = finalGrade.toInt(),
            adminFirstName = adminFirstName,
            adminLastName = adminLastName,
            adminEmail = adminEmail,
            adminUsername = adminUsername,
            adminPassword = adminPassword
        )
    }

    private fun handleEditSchool() {
        val schoolName = binding.etSchoolName.text.toString().trim()
        val location = binding.etSchoolLocation.text.toString().trim()
        val startingGrade = binding.etGradeStart.text.toString().trim()
        val finalGrade = binding.etGradeEnd.text.toString().trim()

        if (schoolName.isEmpty() || location.isEmpty() ||
            startingGrade.isEmpty() || finalGrade.isEmpty()
        ) {
            Toast.makeText(this, "Please fill all school fields", Toast.LENGTH_SHORT).show()
            return
        }

        if (schoolId == null) {
            Toast.makeText(this, "School ID missing", Toast.LENGTH_SHORT).show()
            return
        }

        addEditSchoolViewModel.updateSchoolDetails(
            schoolId = schoolId!!,
            schoolName = schoolName,
            location = location,
            startingGrade = startingGrade.toInt(),
            finalGrade = finalGrade.toInt()
        )
    }



    private fun observeViewModel() {
        addEditSchoolViewModel.registrationStatus.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "School registered successfully", Toast.LENGTH_SHORT).show()
                finish() // Go back to SchoolsFragment
            }.onFailure { e ->
                Toast.makeText(this, "Registration failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

}

