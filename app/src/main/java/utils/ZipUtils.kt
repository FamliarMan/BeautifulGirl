package utils

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.Charset
import java.util.zip.*
import android.R.attr.end
import android.R.attr.end





/**
 * @author jianglei on 3/28/19.
 */
class ZipUtils {
    companion object {
//        fun compress(str:String):String{
//            if(str.isBlank()){
//                return str
//            }
//            val out = ByteArrayOutputStream()
//            val gzip = GZIPOutputStream(out)
//            gzip.write(str.toByteArray())
//            return out.toString("utf-8")
//        }
//        fun uncompress(str:String):String{
//            val out = ByteArrayOutputStream()
//            val input = ByteArrayInputStream(
//                str.toByteArray(Charset.forName("utf-8")))
//            val gzip = GZIPInputStream(input)
//            val bytesBuffer = ByteArray(256)
//            var len=0
//            while(true){
//                len = gzip.read(bytesBuffer)
//                if(len == -1){
//                    break
//                }
//                out.write(bytesBuffer,0,len)
//
//            }
//            return out.toString("utf-8")
//        }

        fun zip(str: String?): String? {
            if (str == null)
                return null
            var compressed: ByteArray?
            var out: ByteArrayOutputStream? = null
            var zout: ZipOutputStream? = null
            var compressedStr: String? = null
            try {
                out = ByteArrayOutputStream()
                zout = ZipOutputStream(out)
                zout.putNextEntry(ZipEntry("0"))
                zout.write(str.toByteArray())
                zout.closeEntry()
                compressed = out.toByteArray()
                compressedStr = Base64.encodeToString(compressed, Base64.NO_WRAP)
            } catch (e: IOException) {
                compressed = null
            } finally {
                if (zout != null) {
                    try {
                        zout.close()
                    } catch (e: IOException) {
                    }

                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (e: IOException) {
                    }

                }
            }
            return compressedStr
        }

        /**
         * 使用zip进行解压缩
         * @param compressed 压缩后的文本
         * @return 解压后的字符串
         */
        fun unzip(compressedStr: String?): String? {
            if (compressedStr == null) {
                return null
            }

            var out: ByteArrayOutputStream? = null
            var `in`: ByteArrayInputStream? = null
            var zin: ZipInputStream? = null
            var decompressed: String? = null
            try {
                val compressed = Base64.decode(compressedStr, Base64.DEFAULT)
                out = ByteArrayOutputStream()
                `in` = ByteArrayInputStream(compressed)
                zin = ZipInputStream(`in`)
                zin.getNextEntry()
                val buffer = ByteArray(1024)
                var offset = 0
                while (offset != -1) {
                    offset = zin.read(buffer)
                    out.write(buffer, 0, offset)
                }
                decompressed = out.toString()
            } catch (e: IOException) {
                decompressed = null
            } finally {
                if (zin != null) {
                    try {
                        zin.close()
                    } catch (e: IOException) {
                    }

                }
                if (`in` != null) {
                    try {
                        `in`.close()
                    } catch (e: IOException) {
                    }

                }
                if (out != null) {
                    try {
                        out.close()
                    } catch (e: IOException) {
                    }

                }
            }
            return decompressed
        }

        // 压缩
        fun zipString(unzip: String): String {
            val deflater = Deflater(9) // 0 ~ 9 压缩等级 低到高
            deflater.setInput(unzip.toByteArray())
            deflater.finish()

            val bytes = ByteArray(256)
            val outputStream = ByteArrayOutputStream(256)

            while (!deflater.finished()) {
                val length = deflater.deflate(bytes)
                outputStream.write(bytes, 0, length)
            }

            deflater.end()
            return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_PADDING)
        }

        fun unzipString(zip: String): String? {
            val decode = Base64.decode(zip, Base64.NO_PADDING)

            val inflater = Inflater()
            inflater.setInput(decode)

            val bytes = ByteArray(256)
            val outputStream = ByteArrayOutputStream(256)

            try {
                while (!inflater.finished()) {
                    val length = inflater.inflate(bytes)
                    outputStream.write(bytes, 0, length)
                }
            } catch (e: DataFormatException) {
                e.printStackTrace()
                return null
            } finally {
                inflater.end()
            }

            return outputStream.toString()
        }
        @JvmStatic
        fun main(args: Array<String>) {
            val fanli =
                "{\"type\":\"image\",\"name\":\"饭粒邪恶网\",\"icon\":\"https://www.8mfh.com/wp-content/themes/xiu/images/logo.png\",\"encoding\":\"GBK\",\"url\":\"https://www.8mfh.com/\",\"supportSearch\":false,\"categoryRule\":{\"dynamicRender\":true,\"nameRule\":\"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @text\",\"urlRule\":\"@class:<nav>[0]->@label:<li>->@label:<a> -> @hasText:<动态图出处,剧情动态图,动态图片,美女图集> -> @property:<href>\"},\"coverRule\":{\"dynamicRender\":true,\"nameRule\":\"@label:<article>->@label:<h2>[0]->@label:<a>->@text\",\"descRule\":\"@label:<article> -> @class:<note>[0]->@text\",\"imageUrlRule\":\"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@label:<img>->@property:<src>\",\"urlRule\":\"@label:<article>->@class:<focus>[0]->@label:<a>[0]->@property:<href>\",\"pageRule\":{\"fromHtml\":true,\"nextUrlRule\":\"@class:<pagination pagination-multi>->@label:<li>->@label:<a>[0]->@hasText:<{page}>->@property:<href>\"}},\"contentRule\":{\"dynamicRender\":true,\"detailRule\":\"@class:<article-content>[0]->@label:<img>->@property:<src>\",\"pageRule\":{\"fromHtml\":true,\"nextUrlRule\":\"@class:<article-paging>->@label:<a>->@hasText:<{page}>->@property:<href>\"}}}"
            val res = zipString(fanli)
            println(res)

            println(unzipString(res))
        }
    }


}