package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.product_detail.RelatedDataItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences
import com.tigwal.utils.RoundRectCornerImageView
import java.util.*
import kotlin.collections.ArrayList

class RecommendationAdapter(
    private val arrayListRecommendation: ArrayList<RelatedDataItem?>
) :
    RecyclerView.Adapter<RecommendationAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtRateCount: AppCompatTextView = itemView.findViewById(R.id.txtRateCount)
        var txtCategoryName: AppCompatTextView = itemView.findViewById(R.id.txtCategoryName)
        var imgProduct: RoundRectCornerImageView = itemView.findViewById(R.id.imgProduct)
        override fun onClick(v: View) {
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_recommendation_list, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListRecommendation.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int, get: RelatedDataItem)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtRateCount.typeface = AppUtils.getMIDIUM(context)
        holder.txtCategoryName.typeface = AppUtils.getMIDIUM(context)

        if (arrayListRecommendation[position]!!.locName != null && !arrayListRecommendation[position]!!.locName.equals("")
        ) {
            holder.txtCategoryName.text = AppUtils.capitalize(arrayListRecommendation[position]!!.locName.toString())
        }

        if(arrayListRecommendation[position]!!.images!=null && arrayListRecommendation[position]!!.images!!.isNotEmpty()) {
            if (arrayListRecommendation[position]!!.images!![0]!!.image != null && !arrayListRecommendation[position]!!.images!![0]!!.image.equals("")
            ) {
                Glide.with(Objects.requireNonNull(context))
                    .load(arrayListRecommendation[position]!!.images!![0]!!.image)
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(holder.imgProduct)
            }
        }

        if (arrayListRecommendation[position]!!.averageRating != null && arrayListRecommendation[position]!!.averageRating!! >0  )
        {
            holder.txtRateCount.text = "" + arrayListRecommendation[position]!!.averageRating
        } else {
            holder.txtRateCount.text = "0"
        }

        holder.itemView.setOnClickListener {
            onItemClickLister!!.itemClicked(holder.itemView, position,
                arrayListRecommendation[position]!!
            )
        }
    }
}