package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.product_listing.DataItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.RoundRectCornerImageView
import java.util.*

class ProductListAdapter(
    private val arrayListCategory: List<DataItem?>?
) :
    RecyclerView.Adapter<ProductListAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context
    var strDiscount: Double = 0.0
    var strTotalPrice: Double = 0.0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txt_order_name: AppCompatTextView = itemView.findViewById(R.id.txt_order_name)
        var txt_productStar: AppCompatTextView = itemView.findViewById(R.id.txt_productStar)
        var txtServiceProviderName: AppCompatTextView =
            itemView.findViewById(R.id.txtServiceProviderName)
        var txtPrice: AppCompatTextView = itemView.findViewById(R.id.txtPrice)
        var tvPrice: AppCompatTextView = itemView.findViewById(R.id.tvPrice)
        var txtslesh: AppCompatTextView = itemView.findViewById(R.id.txtslesh)
        var txtLocation: AppCompatTextView = itemView.findViewById(R.id.txtLocation)
        var txt_product_time: AppCompatTextView = itemView.findViewById(R.id.txt_product_time)
        var ivorderimage: RoundRectCornerImageView = itemView.findViewById(R.id.iv_order_image)
        var txtPerPersonMainDiscount: AppCompatTextView =
            itemView.findViewById(R.id.txtPerPersonMainDiscount)
        var txtPerPersonMainPrice: AppCompatTextView =
            itemView.findViewById(R.id.txtPerPersonMainPrice)

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
        fun itemClicked(view: View?, position: Int, get: DataItem?)
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
        holder.txtPerPersonMainDiscount.typeface = AppUtils.getREG(context)
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

//        if (arrayListCategory[position]!!.price != null && !arrayListCategory[position]!!.price.equals(
//                ""
//            )
//        ) {
//            if (arrayListCategory[position]!!.discount != null && !arrayListCategory[position]!!.discount.equals(
//                    ""
//                )
//            ) {
//                holder.tvPrice.text =
//                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
//                        (arrayListCategory[position]!!.price!!.toInt() - arrayListCategory[position]!!.discount!!.toInt()).toString()
//                    )
//            } else {
//                holder.tvPrice.text =
//                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
//                        arrayListCategory[position]!!.price.toString()
//                    )
//            }
//        }

        if (arrayListCategory[position]!!.users != null && !arrayListCategory[position]!!.users!!.equals(
                ""
            )
        ) {
            holder.txtServiceProviderName.text = "" + AppUtils.capitalize(
                arrayListCategory[position]!!.users!!.firstName + " " + arrayListCategory[position]!!.users!!.lastName
            )
        }


        if (arrayListCategory[position]!!.discount != null && !arrayListCategory[position]!!.discount.equals(
                ""
            ) && !arrayListCategory[position]!!.discount.equals(
                "0"
            )
        ) {
            strDiscount =
                arrayListCategory[position]!!.price!!.toDouble() * arrayListCategory[position]!!.discount!!.toInt()
                    .toDouble()
            strDiscount = strDiscount / 100
            holder.txtPerPersonMainPrice.visibility = View.VISIBLE
            holder.txtPerPersonMainDiscount.visibility = View.VISIBLE
            holder.txtslesh.visibility = View.VISIBLE
            if (strDiscount > 0) {
                var priceTemp = context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate((arrayListCategory[position]!!.price!!.toDouble() ).toString())
                val spannableString1 = SpannableString(priceTemp)
                spannableString1.setSpan(
                    StrikethroughSpan(),
                    0,
                    priceTemp.length,
                    0
                )
                holder.txtPerPersonMainPrice.text = spannableString1
                holder.tvPrice.setText(
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        ((arrayListCategory.get(position)!!.price!!.toDouble() - strDiscount).toString())))
                holder.txtPerPersonMainDiscount.setText("(" + arrayListCategory[position]!!.discount!!.toString() + "%)")
            } else {
                var priceTemp =
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        arrayListCategory[position]!!.price!!.toDouble().toString()
                    )
                val spannableString1 = SpannableString(priceTemp)
                spannableString1.setSpan(
                    StrikethroughSpan(),
                    0,
                    priceTemp.length,
                    0
                )
                holder.txtPerPersonMainPrice.text = spannableString1
                holder.txtPerPersonMainDiscount.setText(
                    "(" + arrayListCategory[position]!!.discount!!.toString() + "%)"
                )
                holder.tvPrice.setText(
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        ((arrayListCategory.get(position)!!.price!!.toDouble() - strDiscount).toString())
                    )
                )
            }
        } else {
            holder.txtPerPersonMainPrice.visibility = View.GONE
            holder.txtPerPersonMainDiscount.visibility = View.GONE
            holder.txtslesh.visibility = View.GONE
            holder.tvPrice.text =
                context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                    arrayListCategory[position]!!.price.toString()
                )
        }

        if (arrayListCategory[position]!!.images != null && arrayListCategory[position]!!.images!!.isNotEmpty()) {
            Glide.with(Objects.requireNonNull(context))
                .load(arrayListCategory[position]!!.images!![0]!!.image)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.ivorderimage)
        }

        if (arrayListCategory[position]!!.averageRating != null && arrayListCategory[position]!!.averageRating!! > 0) {
            holder.txt_productStar.text = "" + arrayListCategory[position]!!.averageRating
        } else {
            holder.txt_productStar.text = "0"
        }


        holder.itemView.setOnClickListener {
            onItemClickLister!!.itemClicked(
                holder.itemView,
                position,
                arrayListCategory.get(position)
            )
        }
    }
}