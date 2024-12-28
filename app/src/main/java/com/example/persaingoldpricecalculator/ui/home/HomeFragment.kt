package com.example.persaingoldpricecalculator.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.persaingoldpricecalculator.NumberTextWatcherForThousand
import com.example.persaingoldpricecalculator.R
import com.example.persaingoldpricecalculator.databinding.FragmentHomeBinding
import java.text.DecimalFormat

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.buttonCalculate.setOnClickListener { view ->
            handleButtonClick(view)
        }

        binding.editDailyGoldPrice.addThousandSeparator();
        binding.editAttachments.addThousandSeparator();
        binding.editNetWeight.addThousandSeparator();

        val text = "(سود فروش طلا + اجرت ساخت) ✕ ۹ %";
        val valueAddedTax = getTaxPercentSetting();
        binding.textTaxDesc.text = "(سود فروش طلا + اجرت ساخت) ✕ 9 %".replace("9", valueAddedTax.toString());

        return root
    }

    private fun EditText.addThousandSeparator() {
        this.addTextChangedListener(NumberTextWatcherForThousand(this))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun handleButtonClick(view: View?) {
        // Handle the button click here
       // Toast.makeText(this.activity, "Button clicked!", Toast.LENGTH_SHORT).show()

        val goldPrice = getNumber(binding.editDailyGoldPrice);
        if(goldPrice < 0) {
            Toast.makeText(this.activity, "Invalid goldPrice. Please enter a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val netWeight = getNumber(binding.editNetWeight);
        if(netWeight < 0) {
            Toast.makeText(this.activity, "Invalid netWeight. Please enter a valid number.", Toast.LENGTH_SHORT).show()
            return
        }

        val goldValue = goldPrice * netWeight;
        binding.textGoldValue.text = getString(R.string.value_result, DecimalFormat("#,###").format(goldValue))

        val wagePercent = getNumber(binding.editConstructionWages);
        var constructionWage = wagePercent;
        if( binding.constructionWagePercent.isChecked) {
            constructionWage = goldValue * (wagePercent / 100);
        }
        binding.textResultConstructionWage.text =
            getString(R.string.value_result, DecimalFormat("#,###").format(constructionWage));

        val salesProfitPercent = getNumber(binding.editSalesProfitPercent);
        val salesProfit = (goldValue + constructionWage) * (salesProfitPercent / 100);
        binding.textResultSalesWage.text = getString(R.string.value_result, DecimalFormat("#,###").format(salesProfit))

        val tax = (salesProfit + constructionWage) * 0.09;
        binding.textResultTax.text =
            getString(R.string.value_result, DecimalFormat("#,###").format(tax))

        val attachments = getNumber(binding.editAttachments);
        val finalPrice = goldValue + constructionWage + salesProfit + tax + attachments;
        binding.textFinalValue.text =
            getString(R.string.value_result, DecimalFormat("#,###").format(finalPrice))

        binding.rootScroll.smoothScrollTo(0, binding.rootScroll.getChildAt(0).bottom);

    }

    private fun getNumber(numberEditText: EditText): Float {
        val enteredNumberString = NumberTextWatcherForThousand.trimCommaOfString(numberEditText.getText().toString())

        // Check if the input is empty
        if (enteredNumberString.isEmpty()) {
            return 0F;
        }

        // Parse the entered string to an integer (or other desired numeric type)
        val enteredNumber: Float
        try {
            enteredNumber = enteredNumberString.toFloat()
            return enteredNumber;
        } catch (e: NumberFormatException) {
            return -1F;
        }
    }

    private fun getTaxPercentSetting(): Float {
        val sharedPreferences = requireContext().getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
        return sharedPreferences.getFloat("valueAddedTax", 9F) // Default to 9
    }
}