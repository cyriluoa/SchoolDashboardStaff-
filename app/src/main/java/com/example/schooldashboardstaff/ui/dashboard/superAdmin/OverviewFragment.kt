package com.example.schooldashboardstaff.ui.dashboard.superAdmin

import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.schooldashboardstaff.R


class OverviewFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val textView = TextView(requireContext())
        textView.text = "Overview Fragment"
        textView.gravity = Gravity.CENTER
        return textView
    }

}