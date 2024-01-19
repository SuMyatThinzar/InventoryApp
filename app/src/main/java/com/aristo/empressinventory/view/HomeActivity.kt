package com.aristo.empressinventory.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aristo.empressinventory.view.productSell.SellingProductActivity
import com.aristo.empressinventory.databinding.ActivityHomeBinding
import com.aristo.empressinventory.view.productAdd.AddProductActivity
import com.aristo.empressinventory.view.productReport.ContainerReportActivity

class HomeActivity : AbstractBaseActivity<ActivityHomeBinding>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.cardInventoryReport.setOnClickListener {
            startActivity(Intent(this, ContainerReportActivity::class.java))
        }

        binding.cardAddProduct.setOnClickListener {
            startActivity(Intent(this, AddProductActivity::class.java))
        }

        binding.cardSellingProduct.setOnClickListener {
            startActivity(Intent(this, SellingProductActivity::class.java))
        }
    }
}