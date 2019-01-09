package utils

import java.util.*


/**
 * @author jianglei on 1/9/19.
 */
class IpUtils {
    companion object {

        private val range0:Array<Int> = arrayOf(607649792, 608174079)//36.56.0.0-36.63.255.255
        private val range1:Array<Int> = arrayOf(1038614528, 1039007743)//61.232.0.0-61.237.255.255
        private val range2:Array<Int> = arrayOf(1783627776, 1784676351)//106.80.0.0-106.95.255.255
        private val range3:Array<Int> = arrayOf(2035023872, 2035154943)//121.76.0.0-121.77.255.255
        private val range4:Array<Int> = arrayOf(2078801920, 2079064063)//123.232.0.0-123.235.255.255
        private val range5:Array<Int> = arrayOf(-1950089216, -1948778497)//139.196.0.0-139.215.255.255
        private val range6:Array<Int> = arrayOf(-1425539072, -1425014785)//171.8.0.0-171.15.255.255
        private val range7:Array<Int> = arrayOf(-1236271104, -1235419137)//182.80.0.0-182.92.255.255
        private val range8:Array<Int> = arrayOf(-770113536, -768606209)//210.25.0.0-210.47.255.255
        private val range9:Array<Int> = arrayOf(-569376768, -564133889) //222.16.0.0-222.95.255.255
        fun getRandomIp(): String {
            //ip范围
            val range:Array<Array<Int>> = arrayOf(
                range0, range1, range2, range3, range4
                , range5, range6, range7, range8, range9
            )
            val rdint = Random()
            val index = rdint.nextInt(10)
            val ip = num2ip (range[index][0] + Random ().nextInt(range[index][1] - range[index][0]));
            return ip;
        }

        fun num2ip(ip: Int): String {
            val b: Array<Int> = arrayOf(
                (ip shr 24) and 0xff,
                (ip shr 16) and 0xff,
                (ip shr 8) and 0xff,
                ip and 0xff
            )

            val x = Integer.toString(b[0]) + "." + Integer.toString(b[1]) + "." + Integer.toString(b[2]) + "." +
                    Integer.toString(b[3])

            return x
        }
    }
}