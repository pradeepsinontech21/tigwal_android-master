package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.tigwal.R
import com.tigwal.data.model.getTimeSlot.DataItem
import com.tigwal.utils.AppUtils
import java.time.LocalTime
import java.util.*
import kotlin.math.abs

class TimeSlotAdapter(
    private val arrayListTimeSlot: ArrayList<DataItem?>,
    private val strBookingDate: String
) :
    RecyclerView.Adapter<TimeSlotAdapter.MyViewHolder>() {
    private var selectedPosition: Int = -1
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context
    var srtAvailableQty: Int = 0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtTimeSlot: AppCompatTextView = itemView.findViewById(R.id.txtTimeSlot)
        var txtAvailableCount: AppCompatTextView = itemView.findViewById(R.id.txtAvailableCount)
        override fun onClick(v: View) {

        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_timeslot_list, parent, false)
        return MyViewHolder(deals)

    }

    override fun getItemCount(): Int {
        return arrayListTimeSlot.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int, get: DataItem?)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged", "SetTextI18n", "NewApi")
    override fun onBindViewHolder(
        holder: MyViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        holder.txtTimeSlot.text =
            AppUtils.timeSlotChange(arrayListTimeSlot[position]!!.startTime.toString()) + " - " + AppUtils.timeSlotChange(
                arrayListTimeSlot[position]!!.endTime.toString()
            )

        if (selectedPosition == position) {
            holder.txtTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.color_primary))
            holder.txtTimeSlot.setBackgroundResource(R.drawable.backgrond_border_social)
            holder.txtAvailableCount.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.color_primary
                )
            )
        } else {

            var strAvailableQty = abs(arrayListTimeSlot[position]!!.availableQty!!.toInt())
            var strTotalQty = arrayListTimeSlot[position]!!.totalQty!!.toInt()
            srtAvailableQty = strTotalQty - strAvailableQty
            holder.txtAvailableCount.text =
                strAvailableQty.toString() + " " + context.resources.getString(R.string.available)

            if (strAvailableQty > 0) {
                holder.txtTimeSlot.setBackgroundResource(R.drawable.backgrond_profile_view)
                holder.txtTimeSlot.alpha = 1F
                holder.txtTimeSlot.isEnabled = true
                holder.txtAvailableCount.setTextColor(ContextCompat.getColor(context, R.color.grey))
                holder.txtTimeSlot.setTextColor(ContextCompat.getColor(context, R.color.grey))
            } else {
                holder.txtTimeSlot.setBackgroundResource(R.drawable.white_rect_border_fill_chat)
                holder.txtTimeSlot.alpha = 0.5F
                holder.txtTimeSlot.isEnabled = false
                holder.txtTimeSlot.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.grey
                    )
                )
                holder.txtAvailableCount.setTextColor(context.resources.getColor(R.color.grey))
            }
        }

        holder.itemView.setOnClickListener()
        {
            var strAvailableQty = abs(arrayListTimeSlot[position]!!.availableQty!!.toInt())
            var strTotalQty = arrayListTimeSlot[position]!!.totalQty!!.toInt()
            srtAvailableQty = strTotalQty - strAvailableQty
            if (strAvailableQty > 0)
            {
                selectedPosition = position
                onItemClickLister!!.itemClicked(
                    holder.itemView,
                    position,
                    arrayListTimeSlot[position]
                )
                notifyDataSetChanged()
            }
        }
    }
}