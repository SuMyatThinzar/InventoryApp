package com.aristo.empressinventory.network

import android.net.Uri
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.data.vos.ProductVO

interface FirebaseRealtimeApi {

    fun saveCustomer(
        productId : String,
        customer : CustomerVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun addProduct(
        product: ProductVO,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun updateProduct(
        product: ProductVO,
        type: String,
        completionHandler: (Boolean, String?) -> Unit
    )

    fun getProductsData(
        completionHandler: (Boolean, ArrayList<ProductVO>?) -> Unit
    )

    fun getCustomerList(
        productId: String,
        completionHandler: (Boolean, ArrayList<CustomerVO>?) -> Unit
    )

    fun uploadImage(
        imageUri: Uri,
        completionHandler: (Boolean, String?) -> Unit
    )
}