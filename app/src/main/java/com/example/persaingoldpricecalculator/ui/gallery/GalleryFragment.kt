package com.example.persaingoldpricecalculator.ui.gallery

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.persaingoldpricecalculator.R
import com.example.persaingoldpricecalculator.databinding.FragmentGalleryBinding
import com.google.android.material.navigation.NavigationView

class GalleryFragment : Fragment() {

    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val galleryViewModel =
            ViewModelProvider(this).get(GalleryViewModel::class.java)

        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val taxPercent = getTaxPercentSetting();
        binding.editValueAddedTax.setText(taxPercent.toString());

        binding.buttonSave.setOnClickListener { view ->
            handleButtonClick(view)
        }

        return root
    }

    private fun handleButtonClick(view: View?) {
        val enteredNumberString = binding.editValueAddedTax.text.toString();
        var enteredNumber = 9f;
        try {
            enteredNumber = enteredNumberString.toFloat();
        } catch (e: NumberFormatException) {
            Toast.makeText(this.activity, "مقدار مالیات نامعتبر است!", Toast.LENGTH_SHORT).show()
            return;
        }

        setTaxPercentSetting(enteredNumber);
        activity?.supportFragmentManager?.popBackStack()

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getTaxPercentSetting(): Float {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("valueAddedTax", 9F) // Default to 9
    }

    private fun setTaxPercentSetting(value: Float) {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        sharedPreferences.edit().putFloat("valueAddedTax", value).apply()
    }

}