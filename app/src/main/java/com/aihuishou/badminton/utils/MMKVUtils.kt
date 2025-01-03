package com.aihuishou.badminton.utils

import android.os.Parcelable
import com.tencent.mmkv.MMKV

/**
 * Title:  类说明
 * Author: chao
 * Date: 2021/9/16
 */
object MMKVUtils {

    val mmkv = MMKV.defaultMMKV()

    fun putBoolean(key: String, value: Boolean) {
        mmkv.encode(key, value)
        mmkv.clearMemoryCache()
    }

    fun getBoolean(key: String, default: Boolean = false): Boolean {
        return mmkv.decodeBool(key, default)
    }

    fun putString(key: String, value: String) {
        mmkv.encode(key, value)
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return mmkv.decodeString(key, defaultValue) ?: ""
    }

    fun putInt(key: String, value: Int) {
        mmkv.encode(key, value)
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return mmkv.decodeInt(key, defaultValue)
    }

    fun putLong(key: String, value: Long) {
        mmkv.encode(key, value)
    }

    fun getLong(key: String, defaultValue: Long = 0): Long {
        return mmkv.decodeLong(key, defaultValue)
    }

    fun putParcelable(key: String, clazz: Parcelable?) {
        clazz?.let {
            mmkv.encode(key, it)
        }
    }

    fun <T : Parcelable> getParcelable(key: String, tClass: Class<T>): T? {
        return mmkv.decodeParcelable(key, tClass)
    }

    fun remove(key: String) {
        mmkv.removeValueForKey(key)
    }
}
