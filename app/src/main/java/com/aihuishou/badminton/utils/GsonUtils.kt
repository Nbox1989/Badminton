package com.aihuishou.badminton.utils

import com.google.gson.Gson
import java.lang.reflect.Type

/**
 * Title:  类说明
 * Author: chao
 * Date: 2021/10/22
 */
object GsonUtils {

    private val gson = Gson()

    @JvmStatic
    fun <T> parseJson(json: String, clazz: Class<T>): T {
        return gson.fromJson(json, clazz)
    }

    @JvmStatic
    fun <T> parseJson(json: String, type: Type): T {
        return gson.fromJson(json, type)
    }

    @JvmStatic
    fun toJsonString(any: Any): String {
        return gson.toJson(any)
    }
}
