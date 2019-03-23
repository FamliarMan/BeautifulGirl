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
    val caoliu = "{\n" +
            "  \"type\": \"video\",\n" +
            "  \"name\": \"草榴\",\n" +
            "  \"icon\": \"\",\n" +
            "  \"encoding\": \"GBK\",\n" +
            "  \"categoryRule\": {\n" +
            "    \"url\": \"https://www.t66y.com/index.php\",\n" +
            "    \"nameRule\": \"@class:<tr3 f_one> -> @label:<h2> ->@label:<a> ->@hasText:<新時代的我們,達蓋爾的旗幟>-> @text\",\n" +
            "    \"targetUrlRule\": \"@class:<tr3 f_one> -> @label:<h2> ->@label:<a> ->@hasText:<新時代的我們,達蓋爾的旗幟>-> @property:<href>\"\n" +
            "  },\n" +
            "  \"coverRule\": {\n" +
            "    \"nameRule\": \"@class:<tal>[0]->@label:<h3>[0]->@label:<a>[0]->@text\",\n" +
            "    \"descRule\": \"@class:<tal>[0]->@text\",\n" +
            "    \"targetUrlRule\": \"@class:<tal>[0]->@label:<h3>[0]->@label:<a>[0]->@property:<href>\"\n" +
            "  },\n" +
            "  \"contentRule\": {\n" +
            "    \"detailRule:\": \"@label:<input>->@property:<data-src>\"\n" +
            "  }\n" +
            "}\n"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val webRule = JsonUtils.parseJsonWithGson(caoliu, WebRule::class.java)
        btnStart.setOnClickListener {
            val webStrategy = WebStrategy(webRule!!)
            webStrategy.fetchAllCategory(this@MainActivity, object : OnDataResultListener<List<Category>> {
                override fun onSuccess(data: List<Category>) {
                }

                override fun onError(msg: String) {
                    ToastUtils.showMsg(this@MainActivity, msg)
                }
            })
        }
    }
}
