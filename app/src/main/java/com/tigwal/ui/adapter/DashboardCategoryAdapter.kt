package com.tigwal.ui.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tigwal.R
import com.tigwal.data.model.getcategory.DataItem
import com.tigwal.utils.AppUtils
import com.tigwal.utils.MySharedPreferences
import java.util.ArrayList

class DashboardCategoryAdapter(
    private val arrayListCategory: ArrayList<DataItem>
) :
    RecyclerView.Adapter<DashboardCategoryAdapter.MyViewHolder>() {
    private var onItemClickLister: OnItemClickLister? = null
    private lateinit var context: Context

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var txtCategoryName: AppCompatTextView = itemView.findViewById(R.id.txtCategoryName)
        var imgCategory: AppCompatImageView = itemView.findViewById(R.id.imgCategory)

        override fun onClick(v: View) {
        }

        init {
            // itemView.setOnClickListener(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        context = parent.context

        val deals = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_dashboard_category_list, parent, false)
        return MyViewHolder(deals)


    }

    override fun getItemCount(): Int {
        return arrayListCategory.size
    }


    interface OnItemClickLister {
        fun itemClicked(view: View?, position: Int)
    }

    fun setOnItemClickLister(onItemClickLister: OnItemClickLister?) {
        this.onItemClickLister = onItemClickLister
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        if (arrayListCategory[position].categoryName != null && !arrayListCategory[position].categoryName.equals(
                ""
            )
        ) {
            if(MySharedPreferences.getMySharedPreferences()!!.language.equals("ar"))
            {
                holder.txtCategoryName.text =
                    "" + AppUtils.capitalize(arrayListCategory[position].category_name_ar.toString())
            }
            else {
                holder.txtCategoryName.text =
                    "" + AppUtils.capitalize(arrayListCategory[position].categoryName.toString())
            }
            }

        if (arrayListCategory[position].categoryImage != null && !arrayListCategory[position].categoryImage.equals(
                ""
            )
        ) {
            Glide.with(context)
                .load(arrayListCategory[position].categoryImage)
                .placeholder(R.drawable.img_placeholder).fitCenter()
                .into(holder.imgCategory)

        }

        holder.itemView.setOnClickListener()
        {
            onItemClickLister!!.itemClicked(holder.itemView, position)
        }
    }
}