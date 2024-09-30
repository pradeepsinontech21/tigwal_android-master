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
import com.tigwal.utils.AppUtils
import com.tigwal.utils.RoundRectCornerImageView
import java.util.*

class SearchListAdapter(
    private val arrayListCategory: ArrayList<com.tigwal.data.model.search.DataItem?>
) :
    RecyclerView.Adapter<SearchListAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txt_order_name: AppCompatTextView = itemView.findViewById(R.id.txt_order_name)
        var txt_productStar: AppCompatTextView = itemView.findViewById(R.id.txt_productStar)
        var txtServiceProviderName: AppCompatTextView =
            itemView.findViewById(R.id.txtServiceProviderName)
        var txtPrice: AppCompatTextView = itemView.findViewById(R.id.txtPrice)
        var tvPrice: AppCompatTextView = itemView.findViewById(R.id.tvPrice)
        var txtslesh: AppCompatTextView = itemView.findViewById(R.id.txtslesh)
        var txtPerPerson: AppCompatTextView = itemView.findViewById(R.id.txtPerPersonMainPrice)
        var txtLocation: AppCompatTextView = itemView.findViewById(R.id.txtLocation)
        var txt_product_time: AppCompatTextView = itemView.findViewById(R.id.txt_product_time)
        var ivorderimage: RoundRectCornerImageView = itemView.findViewById(R.id.iv_order_image)
        override fun onClick(v: View) {
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_product_listing, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListCategory!!.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txt_order_name.typeface = AppUtils.getMIDIUM(context)
        holder.txt_productStar.typeface = AppUtils.getREG(context)
        holder.txtServiceProviderName.typeface = AppUtils.getREG(context)
        holder.txtPrice.typeface = AppUtils.getREG(context)
        holder.tvPrice.typeface = AppUtils.getREG(context)
        holder.txtslesh.typeface = AppUtils.getMIDIUM(context)
        holder.txtPerPerson.typeface = AppUtils.getREG(context)
        holder.txtLocation.typeface = AppUtils.getREG(context)
        holder.txt_product_time.typeface = AppUtils.getREG(context)

        if (arrayListCategory!![position]!!.locName != null && !arrayListCategory[position]!!.locName.equals(
                ""
            )
        ) {
            holder.txt_order_name.text =
                "" + AppUtils.capitalize(arrayListCategory[position]!!.locName.toString())
        }

        if (arrayListCategory[position]!!.locAddress != null && !arrayListCategory[position]!!.locAddress.equals(
                ""
            )
        ) {
            holder.txtLocation.text = "" + arrayListCategory[position]!!.locAddress
        }

        if (arrayListCategory[position]!!.price != null && !arrayListCategory[position]!!.price.equals(
                ""
            )
        ) {
            if (arrayListCategory[position]!!.discount != null && !arrayListCategory[position]!!.discount.equals(
                    ""
                )
            ) {
                holder.tvPrice.text =
                    context.getString(R.string.currency_symbol)+"" + AppUtils.fractionalPartValueFormate((arrayListCategory[position]!!.price!!.toInt() - arrayListCategory[position]!!.discount!!.toInt()).toString())
            } else {
                holder.tvPrice.text =
                    context.getString(R.string.currency_symbol)+"" + AppUtils.fractionalPartValueFormate(arrayListCategory[position]!!.price.toString())
            }
        }

        holder.txtServiceProviderName.visibility=View.GONE

        if (arrayListCategory[position]!!.images != null && arrayListCategory[position]!!.images!!.isNotEmpty()) {
            Glide.with(Objects.requireNonNull(context))
                .load(arrayListCategory[position]!!.images!![0]!!.image)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.ivorderimage)
        }

        if (arrayListCategory[position]!!.averageRating != null && !arrayListCategory[position]!!.averageRating!!.equals(
                ""
            )
        ) {
            holder.txt_productStar.text = "" + arrayListCategory[position]!!.averageRating
        } else {
            holder.txt_productStar.text = "0"
        }

        holder.itemView.setOnClickListener {
            onItemClickLister!!.itemClicked(holder.itemView, position)
        }
    }
}