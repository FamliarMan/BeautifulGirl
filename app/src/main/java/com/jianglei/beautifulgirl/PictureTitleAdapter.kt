package com.jianglei.beautifulgirl

import android.content.Context
import android.net.Uri
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.jianglei.beautifulgirl.spider.PictureTitleVo

/**
 * @author jianglei on 1/4/19.
 */
class PictureTitleAdapter(private val context: Context, private val pictures: MutableList<PictureTitleVo>) :
    RecyclerView.Adapter<PictureTitleAdapter.PictureTitleHolder>() {
    var onItemClickListener: OnItemClickListener? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictureTitleHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.listitem_picture_content, parent, false)
        return PictureTitleHolder(view)
    }

    override fun getItemCount(): Int {
        return pictures.size
    }

    override fun onBindViewHolder(holder: PictureTitleHolder, position: Int) {
        val titleVo = pictures[position]
        holder.tvTitle.text = titleVo.title
        holder.tvDesc.text = titleVo.desc
        holder.ivCover.setImageURI(Uri.parse(titleVo.coverUrl))
//        val options = RequestOptions()
//            .placeholder(R.mipmap.holder_picture)
//            .centerCrop()
//            .dontAnimate()
//        Glide.with(context)
//            .load(titleVo.coverUrl)
//            .apply(options)
//            .into(holder.ivCover)
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
        fun onItemClick(titleVo: PictureTitleVo, pos: Int)
    }
}

