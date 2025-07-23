package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable



import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.schooldashboardstaff.databinding.FragmentCreatedTimetableDraftBinding
import com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters.TimetablePagerAdapter
import com.google.android.material.tabs.TabLayoutMediator


class CreatedTimetableDraftFragment : Fragment() {

    private var _binding: FragmentCreatedTimetableDraftBinding? = null
    private val binding get() = _binding!!

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
            // Save logic
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
