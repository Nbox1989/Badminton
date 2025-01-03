package com.aihuishou.badminton.data

data class MatchData(
    val teams: Map<Int, Team>?,
    val gameRecords: Map<String, GameRecord>?,
)
