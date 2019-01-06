package com.jianglei.beautifulgirl

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.CardView
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.DataSourceCenter
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.vo.PictureTypeVo
import com.jianglei.beautifulgirl.vo.WebsiteVo
import kotlinx.android.synthetic.main.activity_all_website.*
import utils.ToastUtils

class AllWebsiteActivity : BaseActivity() {
    private var allWebsites: ArrayList<WebsiteVo>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_website)
        allWebsites = intent.getParcelableArrayListExtra("websites")
        if (allWebsites == null) {
            return
        }
        rvWebsites.layoutManager = GridLayoutManager(this, 2)
        val adapter = WebsiteAdapter(this, allWebsites!!)
        adapter.onItemClickListener = object : OnItemClickListener<WebsiteVo> {
            override fun onItemClick(vo: WebsiteVo, pos: Int) {
                getPictureTypes(vo)
            }

        }
        rvWebsites.adapter = adapter
    }


    private fun getPictureTypes(vo: WebsiteVo) {
        val dataSource = DataSourceCenter.getDataSource(vo.dataSourceKey)
        showProgress(true)
        dataSource?.fetAllTypes(vo.homePageUrl, object : OnDataResultListener<MutableList<PictureTypeVo>> {
            override fun onSuccess(data: MutableList<PictureTypeVo>) {
                showProgress(false)
                val intent = Intent(this@AllWebsiteActivity, ContentActivity::class.java)
                intent.putParcelableArrayListExtra("types", data as java.util.ArrayList<out Parcelable>)
                intent.putExtra("dataSourceKey",vo.dataSourceKey)
                startActivity(intent)
            }

            override fun onError(msg: String) {
                showProgress(false)
                ToastUtils.showMsg(this@AllWebsiteActivity, msg)
            }

        })

    }

    private inner class WebsiteAdapter(private var context: Context, private var websites: MutableList<WebsiteVo>) :
        RecyclerView.Adapter<WebsiteHolder>() {
        var onItemClickListener: OnItemClickListener<WebsiteVo>? = null
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WebsiteHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.listitem_all_websites, parent, false)
            return WebsiteHolder(view)
        }

        override fun getItemCount(): Int {
            return websites.size
        }

        override fun onBindViewHolder(holder: WebsiteHolder, position: Int) {
            holder.ivContent.setImageResource(websites[position].icon)
            holder.tvName.text = websites[position].name
            holder.mainItem.setOnClickListener {
                onItemClickListener?.onItemClick(websites[position], position)
            }
        }


    }

    private class WebsiteHolder(view: View) : RecyclerView.ViewHolder(view) {
        var ivContent: ImageView = view.findViewById(R.id.iv_icon)
        var tvName: TextView = view.findViewById(R.id.name)
        var mainItem: CardView = view.findViewById(R.id.main_item)
    }
}
