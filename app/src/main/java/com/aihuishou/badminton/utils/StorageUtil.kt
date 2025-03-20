package com.aihuishou.badminton.utils

import com.aihuishou.badminton.data.MatchData
import com.aihuishou.badminton.data.NameAndPoint
import com.google.gson.JsonSyntaxException

object StorageUtil {

    private const val MATCH_DATA_KEY = "match_data_key"

    private fun addMatchDataKeys(key: String) {
        val keys = getMatchDataKeys().toMutableList()
        if (!keys.contains(key)) {
            keys.add(key)
        }
        MMKVUtils.putString(MATCH_DATA_KEY, keys.joinToString(separator = "|"))
    }

    fun getMatchDataKeys(): List<String> {
        return MMKVUtils.getString(MATCH_DATA_KEY).split("|")
    }

    fun saveMatchData(day: String, data: MatchData) {
        MMKVUtils.putString(day, GsonUtils.toJsonString(data))
        addMatchDataKeys(day)
    }

    fun getMatchDataByDay(day: String): MatchData? {
        val strData = MMKVUtils.getString(day)
        return try {
            GsonUtils.parseJson(strData, MatchData::class.java)
        } catch (e: JsonSyntaxException) {
            e.printStackTrace()
            null
        }
    }

    fun savePlayerPoints(players: List<NameAndPoint>) {
        MMKVUtils.putString("players", players.map { GsonUtils.toJsonString(it) }.joinToString(separator = "|"))
    }

    fun updatePlayerPoints(players: List<Pair<String, Int>>): Int {
        val oldPlayers = getPlayerPoints().toMutableList()
        var updateCount = 0
        players.forEach { newPlayer ->
            val player = oldPlayers.firstOrNull { it.name == newPlayer.first }
            player?.let {
                oldPlayers.remove(it)
                oldPlayers.add(it.copy(point = newPlayer.second))
                updateCount ++
            }
        }
        savePlayerPoints(oldPlayers)
        return updateCount
    }

    fun getPlayerPoints(): List<NameAndPoint> {
        return MMKVUtils.getString("players").split("|")
            .map {
                try {
                    GsonUtils.parseJson(it, NameAndPoint::class.java)
                } catch (e: JsonSyntaxException) {
                    e.printStackTrace()
                    null
                }
             }
            .filterNotNull()
    }
}