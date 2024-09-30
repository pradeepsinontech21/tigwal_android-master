package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tigwal.R
import com.tigwal.data.model.notification_list.DataItem
import com.tigwal.utils.AppUtils

class NotificationAdapter(
    private val arrayListNotification: ArrayList<DataItem?>?
) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtNotificationTitle: AppCompatTextView =
            itemView.findViewById(R.id.txt_notification_title)
        var txt_notification_desc: AppCompatTextView =
            itemView.findViewById(R.id.txt_notification_desc)
        var tvDate: AppCompatTextView = itemView.findViewById(R.id.tvDate)
        var tvTime: AppCompatTextView = itemView.findViewById(R.id.tvTime)
        override fun onClick(v: View) {

        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_notification, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListNotification!!.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("ResourceType")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtNotificationTitle.typeface = AppUtils.getMIDIUM(context)
        holder.txt_notification_desc.typeface = AppUtils.getREG(context)
        holder.tvDate.typeface = AppUtils.getREG(context)
        holder.tvTime.typeface = AppUtils.getREG(context)

        if (arrayListNotification!![position]!!.title != null && !arrayListNotification[position]!!.title.equals(
                ""
            )
        ) {
            holder.txtNotificationTitle.text = AppUtils.capitalize(arrayListNotification[position]!!.title.toString())
        }

        if (arrayListNotification[position]!!.message != null && !arrayListNotification[position]!!.message.equals(
                ""
            )
        ) {
            holder.txt_notification_desc.text = arrayListNotification[position]!!.message
        }

        if (arrayListNotification[position]!!.notification_created != null && !arrayListNotification[position]!!.notification_created.equals(
                ""
            )
        ) {
            holder.tvDate.text =
                AppUtils.dateFormate(arrayListNotification[position]!!.notification_created.toString())
        }

        if (arrayListNotification[position]!!.notification_created != null && !arrayListNotification[position]!!.notification_created.equals(
                ""
            )
        ) {
            holder.tvTime.text =
                AppUtils.convertUTC2LocalDateTime(arrayListNotification[position]!!.notification_created.toString())
        }
//        if(position % 4 == 0)
//        {
//            holder.txtNotificationTitle.setTextColor(context.resources.getColor(R.color.red_dark))
//        }
//        else if (position % 4 == 1)
//        {
//            holder.txtNotificationTitle.setTextColor(context.resources.getColor(R.color.green_light))
//        }
//        else if (position % 4 == 2)
//        {
//            holder.txtNotificationTitle.setTextColor(context.resources.getColor(R.color.color_orange))
//        }
//        else
//        {
//            holder.txtNotificationTitle.setTextColor(context.resources.getColor(R.color.color_blue_light))
//        }
    }
}