package com.jianglei.beautifulgirl.spider

import android.accounts.NetworkErrorException
import android.util.Log
import com.jianglei.beautifulgirl.data.DataSource
import com.jianglei.beautifulgirl.data.OnDataResultListener
import com.jianglei.beautifulgirl.data.RetrofitManager
import com.jianglei.beautifulgirl.data.WebService
import com.jianglei.beautifulgirl.vo.Category
import com.jianglei.beautifulgirl.vo.ContentTitle
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**饭粒动态图网站蜘蛛
 * @author jianglei on 1/2/19.
 */
class FanliSpider : DataSource {
    override fun fetAllTypes(
        homePageUrl: String,
        listener: OnDataResultListener<MutableList<Category>>,
        page:Int
    ) {
        RetrofitManager.retrofit
            .create(WebService::class.java)
            .fetchHtmlFromWebsite(homePageUrl)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (!response.isSuccessful) {
                        response.body()?.close()
                        listener.onError("Network Error")
                        return
                    }
                    val bodyStr = response.body()?.string()
                    listener.onSuccess(analyzeType(bodyStr))
                    response.body()?.close()
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    listener.onError(t.localizedMessage)
                }

            })

    }

    override fun fetDetailPictures(url: String, page: Int, listener: OnDataResultListener<MutableList<String>>) {
        val call = RetrofitManager.retrofit.create(WebService::class.java)
            .fetchHtmlFromWebsite(url.replace(".html", "_$page.html"))
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                listener.onError(t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                when {
                    response.isSuccessful -> {
                        val html = response.body()?.string()
                        listener.onSuccess(analyzeDetail(html))
                    }
                    response.code() == 404 -> listener.onSuccess(ArrayList())
                    else -> listener.onError("Network error")
                }
                response.body()?.close()
            }

        })
    }

    override fun fetchTitles(url: String, page: Int, listener: OnDataResultListener<MutableList<ContentTitle>>) {

        startClaw(url, page,
            object : SpiderResultListener<MutableList<ContentTitle>> {
                override fun success(result: MutableList<ContentTitle>) {
                    listener.onSuccess(result)
                }

                override fun error(t: Throwable) {
                    listener.onError(t.toString())
                }
            })
    }

    private fun analyzeDetail(html: String?): MutableList<String> {
        val document = Jsoup.parse(html)
        val articleContent = document.getElementsByClass("article-content")[0]
        val images = articleContent.getElementsByTag("img")
        val res = ArrayList<String>()
        for (image in images) {
            res.add(image.attr("src"))
        }
        res.forEach {
            Log.d("jianglei", it)
        }
        return res
    }

    private fun startClaw(url: String, page: Int, listener: SpiderResultListener<MutableList<ContentTitle>>) {
        RetrofitManager.retrofit.create(WebService::class.java)
            .fetchHtmlFromWebsite(url + "page/$page")
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
                    val res: MutableList<ContentTitle> = ArrayList()
                    val doc = Jsoup.parse(response.body()?.string())
                    val articles = doc.select("article")
                    try {
                        for (article in articles) {
                            val desc = article.getElementsByClass("note")[0].text()
                            val focus = article.getElementsByClass("focus")[0]
                            val path = focus.select("a[href]")[0].attr("href")
                            val cover = focus.select("img")[0].attr("data-original")
                            val title = article.getElementsByTag("h2")[0]
                                .getElementsByTag("a")[0].text()
                            res.add(ContentTitle(title, desc, path, cover))
                        }
                        listener.success(res)
                    } catch (e: Exception) {
                        listener.error(e)
                    } finally {
                        response.body()?.close()
                    }
                }


            })
    }

    private fun analyzeType(html: String?): MutableList<Category> {
        if (html == null) {
            return ArrayList()
        }
        val validType = setOf("动态图出处", "动态图片", "养眼美女")
        val document = Jsoup.parse(html)
        val lis = document.getElementsByClass("nav")[0]
            .getElementsByTag("li")
        val res = ArrayList<Category>()
        for (ls in lis) {
            val a = ls.getElementsByTag("a")
            if (a.size == 0) {
                continue
            }
            val pictureTypeVo = Category(a.text(), a.attr("href"))
            if (validType.contains(pictureTypeVo.title)) {
                res.add(pictureTypeVo)
            }
            Log.d("jianglei", pictureTypeVo.title + "  " + pictureTypeVo.url)

        }
        return res

    }

}

