package com.ireny.warrantyreport.ui.report.photos


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.ui.listeners.ItemClickListener
import com.ireny.warrantyreport.ui.listeners.ItemLongClickListener
import kotlinx.android.synthetic.main.report_photo_item.view.*

class PhotoRecyclerViewAdapter(
    private val clickListener: ItemClickListener<PhotosFragment.Photo>?,
    private val longClickListener: ItemLongClickListener<PhotosFragment.Photo>?
) : RecyclerView.Adapter<PhotoRecyclerViewAdapter.ViewHolder>() {


    private var mValues: Array<PhotosFragment.Photo> = arrayOf()
    private val mOnClickListener: View.OnClickListener
    private val mOnLongClickListener: View.OnLongClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            clickListener?.run {
                val item = v.tag as PhotosFragment.Photo
                onClicked(item)
            }
        }

        mOnLongClickListener = View.OnLongClickListener { v ->
            longClickListener?.run {
                val item = v.tag as PhotosFragment.Photo
                onLongClicked(item)
            }
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.report_photo_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        item.image?.let {
            holder.image.setImageDrawable(it)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
            setOnLongClickListener(mOnLongClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    fun refresh(data: Array<PhotosFragment.Photo>){
        mValues = data
        notifyDataSetChanged()
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val image: ImageView = mView.imageView
    }

}
