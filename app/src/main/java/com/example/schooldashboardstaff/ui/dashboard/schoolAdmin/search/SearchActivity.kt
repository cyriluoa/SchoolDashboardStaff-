package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.example.schooldashboardstaff.data.model.SchoolClass
import com.example.schooldashboardstaff.data.model.SearchParams
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ActivitySearchBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.subjects.SearchSubjectsFragment
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.search.teachers.SearchTeachersFragment
import com.example.schooldashboardstaff.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : AppCompatActivity() {


    companion object {
        fun createIntentForClass(
            context: Context,
            schoolId: String,
            classId: String,
            grade: Int,
            periodsLeft: Int,
            subjectIds: Array<String>
        ): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(Constants.SEARCH_TYPE_KEY, Constants.SEARCH_SUBJECTS)
                putExtra(Constants.SCHOOL_ID_KEY, schoolId)
                putExtra(Constants.CLASS_ID_KEY, classId)
                putExtra(Constants.GRADE_KEY, grade)
                putExtra(Constants.PERIODS_LEFT_KEY, periodsLeft)
                putExtra(Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY, subjectIds)
            }
        }

        fun createIntentForTeacher(
            context: Context,
            schoolId: String,
            user: User? = null
        ): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(Constants.SEARCH_TYPE_KEY, Constants.SEARCH_SUBJECTS_FOR_TEACHER)
                putExtra(Constants.SCHOOL_ID_KEY, schoolId)
                putExtra(Constants.USER_OBJECT_INTENT_KEY, user)
            }
        }

        fun createIntentForAssignTeacher(
            context: Context,
            schoolId: String,
            subject: Subject,
            schoolClass: SchoolClass
        ): Intent {
            return Intent(context, SearchActivity::class.java).apply {
                putExtra(Constants.SEARCH_TYPE_KEY, Constants.SEARCH_TEACHERS)
                putExtra(Constants.SCHOOL_ID_KEY, schoolId)
                putExtra(Constants.SUBJECT_OBJECT_KEY, subject)
                putExtra(Constants.CLASS_OBJECT_KEY, schoolClass)
            }
        }

    }


    private lateinit var binding: ActivitySearchBinding
    private val searchViewModel: SharedSearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val params = parseIntent()
        if (params == null) {
            finishWithError("Missing or invalid intent data")
            return
        }

        // Set up clear button
        binding.btnClear.setOnClickListener {
            binding.etSearch.text?.clear()
        }

        // Update query on text change
        binding.etSearch.doOnTextChanged { text, _, _, _ ->
            searchViewModel.updateQuery(text.toString())
        }

        // Load appropriate fragment
        loadSearchFragment(params)
    }


    private fun parseIntent(): SearchParams? {
        val searchType = intent.getStringExtra(Constants.SEARCH_TYPE_KEY) ?: return null
        val schoolId = intent.getStringExtra(Constants.SCHOOL_ID_KEY) ?: return null

        val subject = intent.getParcelableExtra<Subject>(Constants.SUBJECT_OBJECT_KEY)
        val schoolClass = intent.getParcelableExtra<SchoolClass>(Constants.CLASS_OBJECT_KEY)

        val classId = intent.getStringExtra(Constants.CLASS_ID_KEY)
        val grade = intent.getIntExtra(Constants.GRADE_KEY, -1)
        val periodsLeft = intent.getIntExtra(Constants.PERIODS_LEFT_KEY, 40)
        val subjectKeys = intent.getStringArrayExtra(Constants.SUBJECTS_IDS_FIELD_SCHOOL_CLASSES_KEY)
        val user = intent.getParcelableExtra<User>(Constants.USER_OBJECT_INTENT_KEY)

        return SearchParams(
            type = searchType,
            schoolId = schoolId,
            classId = classId,
            grade = grade,
            periodsLeft = periodsLeft,
            subjectKeys = subjectKeys,
            user = user,
            subject = subject,
            schoolClass = schoolClass
        )
    }

    private fun loadSearchFragment(params: SearchParams) {
        val fragment = when (params.type) {
            Constants.SEARCH_SUBJECTS -> {
                if (params.classId == null || params.subjectKeys == null) {
                    finishWithError("Missing classId or subjectKeys for subject search")
                    return
                }
                SearchSubjectsFragment.newInstanceForClass(
                    schoolId = params.schoolId,
                    classId = params.classId,
                    grade = params.grade,
                    periodsLeft = params.periodsLeft,
                    assignedSubjectIds = params.subjectKeys
                )
            }

            Constants.SEARCH_SUBJECTS_FOR_TEACHER -> {
                SearchSubjectsFragment.newInstanceForTeacher(
                    schoolId = params.schoolId,
                    user = params.user
                )
            }

            Constants.SEARCH_TEACHERS -> {
                if (params.subject == null || params.schoolClass == null) {
                    finishWithError("Missing subject or class for assigning teacher")
                    return
                }
                SearchTeachersFragment.newInstance(
                    schoolId = params.schoolId,
                    subject = params.subject,
                    schoolClass = params.schoolClass
                )
            }


            else -> {
                finishWithError("Invalid search type: ${params.type}")
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

