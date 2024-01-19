package com.aristo.empressinventory.data.vos

import java.io.Serializable

data class CustomerVO(
    var id : String = "",
    var date : String = "",
    var customerName : String = "",
    var productCode : String = "",
    var quantity : Int = 0,
    var price : Int = 0,
    var totalAmount : Int = quantity * price
) : Serializable {
}

data class ProductCode(
    var id : String = "",
    var imageURL : String,
    var remaining : Int
) : Serializable {
}
