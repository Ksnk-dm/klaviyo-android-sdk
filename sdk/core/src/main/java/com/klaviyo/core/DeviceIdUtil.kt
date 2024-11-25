package com.klaviyo.core

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.Locale

object DeviceIdUtil {

    private const val SALT = "salt"

    fun getSecretKey(context: Context): String {
        val short = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 + Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 + Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 + Build.USER.length % 10 //13 digits
        val advId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val wm = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val macAddress = wm.connectionInfo.macAddress
        var longID = short + advId + macAddress;
        longID += SALT + longID.length * SALT.length
        var m: MessageDigest? = null
        try {
            m = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        m!!.update(longID.toByteArray(), 0, longID.length)
        val digest = m.digest()
        var uniqueID = ""
        for (i in digest.indices) {
            val b = 0xFF and digest[i].toInt()
            if (b <= 0xF) uniqueID += "0"
            uniqueID += Integer.toHexString(b)
        }
        return uniqueID.uppercase(Locale.getDefault())
    }
}