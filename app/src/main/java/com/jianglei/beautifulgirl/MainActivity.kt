package com.jianglei.beautifulgirl

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.rule.WebRule
import com.jianglei.beautifulgirl.rule.WebStrategy
import com.jianglei.beautifulgirl.vo.Category
import kotlinx.android.synthetic.main.activity_main.*
import utils.JsonUtils
import utils.ToastUtils

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webRule = JsonUtils.parseJsonWithGson()
        btnStart.setOnClickListener{
            val webStrategy = WebStrategy(webRule)
            webStrategy.fetchAllCategory(this@MainActivity,object:OnDataResultListener<MutableList<Category>>{
                override fun onSuccess(data: MutableList<Category>) {
                    for(categor in data){
                        Log.d("longyi",categor.title+" "+categor.url+" "+categor.coverUrl)
                    }
                }

                override fun onError(msg: String) {
                    ToastUtils.showMsg(this@MainActivity,msg)
                }
            })
        }
    }
}
