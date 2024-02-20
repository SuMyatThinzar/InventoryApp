package com.aristo.empressinventory.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import com.aristo.empressinventory.R
import com.aristo.empressinventory.data.vos.ProductVO
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun addCommasToNumber(number: Int): String {
    return String.format("%,d", number)
}


fun showToastMessage(context: Context, message: String?) {
    message?.let {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}


fun exportToExcel(products: List<ProductVO>, outputFile: File) : String {
    val workbook: Workbook = XSSFWorkbook()
    val sheet: Sheet = workbook.createSheet("Product Data")

    // Create header row
    val headerRow: Row = sheet.createRow(0)
    headerRow.createCell(0).setCellValue("ID")
    headerRow.createCell(1).setCellValue("Payment Complete")
    headerRow.createCell(2).setCellValue("Total Quantity")
    headerRow.createCell(3).setCellValue("Sold Quantity")
    headerRow.createCell(4).setCellValue("Remaining Quantity")
    headerRow.createCell(5).setCellValue("Container No")
    headerRow.createCell(6).setCellValue("Remark")

    // Fill data rows
    for ((index, product) in products.withIndex()) {
        val dataRow: Row = sheet.createRow(index + 1)
        dataRow.createCell(0).setCellValue(product.id)
        dataRow.createCell(1).setCellValue(product.isPaymentComplete.toString())
        dataRow.createCell(2).setCellValue(product.quantity.toDouble())
        dataRow.createCell(3).setCellValue(product.soldQuantity.toDouble())
        dataRow.createCell(4).setCellValue(product.remainingQuantity.toDouble())
        dataRow.createCell(5).setCellValue(product.containerNo)
        dataRow.createCell(6).setCellValue(product.remark)
    }

    // Write the workbook content to a file
    try {
        FileOutputStream(outputFile).use { fileOut ->
            workbook.write(fileOut)
            return "Successfully exported"
        }
    } catch (e: IOException) {
        e.printStackTrace()
        return "Failed to export"
    } finally {
        try {
            workbook.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}


// Notification ------------------------------------------------------------------------------

// Create Notification Channel
private const val CHANNEL_ID = "export_channel"
fun createNotificationChannel(context: Context) {
    val channel = NotificationChannel(
        CHANNEL_ID,
        "Export Channel",
        NotificationManager.IMPORTANCE_DEFAULT
    )
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    notificationManager.createNotificationChannel(channel)
}

// Create Notification

// Not handle click action
private const val NOTIFICATION_ID = 1
private fun createNotification(context: Context, title: String, content: String) {
    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.logo)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

// handle click action
fun createNotificationWithAction(context: Context, title: String, content: String, filePath: String) {

    // filePath to fileUri
    val fileUri = FileProvider.getUriForFile(
        context,
        context.packageName + ".fileprovider",
        File(filePath)
    )

    val notificationIntent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    val pendingIntent = PendingIntent.getActivity(
        context,
        0,
        notificationIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setContentTitle(title)
        .setContentText(content)
        .setSmallIcon(R.drawable.logo)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // Automatically remove the notification when clicked

    val notificationManager = NotificationManagerCompat.from(context)
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
        return
    }
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
}

// ------------------------------------------------------------------------------------------------