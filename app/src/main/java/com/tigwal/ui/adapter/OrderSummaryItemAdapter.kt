package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.data.model.order_detail.OrderitemsItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.RoundRectCornerImageView

class OrderSummaryItemAdapter(
    private val arrayList: List<OrderitemsItem?>?
) :
    RecyclerView.Adapter<OrderSummaryItemAdapter.MyViewHolder>() {
    private lateinit var context: Context
    private var onItemClickLister: OnItemClickLister? = null
    private var countTemp: Int = 0
    private var totalPrice: Double = 0.0
    var price: Double = 0.0
    var strDiscount: Double = 0.0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var llChatView: LinearLayout = itemView.findViewById(R.id.llChatView)
        var llRatingView: LinearLayout = itemView.findViewById(R.id.llRatingView)
        var txt_productStar: AppCompatTextView = itemView.findViewById(R.id.txt_productStar)
        var txt_order_name: AppCompatTextView = itemView.findViewById(R.id.txt_order_name)
        var txtQuantity: AppCompatTextView = itemView.findViewById(R.id.txtQuantity)
        var txtServiceProviderName: AppCompatTextView =
            itemView.findViewById(R.id.txtServiceProviderName)
        var txt_order_location: AppCompatTextView = itemView.findViewById(R.id.txt_order_location)
        var txt_order_quantity: AppCompatTextView = itemView.findViewById(R.id.txt_order_quantity)
        var txtPrice: AppCompatTextView = itemView.findViewById(R.id.txtPrice)
        var txt_order_price: AppCompatTextView = itemView.findViewById(R.id.txt_order_price)
        var txtChat: AppCompatTextView = itemView.findViewById(R.id.txtChat)
        var iv_order_image: RoundRectCornerImageView = itemView.findViewById(R.id.iv_order_image)
        var txtDiscount: AppCompatTextView = itemView.findViewById(R.id.txtDiscount)
        var txtTimeSlotFinal: AppCompatTextView = itemView.findViewById(R.id.txtTimeSlotFinal)
        var llDiscount: LinearLayout = itemView.findViewById(R.id.llDiscount)
        override fun onClick(v: View) {

        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_order_summary_item, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (arrayList!![position]!!.locations!!.status.equals("1")) {
            RestConstant.ongoingMenuType = "1"
        } else {
            RestConstant.ongoingMenuType = "2"
        }
        holder.txt_order_name.typeface = AppUtils.getMIDIUM(context)
        holder.txtQuantity.typeface = AppUtils.getREG(context)
        holder.txtServiceProviderName.typeface = AppUtils.getMIDIUM(context)
        holder.txt_order_location.typeface = AppUtils.getMIDIUM(context)
        holder.txt_order_quantity.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.txtPrice.typeface = AppUtils.getMIDIUM(context)
        holder.txt_order_price.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.txtChat.typeface = AppUtils.getMIDIUM(context)
        holder.txtServiceProviderName.visibility = View.GONE

        if (arrayList!![position]!!.locations!!.locName != null && !arrayList[position]!!.locations!!.locName.equals(
                ""
            )
        ) {
            holder.txt_order_name.text = "" + arrayList[position]!!.locations!!.locName
        }


        if (arrayList[position]!!.locations!!.averageRating != null && arrayList[position]!!.locations!!.averageRating!! > 0) {
            holder.txt_productStar.text = "" + arrayList[position]!!.locations!!.averageRating
        } else {
            holder.txt_productStar.text = "0"
        }

        if (arrayList[position]!!.locations!!.locAddress != null && !arrayList[position]!!.locations!!.locAddress.equals(
                ""
            )
        ) {
            holder.txt_order_location.text = "" + arrayList[position]!!.locations!!.locAddress
        }


        if (arrayList.get(position)!!.slots != null && arrayList.get(position)!!.slots!!.startTime != null) {
            holder.txtTimeSlotFinal.setText(
                arrayList.get(position)!!.slots!!.startTime.toString() + " - " + arrayList.get(
                    position
                )!!.slots!!.endTime.toString()
            )
        }


        if (arrayList[position]!!.qty != null && !arrayList[position]!!.qty!!.equals(
                ""
            )
        ) {
            holder.txt_order_quantity.text = "" + arrayList[position]!!.qty
        }


        if (arrayList[position]!!.locations!!.images.size > 0) {
            Glide.with(context)
                .load(arrayList[position]!!.locations!!.images[0]!!.image)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.iv_order_image)
        }

        if (arrayList.get(position)!!.locations!!.discount != null && !arrayList.get(position)!!.locations!!.discount.equals(
                ""
            ) && !arrayList.get(position)!!.locations!!.discount.equals("0")
        ) {
//            holder.txtDiscount.text =
//                (arrayList.get(position)!!.locations!!.discount!!.toInt() * arrayList.get(position)!!.qty!!.toInt())
//                    .toString() + "%"
            holder.txtDiscount.text = (arrayList.get(position)!!.locations!!.discount!!.toInt())
                .toString() + "%"
            holder.llDiscount.visibility = View.VISIBLE
        } else {
            holder.llDiscount.visibility = View.GONE
        }


        holder.itemView.setOnClickListener()
        {
            onItemClickLister!!.itemClicked(holder.itemView, position)
        }

        holder.llChatView.setOnClickListener()
        {
            onItemClickLister!!.itemChat(holder.itemView, position, arrayList[position]!!)
        }

        holder.llRatingView.setOnClickListener()
        {
            onItemClickLister!!.itemRating(holder.itemView, position, arrayList[position]!!)
        }


        if (arrayList[position]!!.qty != null){
            countTemp = arrayList[position]!!.qty!!
        }

        if (arrayList[position]!!.price != null){
            price = arrayList[position]!!.price!!.toDouble()
        }

        totalPrice = price * countTemp
//        holder.txt_order_price.text =
//            context.getString(R.string.currency_symbol)+"" + AppUtils.fractionalPartValueFormate(totalPrice.toString())
        if (arrayList[position]!!.locations!!.discount != null &&
            arrayList[position]!!.locations!!.discount!!.isNotEmpty()
        ){
            strDiscount =
                totalPrice * arrayList[position]!!.locations!!.discount!!.toInt().toDouble()
        }

//        strDiscount =
//            totalPrice * arrayList.get(position)!!.locations!!.discount!!.toInt().toDouble()
        strDiscount = strDiscount / 100
        if (strDiscount > 0) {
            holder.txt_order_price.text =
                context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                    ((totalPrice - strDiscount).toString())
                )
        } else {
            holder.txt_order_price.text =
                context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                    price.toString()
                )
        }


        if (arrayList.get(position)!!.is_rating == true) {
            holder.llRatingView.visibility = View.GONE
        } else {
            holder.llRatingView.visibility = View.VISIBLE
        }
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
        fun itemChat(view: View?, position: Int, orderitemsItem: OrderitemsItem)
        fun itemRating(view: View?, position: Int, orderitemsItem: OrderitemsItem)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }
}

