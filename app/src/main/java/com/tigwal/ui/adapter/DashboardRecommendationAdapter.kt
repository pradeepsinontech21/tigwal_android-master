package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.tigwal.R
import com.tigwal.data.model.recommendation.DataItem
import com.tigwal.utils.AppUtils
import kotlin.collections.ArrayList

class DashboardRecommendationAdapter(
    private val arrayListRecommendation: ArrayList<DataItem?>?
) :
    RecyclerView.Adapter<DashboardRecommendationAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtName: AppCompatTextView = itemView.findViewById(R.id.txtName)
        var imgProduct: ShapeableImageView = itemView.findViewById(R.id.imgProduct)
        override fun onClick(v: View) {
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_dashboard_recommendation_list, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListRecommendation!!.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int, get: DataItem)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {


        if (arrayListRecommendation!![position]!!.locName != null && !arrayListRecommendation[position]!!.locName.equals(
                ""
            )
        ) {
            holder.txtName.text =
                AppUtils.capitalize(arrayListRecommendation[position]!!.locName.toString())
        }

        if (arrayListRecommendation[position]!!.images != null && arrayListRecommendation[position]!!.images!!.size > 0) {
            if (arrayListRecommendation[position]!!.images!![0]!!.image != null && !arrayListRecommendation[position]!!.images!![0]!!.image.equals(
                    ""
                )
            ) {
                Glide.with(context)
                    .load(arrayListRecommendation[position]!!.images!![0]!!.image.toString())
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(holder.imgProduct)
            }
        }

        holder.itemView.setOnClickListener {
            onItemClickLister!!.itemClicked(
                holder.itemView, position,
                arrayListRecommendation[position]!!
            )
        }
    }
}