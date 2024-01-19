package com.aristo.empressinventory.view.productReport

import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.R
import com.aristo.empressinventory.adapters.InventoryReportRecyclerViewAdapter
import com.aristo.empressinventory.databinding.ActivityInventoryReportBinding
import com.aristo.empressinventory.utils.showToastMessage
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
    }

    private fun requestNetworkCall() {
        // update product list data from intent with realtime data
        mFirebaseRealtimeModel.getProductsData { isSuccess, data ->
            if (isSuccess) {
                if (data != null && oldProductList != null) {
                    productList.clear()
                    data.forEach {
                        oldProductList!!.forEach { product->
                            if (it.id == product.id) {
                                productList.add(it)
                            }
                        }
                    }
                    inventoryReportAdapter.updateData(productList)
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

