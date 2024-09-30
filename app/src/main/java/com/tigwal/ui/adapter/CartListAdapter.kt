package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.listCart.CartListItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.RoundRectCornerImageView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CartListAdapter(
    private val arrayListCartList: ArrayList<CartListItem>,
    private val admin_commision: String
) :
    RecyclerView.Adapter<CartListAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context
    private var totalQuantity: Int = 0
    private var totalPrice: Double = 0.0
    var price: Double = 0.0
    var totalDiscount: Double = 0.0
    var totalServiceTax: Double = 0.0
    var admin_commision_total: Double = 0.0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var imgAddNote: LinearLayout = itemView.findViewById(R.id.ll_cart_addNote)
        var imgCartDelete: AppCompatImageView = itemView.findViewById(R.id.imgCartDelete)
        var iv_cart_productImage: RoundRectCornerImageView =
            itemView.findViewById(R.id.iv_cart_productImage)
        var totalCalculatePrice: AppCompatTextView = itemView.findViewById(R.id.totalCalculatePrice)
        var cartItemNotes: AppCompatTextView = itemView.findViewById(R.id.cartItemNotes)
        var txt_cart_productName: AppCompatTextView =
            itemView.findViewById(R.id.txt_cart_productName)
        var txt_cart_location: AppCompatTextView = itemView.findViewById(R.id.txt_cart_location)
        var txt_cart_price: AppCompatTextView = itemView.findViewById(R.id.txt_cart_price)
        var txtPrice: AppCompatTextView = itemView.findViewById(R.id.txtPrice)
        var iv_cart_minus: AppCompatTextView = itemView.findViewById(R.id.iv_cart_minus)
        var txtBookingDate: AppCompatTextView = itemView.findViewById(R.id.txtBookingDate)
        var txtAddNote: AppCompatTextView = itemView.findViewById(R.id.txtAddNote)
        var txtTimeSlot: AppCompatTextView = itemView.findViewById(R.id.txtTimeSlot)
        var txt_cart_total_item: AppCompatTextView = itemView.findViewById(R.id.txt_cart_total_item)
        var iv_cart_plus: AppCompatTextView = itemView.findViewById(R.id.iv_cart_plus)
        var txtServiceProviderName: AppCompatTextView =
            itemView.findViewById(R.id.txtServiceProviderName)
        var txtPerPerson: AppCompatTextView =
            itemView.findViewById(R.id.txtPerPerson)
        var txtDiscountPercentage: AppCompatTextView =
            itemView.findViewById(R.id.txtDiscountPercentage)

        var txtDiscount: AppCompatTextView =
            itemView.findViewById(R.id.txtDiscount)
        var llDiscount: LinearLayout =
            itemView.findViewById(R.id.llDiscount)

        override fun onClick(v: View) {
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_cart_item, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListCartList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.txtServiceProviderName.typeface = AppUtils.getREG(context)
        holder.txt_cart_productName.typeface = AppUtils.getREG(context)
        holder.txtPrice.typeface = AppUtils.getREG(context)
        holder.txt_cart_location.typeface = AppUtils.getREG(context)
        holder.txt_cart_price.typeface = AppUtils.getREG(context)
        holder.txtAddNote.typeface = AppUtils.getREG(context)
        holder.iv_cart_minus.typeface = AppUtils.getREG(context)
        holder.iv_cart_plus.typeface = AppUtils.getREG(context)
        holder.txt_cart_total_item.typeface = AppUtils.getBOLDNEW(context)
        holder.cartItemNotes.typeface = AppUtils.getSEMIBOLD(context)

        if (arrayListCartList.get(position).location != null) {

            if (arrayListCartList[position].notes != null && !arrayListCartList[position].notes.equals(
                    ""
                )
            ) {
                holder.cartItemNotes.visibility = View.VISIBLE
                holder.txtAddNote.setText(context.resources.getString(R.string.edit_note))
                val spannable =
                    SpannableString(
                        context.resources.getString(R.string.notes) + " " + arrayListCartList[position].notes
                    )
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    0, // start
                    6, // end
                    Spannable.SPAN_EXCLUSIVE_INCLUSIVE
                )
                holder.cartItemNotes.text = spannable

            } else {
                holder.cartItemNotes.visibility = View.GONE
                holder.txtAddNote.setText(context.resources.getString(R.string.add_note))
            }


            if (arrayListCartList[position].location!!.locName != null && !arrayListCartList[position].location!!.locName.equals(
                    ""
                )
            ) {
                holder.txt_cart_productName.text = "" + AppUtils.capitalize(
                    arrayListCartList[position].location!!.locName.toString()
                )
            }

            if (arrayListCartList[position]!!.bookDate != null && !arrayListCartList[position]!!.bookDate.equals(
                    ""
                )
            ) {
                val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
                val outputFormat: DateFormat = SimpleDateFormat("dd-MMM-yyyy")
                val date: Date = inputFormat.parse(arrayListCartList[position]!!.bookDate)
                val outputDateStr: String = outputFormat.format(date)
                holder.txtBookingDate.text = outputDateStr
            }

            if (arrayListCartList[position]!!.slots != null) {
                if (arrayListCartList[position]!!.slots!!.startTime != null && !arrayListCartList[position]!!.slots!!.startTime.equals(
                        ":"
                    )
                )

                    holder.txtTimeSlot.text =
                        arrayListCartList[position]!!.slots!!.startTime.toString() + "-" + arrayListCartList[position]!!.slots!!.endTime.toString()
            }
            if (arrayListCartList[position].location!!.images != null && arrayListCartList[position].location!!.images!!.isNotEmpty()) {
                Glide.with(Objects.requireNonNull(context))
                    .load(
                        arrayListCartList[position].location!!.images!![0]!!.image
                    )
                    .placeholder(R.drawable.img_placeholder).fitCenter()
                    .into(holder.iv_cart_productImage)
            }
            if (arrayListCartList[position].location!!.users!!.firstName != null && !arrayListCartList[position].location!!.users!!.firstName.equals(
                    ""
                )
            ) {
                holder.txtServiceProviderName.text = "" + AppUtils.capitalize(
                    arrayListCartList[position].location!!.users!!.firstName.toString() + " " + arrayListCartList[position].location!!.users!!.lastName.toString()
                )
            }
            if (arrayListCartList[position].location!!.locAddress != null && !arrayListCartList[position].location!!.locAddress.equals(
                    ""
                )
            ) {
                holder.txt_cart_location.text = "" + AppUtils.capitalize(
                    arrayListCartList[position].location!!.locAddress.toString()
                )
            }

            totalQuantity = arrayListCartList[position].qty!!
            price = arrayListCartList[position].location!!.price!!.toDouble()
            totalPrice = price * totalQuantity

            // Total Discount Price
            if (arrayListCartList[position].location!!.discount != null && !arrayListCartList[position].location!!.discount!!.equals(
                    ""
                )
            ) {
                holder.txtDiscountPercentage.setText(
                    "(" + arrayListCartList[position].location!!.discount!!.toInt()
                        .toString() + "%)"
                )

                totalDiscount = totalPrice * arrayListCartList[position].location!!.discount!!.toInt()
                    .toDouble() / 100

            } else {
                totalDiscount = 0.0
            }

            if (totalDiscount > 0) {
                arrayListCartList[position].totalCalculatePrice =
                    (totalPrice - totalDiscount).toString()
                holder.totalCalculatePrice.text =
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        arrayListCartList[position].totalCalculatePrice
                    )
            } else {
                arrayListCartList[position].totalCalculatePrice = (totalPrice.toString())
                holder.totalCalculatePrice.text =
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        arrayListCartList[position].totalCalculatePrice
                    )
            }

            // Total Price
            if (arrayListCartList[position].location!!.price != null && arrayListCartList[position].price!! != ""
            ) {
                holder.txt_cart_price.text =
                    context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                        totalPrice.toString()
                    )
                holder.txtPerPerson.text =
                    arrayListCartList[position].qty.toString() + " " + context.resources.getString(R.string.per_person)
                arrayListCartList[position].totalPrice = totalPrice.toString()
            }

            // Discount
            if (arrayListCartList[position].location!!.discount != null && !arrayListCartList[position].location!!.discount!!.equals(
                    ""
                ) && !arrayListCartList[position].location!!.discount!!.equals("0")
            ) {


                holder.llDiscount.visibility = View.VISIBLE
                holder.txtDiscount.text =
                    context.getString(R.string.currency_symbol) + "" + (totalDiscount)
                arrayListCartList[position].totalDiscount = (totalDiscount).toString()
            } else {
                holder.llDiscount.visibility = View.GONE
            }


            // Service Tax
            if (arrayListCartList[position].service_tax != null && !arrayListCartList[position].service_tax!!.equals(
                    ""
                )
            ) {
                totalServiceTax =
                    arrayListCartList[position].totalCalculatePrice.toDouble() * arrayListCartList[position].service_tax.toString()
                        .toDouble()
                totalServiceTax = totalServiceTax / 100
                arrayListCartList[position].totalServiceTax = totalServiceTax.toString()

                Log.d("totalServiceTax", "===== TOTAL PRICE =======>" + totalPrice)

                Log.d("totalServiceTax", "== Total Service Tax ==>" + totalServiceTax)
            }

            // Quantity
            if (arrayListCartList[position].qty != null && !arrayListCartList[position].qty!!.equals(
                    ""
                )
            ) {
                holder.txt_cart_total_item.text = "" + arrayListCartList[position].qty.toString()
            }

            if (admin_commision != null && !admin_commision!!.equals(
                    ""
                )
            ) {
                arrayListCartList[position].admin_commision = admin_commision
            }
            if (arrayListCartList[position].location!!.users!!.destination_id != null && !arrayListCartList[position].location!!.users!!.destination_id!!.equals(
                    ""
                )
            ) {
                arrayListCartList[position].destination_id =
                    arrayListCartList[position].location!!.users!!.destination_id!!
            }

        if (arrayListCartList[position].location!!.vendorId!= null && !arrayListCartList[position].location!!.vendorId!!.equals(
                    ""
                )
            ) {
                arrayListCartList[position].vendor_id =
                    arrayListCartList[position].location!!.vendorId!!.toString()
            }

            admin_commision_total = (totalPrice * admin_commision.toDouble()) / 100
            arrayListCartList[position].admin_commision_total = admin_commision_total.toString()

            arrayListCartList[position].admin_commision_total_amt =
                (arrayListCartList[position].totalCalculatePrice.toDouble() - admin_commision_total).toString()

            Log.e("TAG", "onBindViewHolder:total_amt " + arrayListCartList[position].admin_commision_total_amt)
            Log.e("TAG", "onBindViewHolder:com " + admin_commision_total)
            // clickListener
            holder.imgAddNote.setOnClickListener()
            {
                onItemClickLister!!.itemAddNote(position, arrayListCartList[position])
            }
            holder.imgCartDelete.setOnClickListener()
            {
                onItemClickLister!!.itemDelete(position, arrayListCartList[position])
            }

            holder.iv_cart_plus.setOnClickListener()
            {
                totalQuantity = arrayListCartList[position].qty!!
                if (totalQuantity > 0) {
                    totalQuantity += 1
                    holder.txt_cart_total_item.text = totalQuantity.toString()
                    arrayListCartList[position].qty = totalQuantity
                    notifyItemChanged(position)
                    onItemClickLister!!.itemCartUpdate(position, arrayListCartList[position])
                }
            }

            holder.iv_cart_minus.setOnClickListener()
            {
                totalQuantity = arrayListCartList[position].qty!!
                if (totalQuantity != 1) {
                    totalQuantity = totalQuantity - 1
                    holder.txt_cart_total_item.text = totalQuantity.toString()
                    arrayListCartList[position].qty = totalQuantity
                    notifyItemChanged(position)
                    onItemClickLister!!.itemCartUpdate(position, arrayListCartList[position])
                }
            }

        }
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
        fun itemDelete(position: Int, get: CartListItem)
        fun itemCartUpdate(position: Int, get: CartListItem)
        fun itemAddNote(position: Int, get: CartListItem)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }
}