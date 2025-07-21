package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.databinding.FragmentTimetableBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.SharedSchoolViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TimetableFragment : Fragment() {

    private var _binding: FragmentTimetableBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TimetableViewModel by viewModels()
    private val sharedSchoolViewModel: SharedSchoolViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimetableBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sharedSchoolViewModel.school.observe(viewLifecycleOwner) { school ->
            viewModel.observeSchoolClasses(school.id)
        }

        viewModel.canGenerateTimetable.observe(viewLifecycleOwner) { canGenerate ->
            if (canGenerate) {
                binding.tvTimetableMessage.text = "You can generate timetable now ✅"
                binding.tvTimetableMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
            } else {
                binding.tvTimetableMessage.text = "You can't generate timetable yet"
                binding.tvTimetableMessage.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_red))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

