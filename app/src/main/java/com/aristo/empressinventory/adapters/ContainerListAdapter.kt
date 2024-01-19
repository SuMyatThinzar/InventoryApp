package com.aristo.empressinventory.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.empressinventory.data.vos.ProductVO
import com.aristo.empressinventory.view.productReport.InventoryReportActivity
import com.aristo.empressinventory.databinding.InventoryReportListItemBinding

class ContainerListAdapter : RecyclerView.Adapter<ContainerListAdapter.ContainerListViewHolder>() {

    private var containerList: List<String> = listOf()
    private var productList: ArrayList<ProductVO> = arrayListOf()

    class ContainerListViewHolder(private var binding: InventoryReportListItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bindData(price: String, cusList: ArrayList<ProductVO>) {

            binding.tvProductCode.text = "  Container No. $price"
            binding.ivProduct.visibility = View.GONE
            binding.tvRemaining.visibility = View.GONE

            val context = itemView.context

            itemView.setOnClickListener {
                val intent = Intent(context, InventoryReportActivity::class.java)
                intent.putExtra("productList", cusList)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerListViewHolder {
        val binding = InventoryReportListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContainerListViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return containerList.size
    }

    override fun onBindViewHolder(holder: ContainerListViewHolder, position: Int) {
        val filteredCusList = arrayListOf<ProductVO>()

        if (containerList.isNotEmpty()) {
            productList.forEach {
                if (it.containerNo == containerList[position]) {
                    filteredCusList.add(it)
                }
            }
            holder.bindData(containerList[position], filteredCusList)
        }
    }

    fun setNewData(containers: List<String>, products: ArrayList<ProductVO>) {
        containerList = containers
        productList = products
        notifyDataSetChanged()
    }

}
