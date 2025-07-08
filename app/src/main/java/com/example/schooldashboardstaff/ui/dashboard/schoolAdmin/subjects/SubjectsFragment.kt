package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.data.model.Subject
import com.example.schooldashboardstaff.databinding.FragmentSubjectsBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import com.example.schooldashboardstaff.ui.schoolclass.AddEditSchoolClassActivity
import com.example.schooldashboardstaff.utils.Constants


class SubjectsFragment : Fragment() {

    private var _binding: FragmentSubjectsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SubjectAdapter
    private val subjects = mutableListOf<Subject>() // Replace with your data source

    private val sharedViewModel: SharedSchoolViewModel by activityViewModels()
    private var currentSchool: School? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSubjectsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        // Observe school from shared view model
        sharedViewModel.school.observe(viewLifecycleOwner) { school ->
            currentSchool = school
            // You can also load subjects here once you implement Firestore
        }



        setUpFabAddSubject()


        // TODO: Load subjects from Firestore and update the adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView(){
        adapter = SubjectAdapter()
        binding.rvSubjects.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSubjects.adapter = adapter
    }

    private fun setUpFabAddSubject(){
        binding.fabAddSubject.setOnClickListener {
            currentSchool?.let { school ->
                val intent = Intent(requireContext(), AddEditSubjectActivity::class.java)
                intent.putExtra(Constants.SCHOOL_OBJECT_INTENT_KEY, school) // Make sure School is Parcelable
                startActivity(intent)
            } ?: Toast.makeText(requireContext(), "No school selected", Toast.LENGTH_SHORT).show()
        }
    }
}
