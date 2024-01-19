package com.aristo.empressinventory.view.productSell

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.aristo.empressinventory.adapters.CustomAutoCompleteAdapter
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.data.vos.ProductCode
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.view.productAdd.AddProductActivity
import com.aristo.empressinventory.databinding.ActivitySellingProductBinding
import com.aristo.empressinventory.delegate.FilteredItemListener
import com.aristo.empressinventory.utils.showToastMessage
import com.aristo.empressinventory.view.AbstractBaseActivity
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SellingProductActivity : AbstractBaseActivity<ActivitySellingProductBinding>(), FilteredItemListener {

    private lateinit var customerData : CustomerVO
    private var productCodeList : ArrayList<ProductCode> = arrayListOf()
    private var productList: ArrayList<ProductVO> = arrayListOf()
    private var productId = ""

    private val isFinishedBothAsyncObservable = MutableLiveData(arrayListOf(false,false))

    companion object {
        private const val MIN_CHARACTERS_REQUIRED = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySellingProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupDatePicker()
        setUpListeners()
        fetchProductCodeData()
        observeLivedataNetworkCall()  // customer save ရင် quantity ကိုပါ update လုပ်ရမှာမို့ network call ခေါ်လို့
    }

    // Date Picker Setup
    private fun setupDatePicker() {

        // For Current Date
        val currentDate = SimpleDateFormat("dd/M/yyyy", Locale.getDefault()).format(Date())
        binding.tvDate.text = currentDate

        // Show Date Picker Dialog and can select date
        binding.tvDate.setOnClickListener {

            val datePickerFragment = DatePickerFragment()
            datePickerFragment.show(supportFragmentManager, "DatePicker")
        }
    }
    
    private fun setUpListeners() {

        binding.btnSave.setOnClickListener{

            if (binding.etProductCode.text.isNotEmpty()) {
                productId = binding.etProductCode.text.toString()
            }

            if (validateFields()) {
                binding.progressBar.visibility = View.VISIBLE

                addCustomerToFirebase()
                updateProductQuantity()
            }
        }

        binding.addNewProductLayout.setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivity(intent)
        }

        binding.ibBack.setOnClickListener {
            finish()
        }
    }

    private fun addCustomerToFirebase() {

        mFirebaseRealtimeModel.saveCustomer(productId, customerData) { isSuccess, message ->
            if (isSuccess){
                showToastMessage(applicationContext, "Successfully saved")
            } else {
                showToastMessage(applicationContext, message)
            }
            updateObservable()
        }
    }

    private fun updateProductQuantity() {
        // Update quantity to firebase
        productList.forEach {
            if (it.id == productId) {
                if (it.remainingQuantity <= customerData.quantity) {
                    it.remainingQuantity = 0
                } else {
                    it.remainingQuantity -= customerData.quantity
                }
                it.soldQuantity += customerData.quantity

                mFirebaseRealtimeModel.updateProduct(it, "qty") { isSuccess, message ->
                    if (!isSuccess) {
                        showToastMessage(applicationContext, message)
                    }
                    updateObservable()
                }
            }
        }
    }

    private fun updateObservable() {

        val updatedList = isFinishedBothAsyncObservable.value?.toMutableList() ?: mutableListOf()

        if (updatedList[0]) {
            updatedList[1] = true
        } else {
            updatedList[0] = true
        }

        isFinishedBothAsyncObservable.value = ArrayList(updatedList)
    }

    private fun observeLivedataNetworkCall() {
        isFinishedBothAsyncObservable.observe(this) {
            if (it[0] && it[1]) {
                binding.progressBar.visibility = View.GONE
                finish()
            }
        }
    }

    // Check All Text Fields
    private fun validateFields(): Boolean {
        val customerName = binding.etCustomerName.text.toString()
        val productCode = binding.etProductCode.text.toString()
        val priceText = binding.etPrice.text.toString()
        val quantityText = binding.etQuantity.text.toString()

        when {
            customerName.isEmpty() -> {
                showToastMessage(applicationContext, "Please enter customer name")
                return false
            }
            productCode.isEmpty() -> {
                showToastMessage(applicationContext, "Please enter product code")
                return false
            }
            priceText.isEmpty() -> {
                showToastMessage(applicationContext, "Please enter price")
                return false
            }
            quantityText.isEmpty() -> {
                showToastMessage(applicationContext, "Please enter quantity")
                return false
            }
            else -> {
                // All fields are non-empty, proceed with further processing if needed
                val price = priceText.toInt()
                val quantity = quantityText.toInt()

                customerData = CustomerVO(
                    date = binding.tvDate.text.toString(),
                    customerName = customerName,
                    productCode = productCode,
                    price = price,
                    quantity = quantity
                )
                return true
            }
        }
    }

    private fun setAutoComplete(){

        val adapter = CustomAutoCompleteAdapter(this, productCodeList, this)
        binding.etProductCode.setAdapter(adapter)
        binding.etProductCode.threshold = 0

        binding.etProductCode.setOnItemClickListener { _, _, position, _ ->

            binding.addNewProductLayout.visibility = View.GONE

            val selectedItem = adapter.getItem(position) as ProductCode
            val selectedText = selectedItem.id
            val selectedImageResId = selectedItem.imageURL
            val selectedRemaining = selectedItem.remaining

            binding.etProductCode.setText(selectedText)
            binding.tvRemaining.visibility = View.VISIBLE
            binding.tvRemaining.text = "Remaining : $selectedRemaining pcs"

            Glide.with(this)
                .load(selectedImageResId)
                .placeholder(com.aristo.empressinventory.R.drawable.placeholder)
                .into(binding.ivAddPhoto)

        }

        binding.etProductCode.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Check the length of the text entered
                val typedTextLength = s?.length ?: 0

                // You can perform actions based on the length of the typed text
                if (typedTextLength == MIN_CHARACTERS_REQUIRED) {
                    // Do something when the typed text has reached a certain length
                    binding.tvRemaining.visibility = View.GONE
                    binding.addNewProductLayout.visibility = View.GONE
                } else {
                    // Do something else when the typed text is less than the required length
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    // Retrieve product list from firebase realtime for autocomplete
    private fun fetchProductCodeData(){

        mFirebaseRealtimeModel.getProductsData { isSuccess, data ->
            if (isSuccess) {
                if (data != null) {
                    for (product in data) {
                        val productCode = ProductCode(
                            id = product.id,
                            imageURL = product.imageURL,
                            remaining = product.remainingQuantity
                        )
                        productList.add(product)
                        productCodeList.add(productCode)
                    }
                    setAutoComplete()
                }
                else{
                   showToastMessage(applicationContext, "No data found")
                }
            }
            else {
                showToastMessage(applicationContext, "Can't retrieve data")
            }
        }
    }

    override fun onNoMatchingItemFound() {
        binding.addNewProductLayout.visibility = View.VISIBLE
        binding.tvRemaining.visibility = View.GONE
    }
}