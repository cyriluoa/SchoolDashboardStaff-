package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.timetable.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TimetablePagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount() = 2
    override fun createFragment(position: Int): Fragment = when (position) {
        0 -> ClassTimetableListFragment()
        1 -> TeacherTimetableListFragment()
        else -> throw IllegalArgumentException("Invalid tab position")
    }
}
