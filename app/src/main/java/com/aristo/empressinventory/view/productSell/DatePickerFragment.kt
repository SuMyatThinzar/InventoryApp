package com.aristo.empressinventory.view.productSell

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.aristo.empressinventory.R
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireContext(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Handle the selected date (e.g., update the TextView)
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        // Assuming you have a TextView with the id tvSelectedDate
        val textView = requireActivity().findViewById<TextView>(R.id.tvDate)
        textView.text = selectedDate
    }
}
