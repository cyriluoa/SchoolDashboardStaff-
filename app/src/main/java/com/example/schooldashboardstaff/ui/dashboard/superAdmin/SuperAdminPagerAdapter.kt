package com.example.schooldashboardstaff.ui.dashboard.superAdmin

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools.SchoolsFragment

class SuperAdminPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            0 -> SchoolsFragment()
            1 -> OverviewFragment()
            2 -> SettingsFragment()
            else -> throw IllegalArgumentException("Invalid tab position")
        }
    }
}
