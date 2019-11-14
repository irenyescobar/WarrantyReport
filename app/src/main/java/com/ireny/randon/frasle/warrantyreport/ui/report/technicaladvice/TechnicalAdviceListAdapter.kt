package com.ireny.randon.frasle.warrantyreport.ui.report.technicaladvice

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.entites.ReportTechnicalAdvice
import com.irenyescobar.mytasksapp.ui.listeners.CheckedChangeListener

class TechnicalAdviceListAdapter internal constructor(
    context: Context,
    private val checkedChangeListener: CheckedChangeListener<ReportTechnicalAdvice>):
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

        holder.run {
            checkBox.isClickable = false
            checkBox.text = current.description
            checkBox.isChecked = current.selectioned
            checkBox.tag = current
            checkBox.setOnClickListener{
                val item = it.tag as ReportTechnicalAdvice
                item.selectioned = !item.selectioned
                checkedChangeListener.onCheckedChange(item)
            }

        }
    }

    override fun getItemCount() = data.size

    inner class TechnicalAdviceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)
    }
}