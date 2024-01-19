package com.aristo.empressinventory.data.vos

import java.io.Serializable

data class ProductVO(
    var id: String = "",
    var imageURL: String = "",
    var isPaymentComplete: Boolean = false,
    var quantity: Int = 0,
    var soldQuantity: Int = 0,
    var remainingQuantity: Int = 0,
    var containerNo: String? = "",
    var timestamp: Long = System.currentTimeMillis(),
    var remark: String = "",
    ) : Serializable