package com.aristo.empressinventory.view.productAdd

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.databinding.ActivityAddProductBinding
import com.aristo.empressinventory.utils.showToastMessage
import com.aristo.empressinventory.view.AbstractBaseActivity

class AddProductActivity : AbstractBaseActivity<ActivityAddProductBinding>() {

    private var selectedImageUri: Uri? = null
    private var productCode: String? = null
    private var containerNo: String? = null
    private var quantity: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.btnSave.setOnClickListener {
            validateFieldsAndRequestNetworkCall()
        }

        binding.ibBack.setOnClickListener {
            finish()
        }

        val galleryImage = registerForActivityResult(
            ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedImageUri = uri
                binding.ivAddPhoto.setImageURI(uri)
            }
        }

        binding.ivAddPhoto.setOnClickListener {
            galleryImage.launch("image/*")
        }
    }

    private fun validateFieldsAndRequestNetworkCall() {
        productCode = binding.etProductCode.text.toString()
        containerNo = binding.etContainerNo.text.toString()
        quantity = binding.etQuantity.text.toString()

        if (binding.etProductCode.text.isEmpty()) {
            showToastMessage(applicationContext, "Please fill Product Name")
            return
        } else if (binding.etQuantity.text.isEmpty()) {
            showToastMessage(applicationContext, "Please fill Quantity")
            return
        } else if (binding.etContainerNo.text.isEmpty()) {
            showToastMessage(applicationContext, "Please fill Container Name")
            return
        } else if (selectedImageUri == null) {
            showToastMessage(applicationContext, "Please add Product Image")
            return
        }

        binding.progressBar.visibility = View.VISIBLE
        requestNetworkCall()
    }

    private fun requestNetworkCall() {
        mFirebaseRealtimeModel.uploadImage(selectedImageUri!!) { isSuccessImage, url ->
            if (isSuccessImage && url != null) {

                val product = ProductVO(
                    id = productCode!!,
                    imageURL = url,
                    quantity = quantity!!.toInt(),
                    remainingQuantity = quantity!!.toInt(),
                    containerNo = containerNo
                )

                mFirebaseRealtimeModel.addProduct(product) { isSuccess, message ->
                    if (isSuccess) {
                        showToastMessage(applicationContext, message)
                        finish()
                    }
                }
            }
        }
    }

}