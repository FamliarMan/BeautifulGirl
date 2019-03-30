package utils

import android.os.Handler
import android.os.Looper
import com.jianglei.beautifulgirl.storage.OnAsyncUtilListener
import java.util.concurrent.Executors

/**
 * @author jianglei on 3/30/19.
 */
class ThreadUtils {
    companion object {

        val excutorService = Executors.newCachedThreadPool()
        val mainHandler = Handler(Looper.getMainLooper())
        fun <E> asyncOprate(listener: OnAsyncUtilListener<E>) {
            excutorService.execute {
                val res = listener.onChildThread()
                mainHandler.post {
                    listener.onMainThread(res)
                }
            }

        }
    }
}