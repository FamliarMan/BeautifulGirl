package com.jianglei.beautifulgirl

import android.content.Context
import android.net.Uri
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jianglei.beautifulgirl.vo.ContentTitle

/**
 * @author jianglei on 1/4/19.
 */
class PictureTitleAdapter(private val context: Context, private val contents: MutableList<ContentTitle>) :
    RecyclerView.Adapter<PictureTitleAdapter.PictureTitleHolder>() {
    var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureTitleHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_picture_content, parent, false)
        return PictureTitleHolder(view)
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun onBindViewHolder(holder: PictureTitleHolder, position: Int) {
        val titleVo = contents[position]
        holder.tvTitle.text = titleVo.title
        holder.tvDesc.text = titleVo.desc
        if(titleVo.coverUrl==""){
            holder.ivCover.visibility=View.GONE
        }else{
            holder.ivCover.setImageURI(Uri.parse(titleVo.coverUrl))
            holder.ivCover.visibility=View.VISIBLE
        }
        holder.mainLayout.setOnClickListener {
                onItemClickListener!!.onItemClick(titleVo,position)

        }
    }

    class PictureTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvDesc: TextView = itemView.findViewById(R.id.tvDesc)
        var ivCover: ImageView = itemView.findViewById(R.id.ivCover)
        var mainLayout: CardView = itemView.findViewById(R.id.layoutMain)

    }

    interface OnItemClickListener {
        fun onItemClick(title: ContentTitle, pos: Int)
    }
}

