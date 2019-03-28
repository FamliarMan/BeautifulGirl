package utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

/**
 * @author jianglei on 3/28/19.
 */
class ZipUtils {
    companion object {
        fun compress(str:String):String{
            if(str.isBlank()){
                return str
            }
            val out = ByteArrayOutputStream()
            val gzip = GZIPOutputStream(out)
            gzip.write(str.toByteArray())
            return out.toString("utf-8")
        }
        fun uncompress(str:String):String{
            val out = ByteArrayOutputStream()
            val input = ByteArrayInputStream(
                str.toByteArray(Charset.forName("utf-8")))
            val gzip = GZIPInputStream(input)
            val bytesBuffer = ByteArray(256)
            var len=0
            while(true){
                len = gzip.read(bytesBuffer)
                if(len == -1){
                    break
                }
                out.write(bytesBuffer,0,len)

            }
            return out.toString("utf-8")
        }

        @JvmStatic
        fun main(args: Array<String>) {
            val fanli = "{\"type\":\"image\",\"name\":\"饭粒邪恶网\",\"icon\":\"https://www.8mfh.com/wp-content/themes/xiu/images/logo.png\",\"encoding\":\"GBK\",\"url\":\"https://www.8mfh.com/\",\"supportSearch\":false,\"categoryRule\":{\"dynamicRender\":true,\"nameRule\":\"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @text\",\"urlRule\":\"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @property:<href>\"},\"coverRule\":{\"dynamicRender\":true,\"nameRule\":\"@label:<article>->@label:<h2>[0]->@label:<a>->@text\",\"descRule\":\"@label:<article> -> @class:<note>[0]->@text\",\"imageUrlRule\":\"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@label:<img>->@property:<src>\",\"urlRule\":\"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@property:<href>\",\"pageRule\":{\"fromHtml\":true,\"nextUrlRule\":\"@class:<pagination pagination-multi>->@label:<li>->@label:<a>[0]->@hasText:<{page}>->@property:<href>\"}},\"contentRule\":{\"dynamicRender\":true,\"detailRule\":\"@class:<article-content>[0]->@label:<img>->@property:<src>\",\"pageRule\":{\"fromHtml\":true,\"nextUrlRule\":\"@class:<article-paging>->@label:<a>->@hasText:<{page}>->@property:<href>\"}}}"
            val res = compress(fanli)
            println(res)

            println(uncompress(res))
        }
    }


}