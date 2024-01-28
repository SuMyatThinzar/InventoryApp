package com.aristo.empressinventory.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.aristo.empressinventory.R
import com.aristo.empressinventory.data.vos.ProductCode
import com.aristo.empressinventory.delegate.FilteredItemListener
import com.bumptech.glide.Glide

class CustomAutoCompleteAdapter(
    private val context: Context,
    private val items: List<ProductCode>,
    private val filteredItemListener: FilteredItemListener
) : ArrayAdapter<ProductCode>(context, 0, items), Filterable {

    private var filteredItems: List<ProductCode> = items

    override fun getCount(): Int {
        return filteredItems.size
    }

    override fun getItem(position: Int): ProductCode {
        return filteredItems[position]
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filterResults = FilterResults()
                val queryString = constraint?.toString()?.toLowerCase()

                // Perform your filtering logic here
                if (queryString.isNullOrBlank()) {
                    filteredItems = items

                } else {
                    val filteredList = items.filter {
                        it.id.toLowerCase().contains(queryString)
                    }
                    filteredItems = filteredList
                }

                filterResults.values = filteredItems
                filterResults.count = filteredItems.size
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredItems = results?.values as? List<ProductCode> ?: emptyList()

                if (filteredItems.isEmpty() && !constraint.isNullOrBlank()) {
                    // Handle the case when typed words are not in the list
                    filteredItemListener.onNoMatchingItemFound()
                }

                notifyDataSetChanged()
            }
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val holder: ViewHolder

        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.view_item_autocomplete, parent, false)
            holder = ViewHolder()
            holder.textView = view.findViewById(R.id.text_view)
            holder.imageView = view.findViewById(R.id.image_view)
            view.tag = holder
        } else {
            holder = view.tag as ViewHolder
        }

        val item = filteredItems[position]
        holder.textView?.text = item.id
        holder.imageView?.let {
            Glide.with(context)
                .load(item.imageURL)
                .placeholder(R.drawable.placeholder)
                .into(it)
        }

        return view!!
    }

    private class ViewHolder {
        var textView: TextView? = null
        var imageView: ImageView? = null
    }
}