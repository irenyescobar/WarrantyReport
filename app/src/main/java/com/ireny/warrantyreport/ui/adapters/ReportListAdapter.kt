package com.ireny.warrantyreport.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.utils.toDateTextFormatted

class ReportListAdapter internal constructor(val context: Context,
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

        holder.textView.text =  "${context.getString(R.string.document_name)} ${current.code?:current.id} - ${(current.code_generated_at?:current.created_at).toDateTextFormatted()}"

    }

    override fun getItemCount() = data.size

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.text)
    }
}