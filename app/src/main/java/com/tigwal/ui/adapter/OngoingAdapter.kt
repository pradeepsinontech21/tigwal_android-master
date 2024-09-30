package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tigwal.R
import com.tigwal.app.rest.RestConstant
import com.tigwal.data.model.listOrder.OrdersItem
import com.tigwal.utils.AppUtils

class OngoingAdapter(
    private val arrayList: ArrayList<OrdersItem?>,
    private val orderType:String

) :
    RecyclerView.Adapter<OngoingAdapter.MyViewHolder>() {
    private lateinit var context: Context
    private var onItemClickLister: OnItemClickLister? = null
    private var onItemOrderSummaryClickLister: OnItemOrderSummaryClickLister? = null
    private var onItemCancelOrderClickLister: OnItemCancelOrderClickLister? = null

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtOrderSummary: AppCompatTextView = itemView.findViewById(R.id.txtOrderSummary)
        var txtCancelOrder: AppCompatTextView = itemView.findViewById(R.id.txtCancelOrder)

        var tvOrderId: AppCompatTextView = itemView.findViewById(R.id.tvOrderId)
        var txt_cancel_orderId: AppCompatTextView = itemView.findViewById(R.id.txt_cancel_orderId)
        var tvTotalItems: AppCompatTextView = itemView.findViewById(R.id.tvTotalItems)
        var txt_cancel_totalOrder: AppCompatTextView =
            itemView.findViewById(R.id.txt_cancel_totalOrder)
        var tvTotalCharges: AppCompatTextView = itemView.findViewById(R.id.tvTotalCharges)
        var txt_order_totalCharge: AppCompatTextView =
            itemView.findViewById(R.id.txt_order_totalCharge)
        var tvOrderStatus: AppCompatTextView = itemView.findViewById(R.id.tvOrderStatus)
        var txt_OrderStatus: AppCompatTextView = itemView.findViewById(R.id.txt_OrderStatus)
        var txt_cancel_orderDate: AppCompatTextView =
            itemView.findViewById(R.id.txt_cancel_orderDate)
        var txt_cancel_orderTime: AppCompatTextView =
            itemView.findViewById(R.id.txt_cancel_orderTime)
        var llStatusView: LinearLayout =
            itemView.findViewById(R.id.llStatusView)
        var txtName: AppCompatTextView =
            itemView.findViewById(R.id.txtName)

        override fun onClick(v: View) {
            when (v.id) {
                R.id.txtOrderSummary -> {
                    onItemOrderSummaryClickLister!!.itemOrderSummaryClicked(
                        adapterPosition,
                        arrayList[adapterPosition]
                    )
                }

                R.id.txtCancelOrder -> {
                    onItemCancelOrderClickLister!!.itemCancelOrderClicked(
                        adapterPosition,
                        arrayList[adapterPosition]
                    )
                }
            }
        }

        init {
            itemView.setOnClickListener(this)
            txtOrderSummary.setOnClickListener(this)
            txtCancelOrder.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_ongoing_list, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tvOrderId.typeface = AppUtils.getREG(context)
        holder.txt_cancel_orderId.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.tvTotalItems.typeface = AppUtils.getREG(context)
        holder.txt_cancel_totalOrder.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.tvTotalCharges.typeface = AppUtils.getREG(context)
        holder.txt_order_totalCharge.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.tvOrderStatus.typeface = AppUtils.getREG(context)
        holder.txt_OrderStatus.typeface = AppUtils.getBOLDMIDIUM(context)
        holder.txt_cancel_orderDate.typeface = AppUtils.getREG(context)
        holder.txt_cancel_orderTime.typeface = AppUtils.getREG(context)
        holder.txtOrderSummary.typeface = AppUtils.getMIDIUM(context)
        holder.txtCancelOrder.typeface = AppUtils.getMIDIUM(context)


        if (arrayList[position]!!.orderitems!!.get(0)!!.bookDate != null && !arrayList[position]!!.orderitems!!.get(
                0
            )!!.bookDate.equals(
                ""
            )
        ) {
            holder.txt_cancel_orderDate.text =
                "" + AppUtils.dateFormateNew(arrayList[position]!!.orderitems!!.get(0)!!.bookDate.toString())
        }

        if (RestConstant.ongoingMenuType.equals("1")) {
            holder.txtCancelOrder.visibility = View.GONE
        } else {
            holder.txtCancelOrder.visibility = View.GONE
        }


        if (arrayList[position]!!.order_created != null && !arrayList[position]!!.order_created.equals(
                ""
            )
        ) {
            holder.txt_cancel_orderTime.text = AppUtils.convertUTC2LocalDateTime(
                arrayList[position]!!.order_created.toString()
            )
        }


        if (arrayList[position]!!.orderitems!! != null && arrayList[position]!!.orderitems!!.size > 0) {
            if (arrayList[position]!!.orderitems!!.get(0)!!.locations != null) {
                holder.txtName.text =
                    AppUtils.capitalize(arrayList[position]!!.orderitems!!.get(0)!!.locations!!.locName.toString())
            }
        }


        if (arrayList[position]!!.totalAmount != null && !arrayList[position]!!.totalAmount.equals(
                ""
            )
        ) {
            holder.txt_order_totalCharge.text =
                context.getString(R.string.currency_symbol) + "" + AppUtils.fractionalPartValueFormate(
                    arrayList[position]!!.totalAmount.toString()
                )
        }


        if (arrayList[position]!!.orderitems != null && arrayList[position]!!.orderitems!!.isNotEmpty()) {
            holder.txt_cancel_totalOrder.text = "" + arrayList[position]!!.orderitems!!.size
        }

        if (arrayList[position]!!.orderId != null && !arrayList[position]!!.orderId.equals("")) {
            holder.txt_cancel_orderId.text = "" + arrayList[position]!!.orderId
        }

        if (arrayList[position]!!.status != null && !arrayList[position]!!.status.equals(
                ""
            )
        ) {
            holder.llStatusView.visibility = View.VISIBLE
            if (arrayList[position]!!.status.equals("2")) {
                holder.txt_OrderStatus.text = context.resources.getString(R.string.success_order)
                holder.txt_OrderStatus.setTextColor(context.resources.getColor(R.color.color_blue_light))
            } else if (arrayList[position]!!.status.equals("3")) {
                holder.txt_OrderStatus.text = context.resources.getString(R.string.fail_order)
//                holder.txt_OrderStatus.setTextColor(context.resources.getColor(R.color.red))
            } else if (arrayList[position]!!.status.equals("4")) {
                holder.txt_OrderStatus.text = context.resources.getString(R.string.cancel_order)
//                holder.txt_OrderStatus.setTextColor(context.resources.getColor(R.color.red))
            }
        } else {
            holder.llStatusView.visibility = View.GONE
        }
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    interface OnItemCancelOrderClickLister {
        fun itemCancelOrderClicked(
            position: Int,
            get: OrdersItem?
        )
    }

    fun setOnItemCancelOrderClickLister(onItemCancelOrderClickLister: OnItemCancelOrderClickLister?) {
        this.onItemCancelOrderClickLister = onItemCancelOrderClickLister
    }

    interface OnItemOrderSummaryClickLister {
        fun itemOrderSummaryClicked(
            position: Int,
            get: OrdersItem?
        )
    }

    fun setOnItemOrderSummaryClickLister(onItemOrderSummaryClickLister: OnItemOrderSummaryClickLister?) {
        this.onItemOrderSummaryClickLister = onItemOrderSummaryClickLister
    }
}

