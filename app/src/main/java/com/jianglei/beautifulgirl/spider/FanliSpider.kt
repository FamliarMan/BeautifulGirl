package com.jianglei.beautifulgirl.spider

import android.accounts.NetworkErrorException
import android.util.Log
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.data.WebService
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**饭粒动态图网站蜘蛛
 * @author jianglei on 1/2/19.
 */
class FanliSpider : DataSource {
    override fun fetDetailPictures(url:String ,page:Int,listener: OnDataResultListener<MutableList<String>>) {
        val  call = RetrofitManager.retrofit.create(WebService::class.java)
            .fetchDetailList(url.replace(".html", "_$page.html"))
        call.enqueue(object:Callback<ResponseBody>{
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val html =  response.body()?.string()
                    listener.onSuccess(analyzeDetail(html))
                }else if(response.code() == 404){
                    listener.onSuccess(ArrayList<String>())
                }else{
                    val msg  =response.errorBody()?.string()
                    listener.onError("Network error")
                }
                response.body()?.close()
            }

        })
    }

    override fun  fetchTitles(page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>) {

        startClaw(page,
            object : SpiderResultListener<MutableList<PictureTitleVo>> {
                override fun success(result: MutableList<PictureTitleVo>) {
                    listener.onSuccess(result )
                }

                override fun error(t: Throwable) {
                    listener.onError(t.toString())
                }
            })
    }

    private fun analyzeDetail(html:String? ):MutableList<String>{
        val document = Jsoup.parse(html)
        val articleContent = document.getElementsByClass("article-content")[0]
        val images = articleContent.getElementsByTag("img")
        var res = ArrayList<String>()
        for(image in images){
            res.add(image.attr("src"))
        }
        res.forEach {
            Log.d("jianglei",it)
        }
        return res
    }

//    override fun fetchTitles(page: Int, listener: OnDataResultListener<MutableList<PictureTitleVo>>) {
//        startClaw(page,
//            object : SpiderResultListener<MutableList<PictureTitleVo>> {
//                override fun success(result: MutableList<PictureTitleVo>) {
//                    listener.onSuccess(result)
//                }
//
//                override fun error(t: Throwable) {
//                    listener.onError(t.toString())
//                }
//            })
//    }

    fun startClaw(page: Int, listener: SpiderResultListener<MutableList<PictureTitleVo>>) {
        RetrofitManager.retrofit.create(WebService::class.java)
            .fetchFanli(page)
            .enqueue(object : Callback<ResponseBody> {
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.error(t)
                }

                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.code() == 404) {
                        //说明没有更多数据
                        listener.success(ArrayList())
                        response.body()?.close()
                        return
                    }
                    if (!response.isSuccessful) {
                        listener.error(NetworkErrorException("Network error"))
                        response.body()?.close()
                        return
                    }
                    val res: MutableList<PictureTitleVo> = ArrayList()
                    val doc = Jsoup.parse(response.body()?.string())
                    val articles = doc.select("article")
                    try {
                        for (article in articles) {
                            val desc= article.getElementsByClass("note")[0].text()
                            val focus = article.getElementsByClass("focus")[0]
                            val path = focus.select("a[href]")[0].attr("href")
                            val cover = focus.select("img")[0].attr("data-original")
                            val title= article.getElementsByTag("h2")[0]
                                .getElementsByTag("a")[0].text()
                            res.add(PictureTitleVo(title, desc,path, cover))
                        }
                        listener.success(res)
                    } catch (e: Exception) {
                        listener.error(e)
                    }finally {
                        response.body()?.close()
                    }
                }


            })
    }

    private fun clawDetail(){

    }

}

