package com.aihuishou.badminton.data

import com.aihuishou.badminton.enums.EnumGameResult

data class GameRecord(
    val firstScore: Int,
    val secondScore: Int,
    /** 主队 **/
    val firstTeamIndex: Int,
    /** 客队 **/
    val secondTeamIndex: Int,
) {
    fun toOpponent(): GameRecord {
        return GameRecord(
            firstScore = this.secondScore,
            secondScore = this.firstScore,
            firstTeamIndex = this.secondTeamIndex,
            secondTeamIndex = this.firstTeamIndex,
        )
    }
}
