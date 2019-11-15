package com.ireny.warrantyreport.ui.report.company

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Company
import com.ireny.warrantyreport.ui.listeners.SelectedListener

class CompanyListAdapter internal constructor(
    private val context: Context,
    private val listener: SelectedListener<Company>
) : RecyclerView.Adapter<CompanyListAdapter.CompanyViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var data = emptyList<Company>()
    private var selectioned:Int? = null

    fun refreshSelection(optionSelectioned: Int?) {
        selectioned = optionSelectioned
        notifyDataSetChanged()
    }

    internal fun setData(items: List<Company>) {
        this.data = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        val itemView = inflater.inflate(R.layout.report_company_item, parent, false)
        return CompanyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        val current = data[position]

        holder.itemView.setOnClickListener{
            listener.onSelected(current)
        }
        holder.description.text = current.description
        holder.itemView.background = context.getDrawable(R.drawable.item_back)
        selectioned?.let {
            if(current.id == it){
                holder.itemView.background = context.getDrawable(R.drawable.item_back_selected)
            }
        }
    }

    override fun getItemCount() = data.size

    inner class CompanyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.text_description)
    }
}