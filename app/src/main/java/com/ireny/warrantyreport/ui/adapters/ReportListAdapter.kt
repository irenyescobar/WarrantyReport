package com.ireny.warrantyreport.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report01
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.utils.toDateTextFormatted

class ReportListAdapter internal constructor(
    val context: Context,
    private val listener: SelectedListener<Report01>,
    private val removeListener: RemoveItemListener?

) : RecyclerView.Adapter<ReportListAdapter.ReportViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var data: MutableList<Report01> = mutableListOf()

    internal fun setData(items: List<Report01>) {
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
        holder.company.text =  if(current.company == "") "Não selecionado - ${current.id}" else current.company

        if(current.code != null){
            holder.created.text = current.code
        }else {
            holder.created.text = current.created_at.toDateTextFormatted()
        }

        holder.distributor.text = if(current.distributor == "") "Não informado" else current.distributor
        holder.client.text = if(current.client == "") "Não informado" else current.client

        when (current.companyId) {
            1 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.frasle))
            }
            2 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.fremax))
            }
            3 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.controil))
            }
            4 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.jost))
            }
            5 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.suspensys))
            }
            6 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.master))
            }
            7 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.lonaflex))
            }
            8 -> {
                holder.companyLogo.setImageDrawable(context.getDrawable(R.drawable.castertech))
            }
            0, null -> {
                holder.companyLogo.setImageDrawable(null)
            }
        }
    }

    override fun getItemCount() = data.size

    fun removeItem(position: Int, viewHolder: RecyclerView.ViewHolder) {
        removeListener?.run {
            val item = data[position]
            onRemoveItem(item.id)
        }
    }

    inner class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val company: TextView = itemView.findViewById(R.id.company)
        val created: TextView = itemView.findViewById(R.id.created)
        val distributor: TextView = itemView.findViewById(R.id.distributor)
        val client: TextView = itemView.findViewById(R.id.client)
        val companyLogo: ImageView = itemView.findViewById(R.id.companyLogo)
    }

    interface RemoveItemListener{
        fun onRemoveItem(reportId:Long)
    }
}