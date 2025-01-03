package com.aihuishou.badminton.data

data class Team(
    val player1: Player,
    val player2: Player,
) {
    fun displayName(): String {
        return "${player1.name}&${player2.name}"
    }

    fun calTeamAvgPoint(): Int {
        /** 向上取整 **/
        return ((player1.point?: 1000) + (player2.point?: 1000) + 1) /2
    }

    companion object {
        fun createDefaultTeam(): List<Team> {
            val playerList = Player.createDefaultPlayerList()
            return listOf(
                Team(
                    playerList[0],
                    playerList[1],
                ),
                Team(
                    playerList[2],
                    playerList[3],
                ),
                Team(
                    playerList[4],
                    playerList[5],
                ),
                Team(
                    playerList[6],
                    playerList[7],
                ),
                Team(
                    playerList[8],
                    playerList[9],
                ),
                Team(
                    playerList[10],
                    playerList[11],
                ),
                Team(
                    playerList[12],
                    playerList[13],
                ),
                Team(
                    playerList[14],
                    playerList[15],
                ),
            )
        }
    }
}
