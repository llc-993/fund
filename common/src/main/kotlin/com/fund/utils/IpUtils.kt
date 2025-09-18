package com.fund.utils



import java.net.InetAddress
import java.net.UnknownHostException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

object IpUtils {

    fun getIpAddr(): String? {
        val attributes =
            RequestContextHolder.getRequestAttributes() as ServletRequestAttributes?
        return getIpAddr(attributes!!.request)
    }

    fun getIpAddr(request: HttpServletRequest?): String? {
        if (request == null) {
            return "unknown"
        }
        var ip = request.getHeader("x-forwarded-for")
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("X-Forwarded-For")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("X-Real-IP")
        }
        if (ip == null || ip.length == 0 || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }
        return if ("0:0:0:0:0:0:0:1" == ip) "127.0.0.1" else ip
    }

    fun internalIp(ip: String): Boolean {
        val addr = textToNumericFormatV4(ip)
        return internalIp(addr) || "127.0.0.1" == ip
    }

    private fun internalIp(addr: ByteArray?): Boolean {
        if (addr!!.size < 2) {
            return true
        }
        val b0 = addr[0]
        val b1 = addr[1]
        // 10.x.x.x/8
        val SECTION_1: Byte = 0x0A
        // 172.16.x.x/12
        val SECTION_2 = 0xAC.toByte()
        val SECTION_3 = 0x10.toByte()
        val SECTION_4 = 0x1F.toByte()
        // 192.168.x.x/16
        val SECTION_5 = 0xC0.toByte()
        val SECTION_6 = 0xA8.toByte()
        return when (b0) {
            SECTION_1 -> true
            SECTION_2 -> {
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true
                }
                when (b1) {
                    SECTION_6 -> return true
                }
                false
            }
            SECTION_5 -> {
                when (b1) {
                    SECTION_6 -> return true
                }
                false
            }
            else -> false
        }
    }

    /**
     * 将IPv4地址转换成字节
     *
     * @param text IPv4地址
     * @return byte 字节
     */
    fun textToNumericFormatV4(text: String): ByteArray? {
        if (text.length == 0) {
            return null
        }
        val bytes = ByteArray(4)
        val elements = text.split("\\.").dropLastWhile { it.isEmpty() }.toTypedArray()
        try {
            var l: Long
            var i: Int
            when (elements.size) {
                1 -> {
                    l = elements[0].toLong()
                    if (l < 0L || l > 4294967295L) {
                        return null
                    }
                    bytes[0] = (l shr 24 and 0xFF).toInt().toByte()
                    bytes[1] = (l and 0xFFFFFF shr 16 and 0xFF).toInt().toByte()
                    bytes[2] = (l and 0xFFFF shr 8 and 0xFF).toInt().toByte()
                    bytes[3] = (l and 0xFF).toInt().toByte()
                }
                2 -> {
                    l = elements[0].toInt().toLong()
                    if (l < 0L || l > 255L) {
                        return null
                    }
                    bytes[0] = (l and 0xFF).toInt().toByte()
                    l = elements[1].toInt().toLong()
                    if (l < 0L || l > 16777215L) {
                        return null
                    }
                    bytes[1] = (l shr 16 and 0xFF).toInt().toByte()
                    bytes[2] = (l and 0xFFFF shr 8 and 0xFF).toInt().toByte()
                    bytes[3] = (l and 0xFF).toInt().toByte()
                }
                3 -> {
                    i = 0
                    while (i < 2) {
                        l = elements[i].toInt().toLong()
                        if (l < 0L || l > 255L) {
                            return null
                        }
                        bytes[i] = (l and 0xFF).toInt().toByte()
                        ++i
                    }
                    l = elements[2].toInt().toLong()
                    if (l < 0L || l > 65535L) {
                        return null
                    }
                    bytes[2] = (l shr 8 and 0xFF).toInt().toByte()
                    bytes[3] = (l and 0xFF).toInt().toByte()
                }
                4 -> {
                    i = 0
                    while (i < 4) {
                        l = elements[i].toInt().toLong()
                        if (l < 0L || l > 255L) {
                            return null
                        }
                        bytes[i] = (l and 0xFF).toInt().toByte()
                        ++i
                    }
                }
                else -> return null
            }
        } catch (e: NumberFormatException) {
            return null
        }
        return bytes
    }

    fun getHostIp(): String? {
        try {
            return InetAddress.getLocalHost().hostAddress
        } catch (e: UnknownHostException) {
        }
        return "127.0.0.1"
    }

    fun getHostName(): String? {
        try {
            return InetAddress.getLocalHost().hostName
        } catch (e: UnknownHostException) {
        }
        return "未知"
    }

    fun getClinetIpByReq(request: HttpServletRequest): String? {
        // 获取客户端ip地址
        var clientIp = request.getHeader("x-forwarded-for")
        if (clientIp == null || clientIp.length == 0 || "unknown".equals(clientIp, ignoreCase = true)) {
            clientIp = request.getHeader("Proxy-Client-IP")
        }
        if (clientIp == null || clientIp.length == 0 || "unknown".equals(clientIp, ignoreCase = true)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP")
        }
        if (clientIp == null || clientIp.length == 0 || "unknown".equals(clientIp, ignoreCase = true)) {
            clientIp = request.remoteAddr
        }
        /*
         * 对于获取到多ip的情况下，找到公网ip.
         */
        var sIP: String? = null
        if (clientIp != null && !clientIp.contains("unknown") && clientIp.indexOf(",") > 0) {
            val ipsz = clientIp.split(",").toTypedArray()
            for (anIpsz in ipsz) {
                if (!isInnerIP(anIpsz.trim { it <= ' ' })) {
                    sIP = anIpsz.trim { it <= ' ' }
                    break
                }
            }
            /*
             * 如果多ip都是内网ip，则取第一个ip.
             */if (null == sIP) {
                sIP = ipsz[0].trim { it <= ' ' }
            }
            clientIp = sIP
        }
        if (clientIp != null && clientIp.contains("unknown")) {
            clientIp = clientIp.replace("unknown,".toRegex(), "")
            clientIp = clientIp.trim { it <= ' ' }
        }
        if ("" == clientIp || null == clientIp) {
            clientIp = "127.0.0.1"
        }
        return clientIp
    }

    /**
     * 判断IP是否是内网地址
     * @param ipAddress ip地址
     * @return 是否是内网地址
     */
    fun isInnerIP(ipAddress: String): Boolean {
        val isInnerIp: Boolean
        val ipNum = getIpNum(ipAddress)

        /**
         * 私有IP：A类  10.0.0.0-10.255.255.255
         * B类  172.16.0.0-172.31.255.255
         * C类  192.168.0.0-192.168.255.255
         * 当然，还有127这个网段是环回地址
         */
        val aBegin = getIpNum("10.0.0.0")
        val aEnd = getIpNum("10.255.255.255")
        val bBegin = getIpNum("172.16.0.0")
        val bEnd = getIpNum("172.31.255.255")
        val cBegin = getIpNum("192.168.0.0")
        val cEnd = getIpNum("192.168.255.255")
        isInnerIp = (isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd)
                || ipAddress == "127.0.0.1")
        return isInnerIp
    }

    private fun getIpNum(ipAddress: String): Long {
        val ip = ipAddress.split("\\.").toTypedArray()
        val a = ip[0].toInt().toLong()
        val b = ip[1].toInt().toLong()
        val c = ip[2].toInt().toLong()
        val d = ip[3].toInt().toLong()
        return a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d
    }

    private fun isInner(userIp: Long, begin: Long, end: Long): Boolean {
        return userIp >= begin && userIp <= end
    }

    fun getRealIP(request: HttpServletRequest): String? {
        // 获取客户端ip地址
        var clientIp = request.getHeader("x-forwarded-for")
        if (clientIp == null || clientIp.length == 0 || "unknown".equals(clientIp, ignoreCase = true)) {
            clientIp = request.remoteAddr
        }
        val clientIps = clientIp!!.split(",").toTypedArray()
        if (clientIps.size <= 1) return clientIp.trim { it <= ' ' }

        // 判断是否来自CDN
        if (isComefromCDN(request)) {
            if (clientIps.size >= 2) return clientIps[clientIps.size - 2].trim { it <= ' ' }
        }
        return clientIps[clientIps.size - 1].trim { it <= ' ' }
    }

    private fun isComefromCDN(request: HttpServletRequest): Boolean {
        val host = request.getHeader("host")
        return host.contains("www.189.cn") || host.contains("shouji.189.cn") || host.contains(
            "image2.chinatelecom-ec.com"
        ) || host.contains(
            "image1.chinatelecom-ec.com"
        )
    }
}
