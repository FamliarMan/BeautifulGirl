package utils

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.JsonParser
import java.lang.reflect.GenericSignatureFormatError

/**
 * @author jianglei on 1/25/19.
 */
class JsonUtils {
    companion object {
        private val gson =Gson()
        /**
         * 将Json数据解析成相应的映射对象
         */
        public fun <T> parseJsonWithGson(jsonData: String, type: Class<T>): T? {
            var result: T? = null
            if (!TextUtils.isEmpty(jsonData)) {
                try {
                    result = gson.fromJson(jsonData, type)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return result
        }

        /**
         * 将Json数组解析成相应的映射对象List
         */
        public fun <T> parseJsonArrayWithGson(jsonData: String, type: Class<T>): List<T>? {
            var result: List<T>? = null
            if (!TextUtils.isEmpty(jsonData)) {
                try {

                    val parser = JsonParser()
                    val jArray = parser.parse(jsonData).asJsonArray
                    if (jArray != null) {
                        result = ArrayList<T>()
                        for (obj in jArray) {
                            try {
                                val cse = gson.fromJson(obj, type)
                                result.add(cse)
                            } catch (e:Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } catch (e:java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            return result;
        }

        public fun toJsonString(obj: Any):String{
            return gson.toJson(obj)
        }
    }
}