package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.schooldashboardstaff.databinding.ActivitySearchBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects.SearchSubjectsFragment
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel: SharedSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val searchType = intent.getStringExtra(Constants.SEARCH_TYPE_KEY)
        val schoolId = intent.getStringExtra(Constants.SCHOOL_ID_KEY)
        val classId = intent.getStringExtra(Constants.CLASS_ID_KEY)
        val grade = intent.getIntExtra(Constants.GRADE_KEY, 0)
        val periodsLeft = intent.getIntExtra(Constants.PERIODS_LEFT_KEY, 40)

        if (searchType == null || schoolId == null || classId == null) {
            finishWithError("Missing searchType, schoolId, or classId")
            return
        }
        // Set up clear button
        binding.btnClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        // Listen to text changes and update ViewModel
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            searchViewModel.updateQuery(text.toString())
        }

        // Load appropriate fragment
        loadSearchFragment(searchType, schoolId,classId,grade,periodsLeft)
    }

    private fun loadSearchFragment(searchType: String, schoolId: String, classId: String, grade: Int,periodsLeft: Int ) {
        val fragment: Fragment = when (searchType) {
            Constants.SEARCH_SUBJECTS -> SearchSubjectsFragment.newInstance(
                schoolId = schoolId,
                classId = classId,
                grade = grade,
                periodsLeft = periodsLeft
            )
//            Constants.SEARCH_TEACHERS -> SearchTeachersFragment.newInstance(schoolId)
//            Constants.SEARCH_CLASSES -> SearchClassesFragment.newInstance(schoolId)
//            Constants.SEARCH_STUDENTS -> SearchStudentsFragment.newInstance(schoolId)
            else -> {
                finishWithError("Invalid search type")
                return
            }
        }

        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    private fun finishWithError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        finish()
    }
}
