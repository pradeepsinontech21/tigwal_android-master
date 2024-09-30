package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.chatlist.DataItem
import com.tigwal.ui.activity.SupportChatActivity
import com.tigwal.utils.AppUtils
import com.tigwal.utils.RoundRectCornerImageView
import java.util.*
import kotlin.collections.ArrayList

class ChatListAdapter(
    private val arrayList: ArrayList<DataItem?>
) :
    RecyclerView.Adapter<ChatListAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context
    private var IMAGE_VENDOR: String = ""

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtName: AppCompatTextView = itemView.findViewById(R.id.txtName)
        var imgLocation: AppCompatImageView = itemView.findViewById(R.id.imgLocation)
        var llView: LinearLayout = itemView.findViewById(R.id.llView)
        var txtLastMessage: AppCompatTextView =
            itemView.findViewById(R.id.txtLastMessage)
        var txtLocation: AppCompatTextView = itemView.findViewById(R.id.txtLocation)
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
            .inflate(R.layout.row_chat_listing, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int, get: DataItem?)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.txtName.typeface = AppUtils.getMIDIUM(context)
        holder.txtLastMessage.typeface = AppUtils.getREG(context)
        holder.txtLocation.typeface = AppUtils.getREG(context)

        if (arrayList[position]!!.address != null && !arrayList[position]!!.address.equals("")) {
            holder.txtLocation.text = "" + arrayList[position]!!.address
            holder.txtLocation.visibility = View.VISIBLE
            holder.imgLocation.visibility = View.VISIBLE
        } else {
            holder.txtLocation.visibility = View.GONE
            holder.imgLocation.visibility = View.GONE
        }




        if (arrayList[position]!!.name != null && !arrayList[position]!!.name!!.equals("")) {
            holder.txtName.text = "" + AppUtils.capitalize(arrayList[position]!!.name!!)
        }

        if (arrayList[position]!!.chat!!.message != null && !arrayList[position]!!.chat!!.message.equals(
                ""
            )
        ) {
            holder.txtLastMessage.text = "" + arrayList[position]!!.chat!!.message.toString()
        }

        if (arrayList[position]!!.imageurl != null && !arrayList[position]!!.imageurl!!.equals("")) {
            Glide.with(Objects.requireNonNull(context))
                .load(arrayList[position]!!.imageurl)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.ivorderimage)
        }

        holder.llView.setOnClickListener {

            if (arrayList[position]!!.imageurl != null && !arrayList[position]!!.imageurl.toString()
                    .equals("")
            ) {
                IMAGE_VENDOR = arrayList[position]!!.imageurl.toString()
            }

            context.startActivity(
                Intent(context, SupportChatActivity::class.java)
                    .putExtra("VENDOR_ID", "" + arrayList[position]!!.userId.toString())
                    .putExtra("IMAGE_VENDOR", "" + IMAGE_VENDOR)
            )
            AppUtils.startFromRightToLeft(context)
        }
    }
}