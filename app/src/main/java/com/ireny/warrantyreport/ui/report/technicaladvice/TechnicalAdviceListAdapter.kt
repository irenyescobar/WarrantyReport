package com.ireny.warrantyreport.ui.report.technicaladvice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.ReportTechnicalAdvice
import com.ireny.warrantyreport.ui.listeners.CheckedChangedListener

class TechnicalAdviceListAdapter internal constructor(
    val context: Context,
    private val checkedChangeListener: CheckedChangedListener<ReportTechnicalAdvice>
):
    RecyclerView.Adapter<TechnicalAdviceListAdapter.TechnicalAdviceViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var data = emptyList<ReportTechnicalAdvice>()

    internal fun setData(items: List<ReportTechnicalAdvice>) {
        this.data = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TechnicalAdviceViewHolder {
        val itemView = inflater.inflate(R.layout.report_technical_advice_item, parent, false)
        return TechnicalAdviceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TechnicalAdviceViewHolder, position: Int) {
        val current = data[position]

        holder.itemView.tag = current
        holder.itemView.setOnClickListener{
            val item = it.tag as ReportTechnicalAdvice
            item.selectioned = !item.selectioned
            checkedChangeListener.onCheckedChanged(item)
        }

        holder.description.text = current.description

        if(current.selectioned) {
            holder.itemView.background = context.getDrawable(R.drawable.item_back_selected)
        }else{
            holder.itemView.background = context.getDrawable(R.drawable.item_back)
        }

    }

    override fun getItemCount() = data.size

    inner class TechnicalAdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val description: TextView = itemView.findViewById(R.id.text_description)
    }
}