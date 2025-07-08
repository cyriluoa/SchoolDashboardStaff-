package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.firebase.SchoolManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ActivitySchoolAdminDashboardBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.SchoolAdminDashboardFragment
import com.example.schooldashboardstaff.utils.Constants

class SchoolAdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySchoolAdminDashboardBinding
    private val schoolManager: SchoolManager = SchoolManager()
    private val schoolViewModel: SharedSchoolViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySchoolAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSchoolName()

        loadDashboardFragment()
        handleBackNavigation()
    }

    /**
     * Handles back press using OnBackPressedDispatcher
     */
    private fun handleBackNavigation() {
        onBackPressedDispatcher.addCallback(this) {
            if (supportFragmentManager.backStackEntryCount > 0) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }


    private fun loadDashboardFragment() {
        binding.fragmentContainer.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, SchoolAdminDashboardFragment())
            .commit()
    }



    /**
     * Loads and sets the school name in the welcome message
     */
    private fun setSchoolName() {
        val currentUser = intent.getParcelableExtra<User>(Constants.USER_OBJECT_INTENT_KEY) ?: return

        schoolManager.getSchoolById(
            currentUser.schoolId,
            onSuccess = { school ->
                schoolViewModel.setSchool(school)
                binding.tvWelcomeMessage.text = "Welcome to ${school.name}'s dashboard"
            },
            onFailure = {
                binding.tvWelcomeMessage.text = "Welcome to Unknown School's dashboard"
            }
        )
    }

}

