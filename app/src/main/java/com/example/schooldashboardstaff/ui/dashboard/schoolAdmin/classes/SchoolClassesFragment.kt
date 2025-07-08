package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.School
import com.example.schooldashboardstaff.databinding.FragmentSchoolClassesBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.populate.MissingGradeDialogFragment
import com.example.schooldashboardstaff.ui.schoolclass.AddEditSchoolClassActivity
import com.example.schooldashboardstaff.utils.Constants


class SchoolClassesFragment : Fragment() {

    private var _binding: FragmentSchoolClassesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: SchoolClassAdapter
    private lateinit var sharedViewModel: SharedSchoolViewModel

    private var currentSchool: School? = null
    private lateinit var viewModel: SchoolClassesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolClassesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedViewModel = ViewModelProvider(requireActivity())[SharedSchoolViewModel::class.java]

        sharedViewModel.school.observe(viewLifecycleOwner) { school ->
            school?.let {
                currentSchool = it
                viewModel = SchoolClassesViewModel(it)
                setupRecyclerView()
                setupFabClick()
                binding.btnPopulateMissingGrades.setOnClickListener {
                    val missingGrades = viewModel.missingGrades.value ?: return@setOnClickListener

                    val dialog = MissingGradeDialogFragment(
                        missingGrades = missingGrades,
                        onSubmit = { filledInputs ->
                            viewModel.populateMissingGrades(filledInputs)
                        }
                    )
                    dialog.show(childFragmentManager, "MissingGradeDialog")
                }

                setupObservers()
                // No need to call viewModel.listenToClasses() here — it's done in init
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerView() {
        adapter = SchoolClassAdapter()
        binding.rvClasses.adapter = adapter
        binding.rvClasses.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupFabClick() {
        binding.fabAddClass.setOnClickListener {
            currentSchool?.let {
                val intent = Intent(requireContext(), AddEditSchoolClassActivity::class.java).apply {
                    putExtra(Constants.SCHOOL_OBJECT_INTENT_KEY, it)
                }
                startActivity(intent)
            }
        }
    }

    private fun setupObservers() {
        viewModel.classes.observe(viewLifecycleOwner) { schoolClasses ->
            adapter.submitList(schoolClasses)
        }
        viewModel.missingGrades.observe(viewLifecycleOwner) { missingGrades ->
            binding.layoutMissingGrades.visibility =
                if (missingGrades.isNotEmpty()) View.VISIBLE else View.GONE
        }

    }


}