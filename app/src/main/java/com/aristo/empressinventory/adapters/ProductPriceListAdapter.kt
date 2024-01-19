package com.aristo.empressinventory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.utils.addCommasToNumber
import com.aristo.empressinventory.databinding.ViewHolderProductPriceBinding

class ProductPriceListAdapter : RecyclerView.Adapter<ProductPriceListAdapter.ProductPriceListViewHolder>() {

    private var priceList: List<Int> = listOf()
    private var customerList: List<CustomerVO> = listOf()

    class ProductPriceListViewHolder(private var binding: ViewHolderProductPriceBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(price: Int, cusList: ArrayList<CustomerVO>) {

            val customerListAdapter = CustomerListAdapter()
            binding.rvCustomer.adapter = customerListAdapter
            binding.rvCustomer.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)

            if (cusList.isNotEmpty()) {
                customerListAdapter.setNewData(cusList)
            }

            itemView.setOnClickListener {
                if (binding.cvExpandedVoucher.visibility == View.VISIBLE) {
                    binding.cvExpandedVoucher.visibility = View.GONE
                    binding.cvCollapsedVoucher.visibility = View.VISIBLE
                } else if (binding.cvCollapsedVoucher.visibility == View.VISIBLE) {
                    binding.cvExpandedVoucher.visibility = View.VISIBLE
                    binding.cvCollapsedVoucher.visibility = View.GONE
                }
            }

            var totalQty = 0
            cusList.forEach {
                totalQty += it.quantity
            }
            binding.tvTotalQtyCollapsed.text = "(Sold Qty : ${addCommasToNumber(totalQty)}  pcs)"
            binding.tvTotalQtyExpanded.text = "(Sold Qty : ${addCommasToNumber(totalQty)}  pcs)"
            binding.tvPriceCollapsed.text = "${addCommasToNumber(price)}  Ks"
            binding.tvPriceExpanded.text = "${addCommasToNumber(price)}  Ks"

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductPriceListViewHolder {
        val binding = ViewHolderProductPriceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductPriceListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return priceList.size
    }

    override fun onBindViewHolder(holder: ProductPriceListViewHolder, position: Int) {
        val filteredCusList = arrayListOf<CustomerVO>()

        if (priceList.isNotEmpty()) {
            customerList.forEach {
                if (it.price == priceList[position]) {
                    filteredCusList.add(it)
                }
            }
            holder.bindData(priceList[position], filteredCusList)
        }
    }

    fun setNewData(prices: List<Int>, customers: ArrayList<CustomerVO>) {
        priceList = prices
        customerList = customers
        notifyDataSetChanged()
    }

}
