package com.aihuishou.badminton.data

data class Player(
    val name: String,
    val point: Int?,
    /** 定级分  **/
    var gradingPoint: Int? = null,
) {
    /** 参赛积分，积分->定级分->null **/
    fun calMatchPoint(): Int? {
        return point?: gradingPoint
    }
}
