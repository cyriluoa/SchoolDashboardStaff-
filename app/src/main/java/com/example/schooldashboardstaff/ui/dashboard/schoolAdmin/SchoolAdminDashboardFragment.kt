package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.firebase.SchoolManager
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.FragmentSchoolAdminDashboardBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.SchoolClassesFragment
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects.SubjectsFragment
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.teachers.TeachersFragment
import com.example.schooldashboardstaff.utils.Constants

class SchoolAdminDashboardFragment: Fragment() {

    private var _binding: FragmentSchoolAdminDashboardBinding? = null
    private val binding get() = _binding!!

    private val schoolManager: SchoolManager = SchoolManager()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInitialUi()
        setupTileClickListeners()
    }

    private fun setDashboardTile(view: View, iconResId: Int, label: String) {
        val imageView = view.findViewById<ImageView>(R.id.iviconDashboard)
        val textView = view.findViewById<TextView>(R.id.tvDashboardLabel)
        imageView.setImageResource(iconResId)
        textView.text = label
    }

    private fun setUpInitialUi() {
        setDashboardTile(binding.itemClasses, R.drawable.ic_class, "Classes")
        setDashboardTile(binding.itemTeachers, R.drawable.ic_teacher, "Teachers")
        setDashboardTile(binding.itemSubjects, R.drawable.ic_subjects, "Subjects")
        setDashboardTile(binding.itemStudents, R.drawable.ic_student, "Students")
        setDashboardTile(binding.itemHolidays, R.drawable.ic_holidays, "Holidays")

        val currentUser = requireActivity()
            .intent.getParcelableExtra<User>(Constants.USER_OBJECT_INTENT_KEY) ?: return

        schoolManager.getSchoolById(
            currentUser.schoolId,
            onSuccess = { school ->
                requireActivity().findViewById<TextView>(R.id.tvWelcomeMessage).text =
                    "Welcome to ${school.name}'s dashboard"
            },
            onFailure = {
                requireActivity().findViewById<TextView>(R.id.tvWelcomeMessage).text =
                    "Welcome to Unknown School's dashboard"
            }
        )
    }

    private fun setupTileClickListeners() {
        binding.itemClasses.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SchoolClassesFragment(), "ClassesFragment")
                .addToBackStack("ClassesFragment")
                .commit()
        }

        binding.itemTeachers.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, TeachersFragment(), "TeachersFragment")
                .addToBackStack("TeachersFragment")
                .commit()
        }

        binding.itemSubjects.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, SubjectsFragment(), "SubjectsFragment")
                .addToBackStack("SubjectsFragment")
                .commit()
        }

        binding.itemStudents.setOnClickListener {
            Toast.makeText(requireContext(), "Navigate to Students", Toast.LENGTH_SHORT).show()
        }

        binding.itemHolidays.setOnClickListener {
            Toast.makeText(requireContext(), "Navigate to Holidays", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}