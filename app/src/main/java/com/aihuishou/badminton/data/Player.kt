package com.aihuishou.badminton.data

data class Player(
    val name: String,
    val point: Int?,
    /** 定级分  **/
    val gradingPoint: Int? = null,
) {
    companion object {
        fun createDefaultPlayerList(): List<Player> {
            val nameList = listOf(
                "张伟" to 899,
                "李丽娜" to 1289,
                "王桂芳" to 966,
                "刘洋" to 900,
                "陈静" to 989,
                "杨丽丽" to null,
                "赵敏" to 1008,
                "黄永杰" to 1223,
                "周强" to 999,
                "吴艳" to 1008,
                "徐雪峰" to null,
                "孙悦" to 960,
                "马永涛" to 1008,
                "朱莉叶" to 1111,
                "周渝民" to 960,
                "陈晨" to 1000,
            )
            return nameList.map {
                Player(it.first, it.second, null)
            }
        }
    }
}
