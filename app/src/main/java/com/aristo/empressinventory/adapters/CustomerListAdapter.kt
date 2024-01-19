package com.aristo.empressinventory.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aristo.empressinventory.data.vos.CustomerVO
import com.aristo.empressinventory.utils.addCommasToNumber
import com.aristo.empressinventory.databinding.ViewHolderCustomerBinding

class CustomerListAdapter : RecyclerView.Adapter<CustomerListAdapter.CustomerListViewHolder>() {

    private var customerList = arrayListOf<CustomerVO>()

    class CustomerListViewHolder(private var binding: ViewHolderCustomerBinding, private var context: Context) : RecyclerView.ViewHolder(binding.root){

        fun bind(customer: CustomerVO) {
            binding.tvCustomerName.text = customer.customerName
            binding.tvDate.text = ":    ${customer.date}"
            binding.tvPrice.text = ":    ${addCommasToNumber(customer.price)}  Ks"
            binding.tvQty.text = ":    ${addCommasToNumber(customer.quantity)}  pcs"
            binding.tvTotalAmount.text = ":    ${addCommasToNumber(customer.totalAmount)}  Ks"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerListViewHolder {
        val binding = ViewHolderCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CustomerListViewHolder(binding,parent.context)
    }

    override fun getItemCount(): Int {
        return customerList.size
    }

    override fun onBindViewHolder(holder: CustomerListViewHolder, position: Int) {
        holder.bind(customerList[position])
    }

    fun setNewData(customers: ArrayList<CustomerVO>) {
        customerList = customers
        notifyDataSetChanged()
    }

}
