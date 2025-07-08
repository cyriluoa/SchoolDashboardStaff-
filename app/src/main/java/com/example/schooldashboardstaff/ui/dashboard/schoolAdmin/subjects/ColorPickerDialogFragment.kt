package com.example.schooldashboardstaff.ui.dashboard.schoolAdmin.subjects

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.schooldashboardstaff.databinding.DialogColorPickerBinding

class ColorPickerDialogFragment(
    private val initialColor: Int = Color.RED,
    private val onColorSelected: (Int) -> Unit
) : DialogFragment() {

    private lateinit var binding: DialogColorPickerBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // ✅ Use layoutInflater directly
        binding = DialogColorPickerBinding.inflate(layoutInflater)

        val red = Color.red(initialColor)
        val green = Color.green(initialColor)
        val blue = Color.blue(initialColor)

        binding.seekRed.progress = red
        binding.seekGreen.progress = green
        binding.seekBlue.progress = blue
        updateColorPreview(red, green, blue)

        // ✅ Use direct implementation instead of aliasing or invalid lambda
        val seekBarListener = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateColorPreview(
                    binding.seekRed.progress,
                    binding.seekGreen.progress,
                    binding.seekBlue.progress
                )
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        }

        binding.seekRed.setOnSeekBarChangeListener(seekBarListener)
        binding.seekGreen.setOnSeekBarChangeListener(seekBarListener)
        binding.seekBlue.setOnSeekBarChangeListener(seekBarListener)

        return AlertDialog.Builder(requireContext())
            .setTitle("Pick a Color")
            .setView(binding.root)
            .setPositiveButton("Select") { _, _ ->
                val selectedColor = Color.rgb(
                    binding.seekRed.progress,
                    binding.seekGreen.progress,
                    binding.seekBlue.progress
                )
                onColorSelected(selectedColor)
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun updateColorPreview(r: Int, g: Int, b: Int) {
        binding.viewColorPreview.setBackgroundColor(Color.rgb(r, g, b))
    }
}
