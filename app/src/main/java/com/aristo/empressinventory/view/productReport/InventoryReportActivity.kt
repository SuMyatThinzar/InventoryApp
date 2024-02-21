package com.aristo.empressinventory.view.productReport

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.R
import com.aristo.empressinventory.adapters.InventoryReportRecyclerViewAdapter
import com.aristo.empressinventory.databinding.ActivityInventoryReportBinding
import com.aristo.empressinventory.utils.*
import com.aristo.empressinventory.view.AbstractBaseActivity

class InventoryReportActivity : AbstractBaseActivity<ActivityInventoryReportBinding>() {

    private val inventoryReportAdapter by lazy { InventoryReportRecyclerViewAdapter() }
    private val inventoryLayoutManager by lazy { LinearLayoutManager(this) }
    private var oldProductList: ArrayList<ProductVO>? = arrayListOf()
    private var productList: ArrayList<ProductVO> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInventoryReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        oldProductList = intent.getSerializableExtra("productList") as? ArrayList<ProductVO>

        setupRecyclerView()
        setUpListeners()
        requestNetworkCall()
    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }
        binding.btnExportExcel.setOnClickListener {

            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                // Request the permission if not granted
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    100
                )
            } else {
                return@setOnClickListener
            }

            val uniqueId = System.currentTimeMillis()

//            val outputFile = File(getExternalFilesDir(null), "$uniqueId product list.xlsx")
//            Log.d("adfasdfsfd", outputFile.absolutePath)

            val message = exportToExcel(productList, applicationContext, uniqueId)
            showToastMessage(applicationContext, message)

            val filePath = getFilePath(applicationContext, uniqueId.toString())

            createNotificationChannel(applicationContext)
            createNotificationWithAction(applicationContext, "Export Complete", "The product list export process has finished.", filePath)
//            createNotification(this, "Export Complete", "")
        }
    }

    private fun requestNetworkCall() {
        // update product list data from intent with realtime data
        mFirebaseRealtimeModel.getProductsData { isSuccess, data ->
            if (isSuccess) {
                if (data != null) {
                    inventoryReportAdapter.updateData(data)
                }
            } else {
                showToastMessage(applicationContext, "Can't retrieve data")
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvInventoryReport.layoutManager = inventoryLayoutManager
        binding.rvInventoryReport.adapter = inventoryReportAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_search, menu)
        val searchItem = menu?.findItem(R.id.search)
        val searchView = searchItem?.actionView as SearchView

        // Customize the searchView as needed (optional)
        searchView.queryHint = "Search..."

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle search query submission
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                inventoryReportAdapter.filter.filter(newText)
                return true
            }
        })

        return true
    }
}

