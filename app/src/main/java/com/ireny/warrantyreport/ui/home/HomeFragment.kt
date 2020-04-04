package com.ireny.warrantyreport.ui.home

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ireny.warrantyreport.R
import com.ireny.warrantyreport.entities.Report01
import com.ireny.warrantyreport.repositories.ReportRepository
import com.ireny.warrantyreport.repositories.listeners.DeleteErrorListener
import com.ireny.warrantyreport.repositories.listeners.GetErrorListener
import com.ireny.warrantyreport.services.IReportDirectoryManager
import com.ireny.warrantyreport.ui.adapters.ReportListAdapter
import com.ireny.warrantyreport.ui.listeners.SelectedListener
import com.ireny.warrantyreport.utils.customApp
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(),
    GetErrorListener,
    SelectedListener<Report01> ,
    DeleteErrorListener,
    ReportListAdapter.RemoveItemListener{

    private val component by lazy { customApp.component }
    private val reportRepository: ReportRepository by lazy { component.reportRepository() }
    private val reportDirectoryManager: IReportDirectoryManager by lazy { component.reportDirectoryManager()}
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ReportListAdapter
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var dividerItemDecoration: DividerItemDecoration
    private var listener:Listener? = null
    private lateinit var colorDrawableBackground: ColorDrawable
    private lateinit var deleteIcon: Drawable

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupRecyclerView()
        setupViewModel()
    }

    private fun setupRecyclerView(){
        adapter = ReportListAdapter(context!!, this, this)
        recyclerview.adapter = adapter
        linearLayoutManager = LinearLayoutManager(context!!)
        dividerItemDecoration = DividerItemDecoration(
            recyclerview.context,
            linearLayoutManager.orientation
        )
        recyclerview.layoutManager = linearLayoutManager

        colorDrawableBackground = ColorDrawable(resources.getColor(R.color.colorProgressBackground,null))
        deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)!!

        val itemTouchHelper = ItemTouchHelper(ItemTouchHelperCallback())
        itemTouchHelper.attachToRecyclerView(recyclerview)
    }

    private fun setupViewModel(){
        reportRepository.setDeleteErrorListener(this)
        viewModel = ViewModelProviders.of(this, HomeViewModel.Companion.Factory(
            customApp,
            reportRepository)
        ).get(HomeViewModel::class.java)

        viewModel.all.observe(this, Observer { data ->
            data?.let { adapter.setData(it) }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Listener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement HomeFragment.Listener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onSelected(item: Report01) {
       listener?.openReport(item.id)
    }

    override fun onGetError(id: Long, error: Exception) {
        listener?.showError(error.localizedMessage?:"")
    }

    override fun onDeleteError(error: Exception) {
        listener?.showError(error.localizedMessage?:"")
    }

    override fun onRemoveItem(reportId: Long) {
        reportDirectoryManager.removeImages(reportId)
        viewModel.remove(reportId)
    }

    interface Listener{
        fun showError(error:String)
        fun openReport(reportId:Long)
    }

    inner class ItemTouchHelperCallback : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, viewHolder2: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDirection: Int) {
            adapter.removeItem(viewHolder.adapterPosition, viewHolder)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val itemView = viewHolder.itemView
            val iconMarginVertical = (viewHolder.itemView.height - deleteIcon.intrinsicHeight) / 2

            if (dX > 0) {
                colorDrawableBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                deleteIcon.setBounds(itemView.left + iconMarginVertical, itemView.top + iconMarginVertical,
                    itemView.left + iconMarginVertical + deleteIcon.intrinsicWidth, itemView.bottom - iconMarginVertical)
            } else {
                colorDrawableBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                deleteIcon.setBounds(itemView.right - iconMarginVertical - deleteIcon.intrinsicWidth, itemView.top + iconMarginVertical,
                    itemView.right - iconMarginVertical, itemView.bottom - iconMarginVertical)
                deleteIcon.level = 0
            }

            colorDrawableBackground.draw(c)

            c.save()

            if (dX > 0)
                c.clipRect(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
            else
                c.clipRect(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)

            deleteIcon.draw(c)

            c.restore()

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}