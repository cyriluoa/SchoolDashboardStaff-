package com.example.schooldashboardstaff.ui.dashboard.superAdmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.schooldashboardstaff.data.model.User
import com.example.schooldashboardstaff.databinding.ActivitySuperAdminDashboardBinding
import com.example.schooldashboardstaff.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator

class SuperAdminDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySuperAdminDashboardBinding
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySuperAdminDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUser = intent.getParcelableExtra(Constants.USER_OBJECT_INTENT_KEY)
            ?: run {
                Toast.makeText(this, "User data missing", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

        setupSuperAdminTabs()
    }

    private fun setupSuperAdminTabs() {
        val adapter = SuperAdminPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Schools"
                1 -> "Overview"
                2 -> "Settings"
                else -> null
            }
        }.attach()
    }
}

