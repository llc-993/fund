package com.fund.utils

import com.warrenstrange.googleauth.GoogleAuthenticator
import java.lang.StringBuffer
import java.lang.Exception
import java.lang.RuntimeException
import java.net.URLEncoder

/**
 * 谷歌验证码util
 */
object GaUtil {
    /**
     * 验证
     * @param secret
     * @param code
     * @return
     */
    fun auth(secret: String?, code: String): Boolean {
        val gAuth = GoogleAuthenticator()
        return gAuth.authorize(secret, code.toInt()) // 驗證
    }

    /**
     * 生成谷歌密钥
     * @return
     */
    fun createSecret(): String {
        val gAuth = GoogleAuthenticator()
        val key = gAuth.createCredentials()
        return key.key
    }

    /**
     * 生成谷歌密钥二维码页面
     *
     * @return [String] 出参释义
     * @author lemon
     * @since 2023 -01-20 15:52:20
     */
    fun createSecretQrCode(): String {
        val secret = createSecret()
        return createSecretQrCode(secret)
    }

    /**
     * 生成谷歌密钥二维码页面
     *
     * @return [String] 出参释义
     * @author lemon
     * @since 2023 -01-20 15:52:20
     */
    fun createSecretQrCode(secret: String): String {
        // String qrCodeData = String.format(googleAuthenticatorKeyUriFormat, otpType, account, secret);
        val qrCodeData = spawnScanQRString("gaomaoge.com", "lemon", secret)
        val googleChartsQrCodeFormat = "https://www.google.com/chart?chs=200x200&cht=qr&chl=%s"

        return String.format(googleChartsQrCodeFormat, qrCodeData)
    }

    private fun spawnScanQRString(title: String, account: String, secretKey: String): String {
        return try {
            val sb = StringBuffer("otpauth://totp/")
            sb.append(URLEncoder.encode("$title:$account", "UTF-8").replace("+", "%20"))
            sb.append("?secret=")
            sb.append(URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20"))
            sb.append("&issuer=")
            sb.append(URLEncoder.encode(title, "UTF-8").replace("+", "%20"))
            sb.toString()
        } catch (e: Exception) {
            throw RuntimeException(e.message)
        }
    }
}
