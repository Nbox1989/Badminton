package com.aihuishou.badminton.ext

fun Int?.orZero(): Int {
    return this ?: 0
}