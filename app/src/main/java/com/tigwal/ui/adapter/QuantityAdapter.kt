package com.tigwal.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tigwal.R
import com.tigwal.utils.AppUtils

class QuantityAdapter(
    private val SpinnerSize: ArrayList<String>
) :
    RecyclerView.Adapter<QuantityAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtItem: AppCompatTextView = itemView.findViewById(R.id.txtItem)

        override fun onClick(v: View) {

        }
        init {
             itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_spinner_item, parent, false)
        return MyViewHolder(deals)

    }

    override fun getItemCount(): Int {
        return SpinnerSize.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtItem.typeface = AppUtils.getSEMIBOLD(context)
        holder.txtItem.text = SpinnerSize[position]
        holder.itemView.setOnClickListener()
        {
            onItemClickLister!!.itemClicked(holder.itemView, position)
        }
    }
}