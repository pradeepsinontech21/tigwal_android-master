package com.tigwal.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.tigwal.R
import com.tigwal.data.model.language.LanguageListModel
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences

class ChangeLanguageAdapter(
    private val arraylist: ArrayList<LanguageListModel>
) :
    RecyclerView.Adapter<ChangeLanguageAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null

    private lateinit var conext: Context
    var isClick = 0

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var tv_LanName: AppCompatTextView = itemView.findViewById(R.id.tv_LanName)
        var iv_lanLogo: AppCompatImageView = itemView.findViewById(R.id.iv_lanLogo)
        var cb_check: AppCompatImageView = itemView.findViewById(R.id.cb_check)
        var llLanguageView: LinearLayout = itemView.findViewById(R.id.llLanguageView)
        override fun onClick(v: View) {
            isClick = adapterPosition
            MySharedPreferences.getMySharedPreferences()!!.language = arraylist.get(position).id
            onItemClickLister!!.itemClicked(
                v,
                adapterPosition
            )
            notifyDataSetChanged()
        }

        init {
            itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        conext = parent.context
        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_change_language_list, parent, false)
        return MyViewHolder(deals)
    }

    override fun getItemCount(): Int {
        return arraylist!!.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tv_LanName.setText(arraylist.get(position).name)
        holder.tv_LanName.typeface = AppUtils.getMIDIUM(conext)
        if (arraylist.get(position).id.equals(MySharedPreferences.getMySharedPreferences()!!.language)) {
            holder.cb_check.visibility = View.VISIBLE
            holder.llLanguageView.setBackgroundDrawable(conext.resources.getDrawable(R.drawable.backgrond_border_social))
        } else {
            holder.cb_check.visibility = View.GONE
            holder.llLanguageView.setBackgroundDrawable(conext.resources.getDrawable(R.drawable.white_rect_border))
        }
    }

    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }
}

