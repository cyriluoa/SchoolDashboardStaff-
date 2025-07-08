package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.populate

import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.schooldashboardstaff.data.model.input.MissingGradeInput
import com.example.schooldashboardstaff.databinding.DialogPopulateMissingGradesBinding


class MissingGradeDialogFragment(
    private val missingGrades: List<Int>, // e.g. listOf(6, 7, 9)
    private val onSubmit: (List<MissingGradeInput>) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogPopulateMissingGradesBinding
    private lateinit var adapter: MissingGradeAdapter
    private lateinit var gradeInputs: MutableList<MissingGradeInput>

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogPopulateMissingGradesBinding.inflate(layoutInflater)

        gradeInputs = missingGrades.map { MissingGradeInput(it) }.toMutableList()
        adapter = MissingGradeAdapter(gradeInputs) {
            val allComplete = gradeInputs.all { it.isCompleted }
            binding.btnSubmitClass.isEnabled = allComplete
            binding.btnSubmitClass.alpha = if (allComplete) 1f else 0.5f
        }

        binding.rvMissingGrades.adapter = adapter
        binding.rvMissingGrades.layoutManager = LinearLayoutManager(requireContext())

        // 🔼 Set initial button disabled
        binding.btnSubmitClass.isEnabled = false
        binding.btnSubmitClass.alpha = 0.5f

        val dialog = Dialog(requireContext())
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)

        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.96).toInt(), // ~96% of screen width
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        binding.btnCancel.setOnClickListener { dismiss() }

        binding.btnSubmitClass.setOnClickListener {
            onSubmit(adapter.items) // Send back to parent
            dismiss()
        }
    }
}
