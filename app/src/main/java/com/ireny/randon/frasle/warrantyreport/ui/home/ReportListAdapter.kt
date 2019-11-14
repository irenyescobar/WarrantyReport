package com.ireny.randon.frasle.warrantyreport.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.randon.frasle.warrantyreport.R
import com.ireny.randon.frasle.warrantyreport.ui.listeners.SelectedListener
import com.ireny.randon.frasle.warrantyreport.entities.Report
import com.ireny.randon.frasle.warrantyreport.utils.toDateTimeTextFormatted

class ReportListAdapter internal constructor( context: Context,
    private val listener: SelectedListener<Report>
) : RecyclerView.Adapter<ReportListAdapter.ReportViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var data: MutableList<Report> = mutableListOf()


    internal fun setData(items: List<Report>) {
        data.clear()
        data.addAll(items)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = inflater.inflate(R.layout.report_item, parent, false)
        return ReportViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val current = data[position]

        holder.itemView.setOnClickListener{
            listener.onSelected(current)
        }

        holder.id.text = current.created_at.toDateTimeTextFormatted()
        holder.type.text = "${current.distributor}/${current.client}"

    }

    override fun getItemCount() = data.size

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.text_id)
        val type: TextView = itemView.findViewById(R.id.text_type)
    }
}