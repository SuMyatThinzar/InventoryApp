package com.aristo.empressinventory.network

import android.net.Uri
import android.util.Log
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.data.vos.ProductVO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

object FirebaseRealtimeApiImpl : FirebaseRealtimeApi {

    private val database = FirebaseDatabase.getInstance()
    private val productsRef: DatabaseReference = database.getReference("Products")

    override fun saveCustomer(productId : String, customer : CustomerVO, completionHandler: (Boolean, String?) -> Unit){

        val customerId = productsRef.push().key

        if (customerId != null) {
            customer.id = customerId
        }
        // Store sold lists data in Firebase Realtime Database
        productsRef.child(productId).child("price_lists").child(customer.price.toString()).child(customer.id).setValue(customer)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    completionHandler(true, null) // Success, pass true and no error message

                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error occurred"
                    completionHandler(false, errorMessage) // Failure, pass false and the error message
                }
            }
    }

    override fun addProduct(product: ProductVO, completionHandler: (Boolean, String?) -> Unit) {
        productsRef.child(product.id).setValue(product).addOnCompleteListener {
            if (it.isSuccessful) {
                completionHandler(true, "Successful")
            } else {
                completionHandler(false, it.exception?.message)
            }
        }
    }

    override fun updateProduct(product: ProductVO, type: String, completionHandler: (Boolean, String?) -> Unit) {
        when (type) {
            "payment" -> {
                productsRef.child(product.id).updateChildren(mapOf("paymentComplete" to product.isPaymentComplete)).addOnCompleteListener {
                    if (it.isSuccessful) { completionHandler(true, "Successful") }
                    else { completionHandler(false, it.exception?.message) }
                }
            }
            "remark" -> {
                productsRef.child(product.id).updateChildren(mapOf("remark" to product.remark)).addOnCompleteListener {
                    if (it.isSuccessful) { completionHandler(true, "Successful") }
                    else { completionHandler(false, it.exception?.message) }
                }
            }
            "qty" -> {
                productsRef.child(product.id).updateChildren(mapOf("soldQuantity" to product.soldQuantity, "remainingQuantity" to product.remainingQuantity)).addOnCompleteListener {
                    if (it.isSuccessful) { completionHandler(true, "Successful") }
                    else { completionHandler(false, it.exception?.message) }
                }
            }
        }
    }

    override fun getProductsData(completionHandler: (Boolean, ArrayList<ProductVO>?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val categoriesRef: DatabaseReference = database.getReference("Products")

        categoriesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList: ArrayList<ProductVO> = ArrayList()

                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(ProductVO::class.java)
                    product?.let {
                        // Extract isPaymentComplete directly from the ProductModel object
                        val isPaid = it.isPaymentComplete
                        Log.d("isPaid", "isPaid: $isPaid")
                        productList.add(it)
                    }
                }

                completionHandler(true, productList)
            }

            override fun onCancelled(error: DatabaseError) {
                completionHandler(false, null)
            }
        })
    }

    override fun getCustomerList(productId: String, completionHandler: (Boolean, ArrayList<CustomerVO>?) -> Unit) {
        database.getReference("Products").child(productId).child("price_lists")
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {
                    val customerList = ArrayList<CustomerVO>()
                    for (priceSnapshot in snapshot.children) {
                        for (customerSnapshot in priceSnapshot.children) {
                            val customer = customerSnapshot.getValue(CustomerVO::class.java)
                            customer?.let {
                                customerList.add(it)
                            }
                        }
                    }
                    completionHandler(true, customerList)
                }

                override fun onCancelled(error: DatabaseError) {
                    completionHandler(false, null)
                }
            })

    }

    override fun uploadImage(imageUri: Uri, completionHandler: (Boolean, String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        val uploadTask = imageRef.putFile(imageUri)

        uploadTask.addOnSuccessListener { _ ->
            imageRef.downloadUrl.addOnSuccessListener {
                completionHandler(true, it.toString())
            }
        }.addOnFailureListener { exception ->
            completionHandler(false, exception.message)
        }
    }
}