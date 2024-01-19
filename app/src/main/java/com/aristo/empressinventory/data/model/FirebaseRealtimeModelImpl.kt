package com.aristo.empressinventory.data.model

import android.net.Uri
import com.aristo.admin.data.model.AbstractBaseModel
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.data.vos.ProductVO

object FirebaseRealtimeModelImpl : AbstractBaseModel(), FirebaseRealtimeModel {

    override fun saveCustomer(
        productId: String,
        customer: CustomerVO,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.saveCustomer(productId, customer, completionHandler)
    }

    override fun addProduct(product: ProductVO, completionHandler: (Boolean, String?) -> Unit) {
        mFirebaseRealtimeApi.addProduct(product, completionHandler)
    }

    override fun updateProduct(
        product: ProductVO,
        type: String,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.updateProduct(product, type, completionHandler)
    }

    override fun getProductsData(completionHandler: (Boolean, ArrayList<ProductVO>?) -> Unit) {
        mFirebaseRealtimeApi.getProductsData(completionHandler)
    }

    override fun getCustomerList(
        productId: String,
        completionHandler: (Boolean, ArrayList<CustomerVO>?) -> Unit
    ) {
        mFirebaseRealtimeApi.getCustomerList(productId, completionHandler)
    }

    override fun uploadImage(
        imageUri: Uri,
        completionHandler: (Boolean, String?) -> Unit
    ) {
        mFirebaseRealtimeApi.uploadImage(imageUri, completionHandler)
    }

}