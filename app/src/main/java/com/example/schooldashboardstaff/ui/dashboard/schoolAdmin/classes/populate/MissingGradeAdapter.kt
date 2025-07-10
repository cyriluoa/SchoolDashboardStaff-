package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.classes.populate

import android.graphics.Typeface
import android.text.InputType
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.RecyclerView
import com.example.schooldashboardstaff.R
import com.example.schooldashboardstaff.data.model.input.MissingGradeInput
import com.example.schooldashboardstaff.data.model.input.SectionInput
import com.example.schooldashboardstaff.databinding.ItemMissingGradeInputBinding
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MissingGradeAdapter(
    val items: MutableList<MissingGradeInput>, // 🔓 public
    private val onCompletionChanged: () -> Unit
) : RecyclerView.Adapter<MissingGradeAdapter.MissingGradeViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long = items[position].grade.toLong()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MissingGradeViewHolder {
        val binding = ItemMissingGradeInputBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MissingGradeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MissingGradeViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class MissingGradeViewHolder(private val binding: ItemMissingGradeInputBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isExpanded = false

        fun bind(item: MissingGradeInput) {
            isExpanded = false
            binding.containerSectionInputs.removeAllViews()
            binding.containerSectionInputs.visibility = View.GONE

            binding.tvGrade.text = "Grade ${item.grade}"
            binding.etSectionCount.setText(
                if (item.numSections > 0) item.numSections.toString() else ""
            )

            // Reset listener first to prevent recycling side-effects
            binding.etSectionCount.setOnEditorActionListener(null)

            binding.etSectionCount.doAfterTextChanged { editable ->
                val newCount = editable.toString().toIntOrNull()
                if (newCount != null && newCount in 1..26 && newCount != item.numSections) {
                    item.numSections = newCount
                    item.sectionInputs.clear()
                    item.isCompleted = false
                    updateCompletionState(item)
                    onCompletionChanged()
                }
            }

            updateCompletionState(item)

            binding.ivExpandToggle.setOnClickListener {
                if (!isExpanded) {
                    val count = binding.etSectionCount.text.toString().toIntOrNull()
                    if (count != null && count in 1..26) {
                        item.numSections = count
                        expandSectionInputs(item)
                        isExpanded = true
                        binding.containerSectionInputs.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Enter valid section count (1–26)",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    collapse()
                }
            }
        }

        private fun expandSectionInputs(item: MissingGradeInput) {
            binding.containerSectionInputs.removeAllViews()

            if (item.sectionInputs.size != item.numSections) {
                item.sectionInputs.clear()
                repeat(item.numSections) {
                    item.sectionInputs.add(SectionInput(('A' + it).toString()))
                }
            }

            item.sectionInputs.forEachIndexed { index, sectionInput ->
                val sectionLetter = ('A' + index).toString()

                val sectionLayout = LinearLayout(binding.root.context).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, 12, 0, 12)
                }

                val label = TextView(binding.root.context).apply {
                    text = "Section $sectionLetter"
                    setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    textSize = 16f
                    setTypeface(null, Typeface.BOLD)
                }

                val maxStudentsInputLayout = TextInputLayout(binding.root.context).apply {
                    hint = "Max Students"
                }

                val maxStudentsEditText = TextInputEditText(binding.root.context).apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(sectionInput.maxStudents)
                }

                maxStudentsEditText.doAfterTextChanged {
                    val newVal = it.toString()
                    if (newVal != sectionInput.maxStudents) {
                        sectionInput.maxStudents = newVal
                        if (item.isCompleted) {
                            item.isCompleted = false
                            updateCompletionState(item)
                            onCompletionChanged()
                        }
                    }
                }

                maxStudentsInputLayout.addView(maxStudentsEditText)

                val maxPeriodsInputLayout = TextInputLayout(binding.root.context).apply {
                    hint = "Max Periods"
                }

                val maxSubjectsEditText = TextInputEditText(binding.root.context).apply {
                    inputType = InputType.TYPE_CLASS_NUMBER
                    setText(sectionInput.maxPeriods)
                }

                maxSubjectsEditText.doAfterTextChanged {
                    val newVal = it.toString()
                    if (newVal != sectionInput.maxPeriods) {
                        sectionInput.maxPeriods = newVal
                        if (item.isCompleted) {
                            item.isCompleted = false
                            updateCompletionState(item)
                            onCompletionChanged()
                        }
                    }
                }

                maxPeriodsInputLayout.addView(maxSubjectsEditText)

                sectionLayout.addView(label)
                sectionLayout.addView(maxStudentsInputLayout)
                sectionLayout.addView(maxPeriodsInputLayout)

                binding.containerSectionInputs.addView(sectionLayout)
            }

            addConfirmCancelButtons(item)
        }

        private fun addConfirmCancelButtons(item: MissingGradeInput) {
            val buttonLayout = LinearLayout(binding.root.context).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.END
                setPadding(0, 16, 0, 0)
            }

            val cancelButton = Button(binding.root.context).apply {
                text = "Cancel"
                setOnClickListener { collapse() }
            }

            val confirmButton = Button(binding.root.context).apply {
                text = "Confirm"
                setOnClickListener {
                    val allValid = item.sectionInputs.all {
                        it.maxStudents.isNotEmpty() && it.maxPeriods.isNotEmpty()
                    }

                    if (allValid) {
                        item.isCompleted = true
                        updateCompletionState(item)
                        collapse()
                        onCompletionChanged()
                    } else {
                        Toast.makeText(
                            binding.root.context,
                            "Please fill all inputs",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

            buttonLayout.addView(cancelButton)
            buttonLayout.addView(confirmButton)
            binding.containerSectionInputs.addView(buttonLayout)
        }

        private fun collapse() {
            isExpanded = false
            binding.containerSectionInputs.removeAllViews()
            binding.containerSectionInputs.visibility = View.GONE
        }

        private fun updateCompletionState(item: MissingGradeInput) {
            if (item.isCompleted) {
                binding.ivCompleteCheck.setImageResource(R.drawable.ic_green_circle)
                binding.mcvMissingGrade.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.green)
                binding.tvGrade.setTextColor(ContextCompat.getColor(binding.root.context, R.color.green))
                binding.ivExpandToggle.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.green),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )


            } else {
                binding.ivCompleteCheck.setImageResource(R.drawable.ic_red_circle)
                binding.mcvMissingGrade.strokeColor =
                    ContextCompat.getColor(binding.root.context, R.color.red)
                binding.tvGrade.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red))
                binding.ivExpandToggle.setColorFilter(
                    ContextCompat.getColor(binding.root.context, R.color.red),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }
        }
    }


}



