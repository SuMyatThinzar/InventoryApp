package com.aristo.empressinventory.view.productReport

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.R
import com.aristo.empressinventory.utils.addCommasToNumber
import com.aristo.empressinventory.adapters.ProductPriceListAdapter
import com.aristo.empressinventory.databinding.ActivityReportDetailBinding
import com.aristo.empressinventory.databinding.RemarkDialogBinding
import com.aristo.empressinventory.view.AbstractBaseActivity
import com.bumptech.glide.Glide

class ReportDetailActivity : AbstractBaseActivity<ActivityReportDetailBinding>() {

    private val productPriceListAdapter by lazy { ProductPriceListAdapter() }
    private var product: ProductVO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product = intent.getSerializableExtra("Product") as ProductVO

        setUpAdapters()
        setUpListeners()
        setUpProductData()
    }

    private fun setUpProductData() {

        product?.let {
            binding.cbPaymentComplete.isChecked = it.isPaymentComplete
            binding.tvTitle.text = it.id

            binding.tvTotalQty.text = addCommasToNumber(it.quantity)
            binding.tvSoldQty.text = addCommasToNumber(it.soldQuantity)
            binding.tvRemainingQty.text = addCommasToNumber(it.remainingQuantity)

            Glide.with(this)
                .load(it.imageURL)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProduct)

            if (it.remark.isNotEmpty()) {
                binding.llRemark.visibility = View.VISIBLE
                binding.etRemark.text = it.remark
            }

            mFirebaseRealtimeModel.getCustomerList(it.id) { isSuccess, customerList ->
                if (isSuccess && customerList != null) {
                    val priceList = mutableSetOf<Int>()
                    customerList.forEach { customer ->
                        priceList.add(customer.price)
                    }
                    productPriceListAdapter.setNewData(priceList.toList(), customerList)
                }
            }
        }
    }

    private fun setUpAdapters() {
        binding.rvProductPrice.adapter = productPriceListAdapter
        binding.rvProductPrice.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun setUpListeners() {
        binding.ibBack.setOnClickListener {
            finish()
        }

        binding.btnAddRemark.setOnClickListener {
            showRemarkDialog()
        }

        binding.cbPaymentComplete.setOnCheckedChangeListener { button, isChecked ->
            binding.progressBar.visibility = View.VISIBLE
            binding.cbPaymentComplete.isClickable = false

            product?.let {
                it.isPaymentComplete = isChecked
                mFirebaseRealtimeModel.updateProduct(it, "payment") { _, _ ->
                    binding.progressBar.visibility = View.GONE
                    binding.cbPaymentComplete.isClickable = true
                }
            }
        }
    }

    private fun showRemarkDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = RemarkDialogBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        builder.setCancelable(false)

        val dialog = builder.create()

        product?.let {
            dialogBinding.etRemark.setText(it.remark)
        }

        dialogBinding.btnConfirm.setOnClickListener {
            product?.let {
                it.remark = dialogBinding.etRemark.text.toString()
                mFirebaseRealtimeModel.updateProduct(it, "remark") { _, _ ->
                        if (dialogBinding.etRemark.text.isNotEmpty()) {
                            binding.llRemark.visibility = View.VISIBLE
                            binding.etRemark.text = it.remark
                        } else {
                            binding.llRemark.visibility = View.GONE
                        }
                        dialog.cancel()
                    }
            }
        }

        dialogBinding.btnCancel.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }

}