package com.aihuishou.badminton.enums

enum class EnumGameResult(val desc: String, val value: Int) {
    WIN("胜", 1),
    LOSS("负", -1),
    DRAW("平局", 0)
}