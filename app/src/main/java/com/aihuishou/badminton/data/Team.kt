package com.aihuishou.badminton.data

data class Team(
    val player1: Player,
    val player2: Player,
) {
    fun displayName(): String {
        return "${player1.name}&${player2.name}"
    }

    fun calTeamAvgPoint(): Int? {
        val matchPoint1 = player1.calMatchPoint()
        val matchPoint2 = player2.calMatchPoint()
        return if (matchPoint1 == null || matchPoint2 == null) {
            null
        } else {
            /** 向上取整 **/
            (matchPoint1 + matchPoint2 + 1) / 2
        }
    }
}
