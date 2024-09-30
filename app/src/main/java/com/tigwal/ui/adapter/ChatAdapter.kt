package com.tigwal.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.chat.ChatItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences
import de.hdodenhof.circleimageview.CircleImageView
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatAdapter(
    private val arrayListChat: ArrayList<ChatItem?>?,
    private var imageVendor1: String?
) :
    RecyclerView.Adapter<ChatAdapter.MyViewHolder>()
{
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var llSenderView: LinearLayout = itemView.findViewById(R.id.llSenderView)
        var llReceiverView: LinearLayout = itemView.findViewById(R.id.llReceiverView)
        var txtSenderMessage: AppCompatTextView = itemView.findViewById(R.id.txtSenderMessage)
        var txtReceiverName: AppCompatTextView = itemView.findViewById(R.id.txtReceiverName)
        var txtSenderDateTime: AppCompatTextView = itemView.findViewById(R.id.txtSenderDateTime)
        var txtReceiverDateTime: AppCompatTextView = itemView.findViewById(R.id.txtReceiverDateTime)
        var img_profilePic: CircleImageView = itemView.findViewById(R.id.img_profilePic)
        var llViewDateTime: LinearLayout = itemView.findViewById(R.id.llViewDateTime)
        var txtDate: AppCompatTextView = itemView.findViewById(R.id.txtDate)

        override fun onClick(v: View) {
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_chat, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayListChat!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (arrayListChat!!.get(position)!!.senderId!!.toString()
                .equals(MySharedPreferences.getMySharedPreferences()!!.userId!!)
        ) {
            holder.llSenderView.visibility = View.VISIBLE
            holder.llReceiverView.visibility = View.GONE
            holder.txtSenderMessage.setText(arrayListChat!![position]!!.message)
            holder.txtSenderDateTime.setText(AppUtils.convertUTC2LocalDateTime(arrayListChat!![position]!!.chatCreated.toString()))
        } else {
            holder.llReceiverView.visibility = View.VISIBLE
            holder.llSenderView.visibility = View.GONE
            holder.txtReceiverName.setText(arrayListChat!![position]!!.message)
            holder.txtReceiverDateTime.setText(AppUtils.convertUTC2LocalDateTime(arrayListChat!![position]!!.chatCreated.toString()))
        }

        if (imageVendor1 != null && !imageVendor1!!.equals(""))
        {
            Glide.with(context).load(imageVendor1)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.img_profilePic)
        }

        var previousTs: Long = 0
        if (position >= 1)
        {
            val previousMessage: ChatItem = arrayListChat.get(position - 1)!!
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = sdf.parse(previousMessage.chatCreated)
            val millis = date.time
            previousTs = millis
        }
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val date = sdf.parse(
            arrayListChat.get(position)
            !!.chatCreated
        )
        val millis = date.time
        cal1.timeInMillis = millis
        cal2.timeInMillis = previousTs
        val sameDay = cal1[Calendar.YEAR] === cal2[Calendar.YEAR] &&
                cal1[Calendar.DAY_OF_YEAR] === cal2[Calendar.DAY_OF_YEAR]
        if (!sameDay) {
            holder.llViewDateTime.visibility = View.VISIBLE
        } else {
            holder.llViewDateTime.visibility = View.GONE
        }


        if (arrayListChat.get(position)!!.chatCreated != null && !arrayListChat.get(position)!!.chatCreated.equals(
                ""
            )
        ) {
            holder.txtDate.text =
                arrayListChat.get(position)!!.chatCreated?.let {
                    AppUtils.formatToYesterdayOrToday(
                        it
                    )
                }
        }
    }
}