package com.aristo.empressinventory.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.view.productSell.SellingProductActivity
import com.aristo.empressinventory.databinding.ActivityHomeBinding
import com.aristo.empressinventory.utils.showToastMessage
import com.aristo.empressinventory.view.productAdd.AddProductActivity
import com.aristo.empressinventory.view.productReport.ContainerReportActivity
import com.aristo.empressinventory.view.productReport.InventoryReportActivity

class HomeActivity : AbstractBaseActivity<ActivityHomeBinding>() {

    private var productList = arrayListOf<ProductVO>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.progressBar.visibility = View.VISIBLE
        mFirebaseRealtimeModel.getProductsData { isSuccess, data ->
            if (isSuccess) {
                if (!data.isNullOrEmpty()) {
                    productList = data
                }
            }
            binding.progressBar.visibility = View.GONE
        }

        binding.cardInventoryReport.setOnClickListener {
//            startActivity(Intent(this, ContainerReportActivity::class.java))

            val intent = Intent(this, InventoryReportActivity::class.java)
            intent.putExtra("productList", productList)
            startActivity(intent)
        }

        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        binding.cardSellingProduct.setOnClickListener {
            startActivity(Intent(this, SellingProductActivity::class.java))
        }
    }
}