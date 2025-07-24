package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable



import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.schooldashboardstaff.databinding.FragmentCreatedTimetableDraftBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters.TimetablePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CreatedTimetableDraftFragment : Fragment() {

    private var _binding: FragmentCreatedTimetableDraftBinding? = null
    private val binding get() = _binding!!

    private val sharedViewModel: SharedTimetableViewModel by activityViewModels()
    private val sharedSchoolViewModel: SharedSchoolViewModel by activityViewModels()
    private val saveViewModel: SaveTimetableViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCreatedTimetableDraftBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabTitles = listOf("Classes", "Teachers")
        val adapter = TimetablePagerAdapter(this)
        binding.viewPager.adapter = adapter // ✅ Set adapter first
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()



        binding.fabSave.setOnClickListener {
            val timetable = sharedViewModel.finalTimetable.value ?: return@setOnClickListener
            val schoolId = sharedSchoolViewModel.school.value?.id ?: return@setOnClickListener

            val input = EditText(requireContext()).apply {
                hint = "Enter draft name"
                inputType = InputType.TYPE_CLASS_TEXT
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Save Draft Timetable")
                .setView(input)
                .setPositiveButton("Save") { _, _ ->
                    val draftName = input.text.toString().trim()
                    if (draftName.isEmpty()) {
                        Toast.makeText(requireContext(), "Draft name cannot be empty", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }
                    saveViewModel.saveDraftTimetable(schoolId, draftName, timetable)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        saveViewModel.saveStatus.observe(viewLifecycleOwner) { success ->
            if (success == true) {
                Toast.makeText(requireContext(), "Draft saved successfully ✅", Toast.LENGTH_SHORT).show()
            } else if (success == false) {
                Toast.makeText(requireContext(), "Failed to save draft ❌", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
