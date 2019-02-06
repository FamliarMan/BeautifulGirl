package com.jianglei.beautifulgirl.data

import com.gargoylesoftware.htmlunit.*
import com.gargoylesoftware.htmlunit.util.NameValuePair
import okhttp3.ResponseBody
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import retrofit2.Call
import java.io.*

/**
 * @author jianglei on 2/6/19.
 */
class OkHttpWebConnection(val webClient: WebClient) : WebConnection {
    private var call: Call<ResponseBody>? = null
    override fun close() {
        call?.cancel()
    }

    override fun getResponse(request: WebRequest?): WebResponse {
        call = RetrofitManager.retrofit.create(WebService::class.java)
            .fetchHtmlFromWebsite(request!!.url.toString(), request.additionalHeaders)
        val start = System.currentTimeMillis()
        val body = call!!.execute()
        var downloadedContent: DownloadedContent
        if (body.isSuccessful) {
            downloadedContent = downloadContent(
                body
                    .body()!!.byteStream(), webClient.options.maxInMemory
            )
        } else {
            downloadedContent = downloadContent(
                body
                    .errorBody()!!.byteStream(), webClient.options.maxInMemory
            )
        }

        val headers = ArrayList<NameValuePair>()
        for (i in 0 until body.headers().size()) {
            headers.add(NameValuePair(body.headers().name(i), body.headers().value(i)))
        }
        val webResponseBody = WebResponseData(
            downloadedContent, body.code(), body.message(),
            headers
        )
        val loadTime = System.currentTimeMillis() - start
        return WebResponse(webResponseBody, request, loadTime)
    }

    public fun downloadContent(ism: InputStream, maxInMemory: Int): DownloadedContent {

        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)

        var nbRead = 0
        while ({nbRead=ism.read(buffer);nbRead}() != -1) {
            bos.write(buffer, 0, nbRead)
            if (bos.size() > maxInMemory) {
                // we have exceeded the max for memory, let's write everything to a temporary file
                val file = createTempFile("htmlunit", ".tmp")
                file.deleteOnExit()
                val fos = FileOutputStream(file)
                bos.writeTo(fos) // what we have already read
                IOUtils.copyLarge(ism, fos) // what remains from the server response
                return OnFile(file, true)
            }
        }
        return InMemory(bos.toByteArray())

    }


    class InMemory(val bytes_: ByteArray) : DownloadedContent {


        override fun getInputStream(): InputStream {
            return ByteArrayInputStream(bytes_)
        }

        override fun cleanUp() {
        }

        override fun isEmpty(): Boolean {
            return length() == 0L
        }

        override fun length(): Long {
            return bytes_.size.toLong()
        }
    }

    class OnFile(var file_: File?, var temporary_: Boolean) : DownloadedContent {

        override fun length(): Long {
            if (file_ == null) {
                return 0
            }
            return file_!!.length()
        }

        override fun cleanUp() {
            if (temporary_) {
                FileUtils.deleteQuietly(file_)
            }
        }

        override fun isEmpty(): Boolean {
            return false
        }

        override fun getInputStream(): InputStream {
            return FileInputStream(file_)
        }

    }
}