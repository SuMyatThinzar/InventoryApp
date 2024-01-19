package com.aristo.empressinventory.utils

import android.content.Context
import android.content.Intent
import android.widget.Toast

fun addCommasToNumber(number: Int): String {
    return String.format("%,d", number)
}


fun showToastMessage(context: Context, message: String?) {
    message?.let {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}
