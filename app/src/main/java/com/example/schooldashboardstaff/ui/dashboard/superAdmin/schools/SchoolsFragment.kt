package com.example.schooldashboardstaff.ui.dashboard.superAdmin.schools

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.DisplaySchool
import com.example.schooldashboardstaff.databinding.FragmentSchoolsBinding
import com.example.schooldashboardstaff.utils.Constants
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SchoolsFragment : Fragment() {

    private var _binding: FragmentSchoolsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SchoolsViewModel by viewModels()
    private lateinit var adapter: SchoolAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSchoolsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SchoolAdapter()
        binding.rvSchools.adapter = adapter
        binding.rvSchools.layoutManager = LinearLayoutManager(requireContext())

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.listenToSchools()  // re-fetch schools from Firestore

            // Optional: If fetchSchools is async and LiveData handles updates,
            // just stop refreshing immediately (since data updates via observer)
            binding.swipeRefreshLayout.isRefreshing = false
        }

        binding.swipeRefreshLayout.setColorSchemeResources(
            R.color.primary_red,
            R.color.black
        )



        viewModel.displaySchools.observe(viewLifecycleOwner) { displayList ->
            adapter.submitList(displayList)
            binding.swipeRefreshLayout.isRefreshing = false
        }



        viewModel.listenToSchools()

        adapter.onDeleteClick = { displaySchool ->
            // Inflate the custom dialog layout
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_school, null)

            // Create the dialog
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Set transparent background to show custom styling
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set school name in the dialog
            val tvSchoolName = dialogView.findViewById<TextView>(R.id.tvSchoolNameDelete)
            tvSchoolName.text = displaySchool.school.name

            // Handle cancel button
            val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            // Handle delete button
            val btnDelete = dialogView.findViewById<AppCompatButton>(R.id.btnConfirm)
            btnDelete.setOnClickListener {
                dialog.dismiss()

                // Step 1: Visually remove the school from the list
                val index = adapter.currentList.indexOf(displaySchool)
                val mutableList = adapter.currentList.toMutableList()
                mutableList.remove(displaySchool)
                adapter.submitList(mutableList)

                var undoClicked = false

                // Step 2: Show Snackbar with Undo option
                val snackbar = Snackbar.make(requireView(), "${displaySchool.school.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        undoClicked = true
                        val restoredList = adapter.currentList.toMutableList()
                        restoredList.add(index, displaySchool)
                        adapter.submitList(restoredList)
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (!undoClicked) {
                                viewModel.deleteSchoolWithAdmins(displaySchool.school)
                            }
                        }
                    })



                snackbar.show()

            }


            dialog.show()
        }

        setUpDeleteClickListener()

        setUpEditClickListener()

        setUpAssignClickListener()

        setUpAddClickListener()






    }

    private fun setUpDeleteClickListener(){
        adapter.onDeleteClick = { displaySchool ->
            // Inflate the custom dialog layout
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_school, null)

            // Create the dialog
            val dialog = AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create()

            // Set transparent background to show custom styling
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // Set school name in the dialog
            val tvSchoolName = dialogView.findViewById<TextView>(R.id.tvSchoolNameDelete)
            tvSchoolName.text = displaySchool.school.name

            // Handle cancel button
            val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
            btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            // Handle delete button
            val btnDelete = dialogView.findViewById<AppCompatButton>(R.id.btnConfirm)
            btnDelete.setOnClickListener {
                dialog.dismiss()

                // Step 1: Visually remove the school from the list
                val index = adapter.currentList.indexOf(displaySchool)
                val mutableList = adapter.currentList.toMutableList()
                mutableList.remove(displaySchool)
                adapter.submitList(mutableList)

                var undoClicked = false

                // Step 2: Show Snackbar with Undo option
                val snackbar = Snackbar.make(requireView(), "${displaySchool.school.name} deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        undoClicked = true
                        val restoredList = adapter.currentList.toMutableList()
                        restoredList.add(index, displaySchool)
                        adapter.submitList(restoredList)
                    }
                    .addCallback(object : Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if (!undoClicked) {
                                viewModel.deleteSchoolWithAdmins(displaySchool.school)
                            }
                        }
                    })



                snackbar.show()

            }


            dialog.show()
        }
    }


    private fun setUpEditClickListener(){
        adapter.onEditClick = { displaySchool: DisplaySchool ->
            val intent = Intent(requireContext(), AddEditSchoolActivity::class.java)
            intent.putExtra(Constants.MODE_KEY, Constants.MODE_EDIT)
            intent.putExtra(Constants.SCHOOL_ID_KEY, displaySchool.school.id)
            intent.putExtra(Constants.SCHOOL_FIELD_NAME_KEY, displaySchool.school.name)
            intent.putExtra(Constants.SCHOOL_FIELD_LOCATION_KEY, displaySchool.school.location)
            intent.putExtra(Constants.SCHOOL_FIELD_GRADE_START_KEY, displaySchool.school.startingGrade)
            intent.putExtra(Constants.SCHOOL_FIELD_GRADE_END_KEY, displaySchool.school.finalGrade)
            startActivity(intent)

        }
    }

    private fun setUpAssignClickListener(){
        adapter.onAssignAdminClick = { displaySchool: DisplaySchool ->
            val intent = Intent(requireContext(), AddEditSchoolActivity::class.java)
            intent.putExtra(Constants.MODE_KEY, Constants.MODE_ASSIGN)
            intent.putExtra(Constants.SCHOOL_ID_KEY, displaySchool.school.id)

            startActivity(intent)

        }
    }

    private fun setUpAddClickListener(){
        binding.fabAddSchool.setOnClickListener {
            val intent = Intent(requireContext(), AddEditSchoolActivity::class.java)
            intent.putExtra(Constants.MODE_KEY, Constants.MODE_ADD)
            startActivity(intent)
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.listenToSchools()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.removeSchoolsListener()
        _binding = null
    }
}

