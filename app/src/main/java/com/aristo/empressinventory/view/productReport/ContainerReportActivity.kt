package com.aristo.empressinventory.view.productReport

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.empressinventory.adapters.ContainerListAdapter
import com.aristo.empressinventory.databinding.ActivityContainerReportBinding
import com.aristo.empressinventory.utils.showToastMessage
import com.aristo.empressinventory.view.AbstractBaseActivity

class ContainerReportActivity : AbstractBaseActivity<ActivityContainerReportBinding>() {

    private val containerListAdapter by lazy { ContainerListAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContainerReportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setUpListeners()
        requestNetworkCall()
    }

    private fun setUpListeners() {
        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun requestNetworkCall() {

        mFirebaseRealtimeModel.getProductsData { isSuccess, data ->
            if (isSuccess) {
                if (!data.isNullOrEmpty()) {

                    val containerList = mutableSetOf<String>()
                    data.forEach {
                        it.containerNo?.let { it1 ->
                            containerList.add(it1)
                        }
                    }
                    containerListAdapter.setNewData(containerList.toList(), data)
                    binding.progressBar.visibility = View.GONE
                }
            } else {
                showToastMessage(applicationContext, "Can't retrieve data")
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvInventoryReport.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvInventoryReport.adapter = containerListAdapter
    }

}