package com.aristo.empressinventory.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.R
import com.aristo.empressinventory.view.productReport.ReportDetailActivity
import com.aristo.empressinventory.databinding.InventoryReportListItemBinding
import com.bumptech.glide.Glide
import java.util.Locale

class InventoryReportRecyclerViewAdapter :
    RecyclerView.Adapter<InventoryReportRecyclerViewAdapter.InventoryReportRecyclerViewHolder>(),
    Filterable {

    private var originalList: ArrayList<ProductVO> = arrayListOf()
    private var filteredList: ArrayList<ProductVO> = originalList

    class InventoryReportRecyclerViewHolder(private val binding: InventoryReportListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindData(product: ProductVO) {
            binding.tvProductCode.text = product.id
            binding.tvRemaining.text = "Remaing Quantity: ${product.remainingQuantity}"

            val context = itemView.context

            Glide.with(context)
                .load(product.imageURL)
                .placeholder(R.drawable.placeholder)
                .into(binding.ivProduct)

            if (product.isPaymentComplete) {
                binding.paymentView.visibility = View.VISIBLE
            } else {
                binding.paymentView.visibility = View.GONE
            }

            itemView.setOnClickListener {
                val intent = Intent(context, ReportDetailActivity::class.java)
                intent.putExtra("Product", product)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InventoryReportRecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = InventoryReportListItemBinding.inflate(inflater, parent, false)
        return InventoryReportRecyclerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: InventoryReportRecyclerViewHolder, position: Int) {
        holder.bindData(filteredList[position])
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val queryString = constraint?.toString()?.toLowerCase(Locale.getDefault())

                filteredList = if (queryString.isNullOrBlank()) {
                    originalList
                } else {
                    val tempList = ArrayList<ProductVO>()
                    for (item in originalList) {
                        if (item.id.toLowerCase(Locale.getDefault()).contains(queryString)) {
                            tempList.add(item)
                        }
                    }
                    tempList
                }

                filterResults.values = filteredList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredList = results?.values as ArrayList<ProductVO>
                notifyDataSetChanged()
            }
        }
    }

    fun updateData(allProductList: ArrayList<ProductVO>) {
        originalList.clear()
        originalList.addAll(allProductList)
        notifyDataSetChanged()
    }
}
