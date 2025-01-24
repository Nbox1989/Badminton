package com.aihuishou.badminton.main

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aihuishou.badminton.data.GameRecord
import com.aihuishou.badminton.data.MatchData
import com.aihuishou.badminton.data.Player
import com.aihuishou.badminton.data.Team
import com.aihuishou.badminton.enums.EnumGameResult
import com.aihuishou.badminton.ext.orZero
import com.aihuishou.badminton.main.dialog.InsertGameRecordDialog
import com.aihuishou.badminton.main.dialog.SettingListener
import com.aihuishou.badminton.main.dialog.SettingsDialog
import com.aihuishou.badminton.main.dialog.TeamEditCallback
import com.aihuishou.badminton.main.dialog.TeamEditDialog
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.aihuishou.badminton.utils.StorageUtil
import com.blankj.utilcode.util.ImageUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale


class MainActivity : ComponentActivity() {

    companion object {
        private const val WIDTH_HEADER = 100
        private const val WIDTH_GAME_RECORD_MIN = 2
        private const val WIDTH_GAME_RECORD = 80
        private const val WIDTH_WIN_LOSS = 70
        private const val WIDTH_POINT_CHANGE = 70
        private const val WIDTH_POINT_AFTER = 100
    }

    private val viewModel: MainViewModel by viewModels()

    private var autoSaveFlag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setupStatusBar()

        initObservers()

        setContent {
            ComposeTheme {
                BadmintonGrid()
            }
        }

        initData()
    }

    private fun setupStatusBar() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    private fun initObservers() {
        viewModel.displayMatchDay.observe(this) {
            if (it != null) {
                autoSaveFlag = false
                restoreDataByDay(it)
                autoSaveFlag = true
            }
        }

        viewModel.teamMap.observe(this) {
            saveDataImmediately()
        }

        viewModel.gameRecordMap.observe(this) {
            saveDataImmediately()
        }
    }

    private fun initData() {
        viewModel.displayMatchDay.value = createMatchDataCacheKey()
    }

    private fun restoreDataByDay(day: String) {
        StorageUtil.getMatchDataByDay(day).let {
            viewModel.teamMap.value = it?.teams
            viewModel.gameRecordMap.value = it?.gameRecords
        }
    }

    private fun saveDataImmediately() {
        if (autoSaveFlag) {
            val matchData = MatchData(
                teams = viewModel.teamMap.value,
                gameRecords = viewModel.gameRecordMap.value
            )
            StorageUtil.saveMatchData(viewModel.displayMatchDay.value.orEmpty(), matchData)
        }
    }

    private fun createMatchDataCacheKey(): String{
        val date = Date()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA)
        return sdf.format(date)
    }

    private fun createDisplayTextOfPlayer(player: Player): AnnotatedString {
        return buildAnnotatedString {
            append(player.name)
            if (player.point != null) {
                append("(${player.point})")
            } else if (player.gradingPoint != null) {
                withStyle(style = SpanStyle(color = Color.Blue)) {
                    append("(${player.gradingPoint})")
                }
            } else {
                withStyle(style = SpanStyle(color = Color.Red)) {
                    append("(待定)")
                }
            }
        }
    }

    private fun onGameRecordClick(firstTeamIndex: Int, secondTeamIndex: Int) {
        val firstTeam = viewModel.teamMap.value?.get(firstTeamIndex)
        val secondTeam = viewModel.teamMap.value?.get(secondTeamIndex)
        if (firstTeam == null || secondTeam == null) {
            ToastUtils.showShort("对阵双方不完整！")
        } else {
            val gameRecordKey = generateGameRecordKey(
                firstTeamIndex,
                secondTeamIndex
            )
            val oldGameRecord = viewModel.gameRecordMap.value
                ?.get(gameRecordKey)
                ?: GameRecord(
                    firstScore = 0,
                    secondScore = 0,
                    firstTeamIndex = firstTeamIndex,
                    secondTeamIndex = secondTeamIndex,
                )
            InsertGameRecordDialog(this, oldGameRecord, firstTeam to secondTeam)
                .showDialog {
                    val newMap = HashMap(viewModel.gameRecordMap.value?: emptyMap())
                    newMap.put(gameRecordKey, it)
                    val opponentGameRecordKey = generateGameRecordKey(
                        secondTeamIndex,
                        firstTeamIndex,
                    )
                    newMap.put(opponentGameRecordKey, it.toOpponent())
                    viewModel.gameRecordMap.value = newMap
                }
        }
    }

    private fun onTeamClick(index: Int) {
        val teamMap = viewModel.teamMap.value
        val team = teamMap?.get(index)
        TeamEditDialog(this, team)
            .showDialog (
                object : TeamEditCallback {
                    override fun onTeamEditResult(newTeam: Team?) {
                        val newMap = HashMap(viewModel.teamMap.value?: emptyMap())
                        newMap.put(index, newTeam)
                        viewModel.teamMap.value = newMap
                        if (newTeam == null) {
                            clearGameRecordOfTeamIndex(index)
                        }
                    }

                    override fun onRequestGrade(team: Team) {
                        calTeamGradingPoints(index, team)
                    }

                }
            )
    }

    private fun onSettingClick() {
        SettingsDialog(this)
            .showDialog(object : SettingListener {
                override fun loadMatchData(key: String) {
                    viewModel.displayMatchDay.value = key
                }

                override fun onScreenShot() {
                    viewModel.screenShotTrigger.value = System.currentTimeMillis()
                }
            })
    }

    private fun calTeamGradingPoints(index: Int, team: Team) {
        val gameRecordMap = viewModel.gameRecordMap.value
        val teamMap = viewModel.teamMap.value

        val relatedGameRecords = gameRecordMap?.filter { it.key.startsWith("$index") }?.values
        if (teamMap?.size.orZero() <= 1) {
            ToastUtils.showShort("队伍数量不足")
        } else if (relatedGameRecords?.size.orZero() != teamMap?.size.orZero() - 1) {
            ToastUtils.showShort("还有未结束的比赛")
        } else {
            val winGame = relatedGameRecords?.filter { calGameResult(it) == EnumGameResult.WIN }
            val lossGame = relatedGameRecords?.filter { calGameResult(it) == EnumGameResult.LOSS }
            val highestWin = winGame?.map {
                val opponentTeam = teamMap?.get(it.secondTeamIndex)
                ((opponentTeam?.player1?.point?: 1000) + (opponentTeam?.player2?.point?: 1000) + 1) / 2
            }?.maxOfOrNull { it }
            val lowestLoss = lossGame?.map {
                val opponentTeam = teamMap?.get(it.secondTeamIndex)
                ((opponentTeam?.player1?.point ?: 1000) + (opponentTeam?.player2?.point ?: 1000) + 1) / 2
            }?.minOfOrNull { it }
            val teamGradingPoint = if (winGame?.size.orZero() == 0 ) {
                lowestLoss.orZero()
            } else if (lossGame?.size.orZero() == 0) {
                highestWin.orZero()
            } else {
                (lowestLoss.orZero() + highestWin.orZero() + 1) / 2
            }
            val playerPair = if (team.player1.point == null && team.player2.point == null) {
                team.player1.copy(gradingPoint = teamGradingPoint) to
                team.player2.copy(gradingPoint = teamGradingPoint)
            } else if(team.player1.point == null) {
                team.player1.copy(gradingPoint = teamGradingPoint * 2 - team.player2.point.orZero()) to
                team.player2
            } else {
                team.player1 to
                team.player2.copy(gradingPoint = teamGradingPoint * 2 - team.player1.point.orZero())
            }
            val newTeam = Team(
                player1 = playerPair.first,
                player2 = playerPair.second
            )
            val newTeamMap = HashMap(teamMap?: emptyMap())
            newTeamMap[index] = newTeam
            viewModel.teamMap.value = newTeamMap
            ToastUtils.showShort("${team.displayName()}定级分已更新")
        }
    }

    private fun clearGameRecordOfTeamIndex(teamIndex: Int) {
        val newMap = HashMap(viewModel.gameRecordMap.value?: emptyMap())
        val matchedKeys = newMap.keys.filter { it.contains(teamIndex.toString()) }
        matchedKeys.forEach {
            newMap.remove(it)
        }
        viewModel.gameRecordMap.value = newMap
    }

    private fun generateGameRecordKey(firstTeamIndex: Int, secondTeamIndex: Int): String {
        return "${firstTeamIndex}vs${secondTeamIndex}"
    }

    private fun calTeamMatchPointDiff(team1: Team, team2: Team): Int? {
        val avg1 = team1.calTeamAvgPoint()
        val avg2 = team2.calTeamAvgPoint()
        return if (avg1 == null || avg2 == null) {
            null
        } else {
            Math.abs(avg2 - avg1)
        }
    }

    private fun calGameResult(record: GameRecord): EnumGameResult {
        return if(record.firstScore == record.secondScore) {
            EnumGameResult.DRAW
        } else if(record.firstScore > record.secondScore) {
            EnumGameResult.WIN
        } else {
            EnumGameResult.LOSS
        }
    }

    private fun calTeamPointChange(pointDiff: Int, result: EnumGameResult, teams: Pair<Team, Team>): Int? {
        val firstTeam = teams.first
        val secondTeam = teams.second
        val firstAvg = firstTeam.calTeamAvgPoint()
        val secondAvg = secondTeam.calTeamAvgPoint()
        if (firstAvg == null || secondAvg == null) {
            return null
        } else {
            val pointMap = pointMap(pointDiff)
            return when (result) {
                EnumGameResult.DRAW -> 0
                EnumGameResult.WIN -> {
                    if (secondAvg < firstAvg) {
                        /** 赢，对方分低 **/
                        pointMap.first
                    } else {
                        /** 赢，对方分高 **/
                        pointMap.second
                    }
                }

                EnumGameResult.LOSS -> {
                    if (secondAvg < firstAvg) {
                        /** 败，对方分低 **/
                        -pointMap.second
                    } else {
                        /** 败，对方分高 **/
                        -pointMap.first
                    }
                }
            }
        }
    }

    private fun pointMap(pointDiff: Int): Pair<Int, Int> {
        return when(Math.abs(pointDiff)) {
            in 0..12 -> {
                8 to 8
            }
            in 13..37 -> {
                7 to 10
            }
            in 38..62 -> {
                6 to 13
            }
            in 63..87 -> {
                5 to 16
            }
            in 88..112 -> {
                4 to 20
            }
            in 113..137 -> {
                3 to 25
            }
            in 138..162 -> {
                2 to 30
            }
            in 163..187 -> {
                2 to 35
            }
            in 188..212 -> {
                1 to 40
            }
            in 213..237 -> {
                1 to 45
            }
            in 238..Int.MAX_VALUE -> {
                0 to 50
            }
            else -> {
                0 to 0
            }
        }
    }

    private fun saveBitmapToLocal(bitmap: ImageBitmap) {
//        val sdf = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.getDefault())
//        val timeStr = TimeUtils.getNowString(sdf)
//        val filePath = PathUtils.getExternalAppPicturesPath() + File.separator + "${timeStr}.jpg"
//        val isSuccess = ImageUtils.save(bitmap.asAndroidBitmap(), filePath, Bitmap.CompressFormat.JPEG, true)
        val file = ImageUtils.save2Album(bitmap.asAndroidBitmap(), Bitmap.CompressFormat.JPEG)
        ToastUtils.showShort(if (file != null) "保存成功" else "保存失败")
    }

    @Composable
    private fun BadmintonGrid() {
        val coroutineScope = rememberCoroutineScope()
        val graphicsLayer = rememberGraphicsLayer()
        val shotTrigger by viewModel.screenShotTrigger.observeAsState()
        LaunchedEffect(key1 = "shotTrigger is $shotTrigger") {
            coroutineScope.launch {
                if (shotTrigger != null) {
                    val bitmap = graphicsLayer.toImageBitmap()
                    saveBitmapToLocal(bitmap)
                }
            }
        }
        Row (
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .horizontalScroll(rememberScrollState())
        ) {
            Row (
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentWidth()
                    .drawWithContent {
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(graphicsLayer)
                    }
            ){
                repeat(12) {
                    BadmintonColumn(it)
                }
            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumn(column: Int) {
        when(column) {
            0 -> {
                BadmintonColumnHeader(column)
            }
            in 1 until 9 -> {
                BadmintonColumnGameScore(column)
            }
            9 -> {
                BadmintonColumnWinLoss(column)
            }
            10 -> {
                BadmintonColumnTeamPointChange(column)
            }
            11 -> {
                BadmintonColumnPlayerPointAfter(column)
            }
            else -> {

            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumnHeader(column: Int) {
        Column(
            modifier = Modifier
                .width(WIDTH_HEADER.dp)
                .fillMaxHeight()
        ) {
            repeat(9) {
                BadmintonItem(
                    row = it,
                    column = column,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            color = Color(0xff999999),
                            shape = RectangleShape,
                            width = 0.5.dp,
                        )
                )
            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumnGameScore(column: Int) {
        val teamMap by viewModel.teamMap.observeAsState()
        val teamCount = teamMap?.size.orZero()
        val width = if (column > teamCount) WIDTH_GAME_RECORD_MIN else WIDTH_GAME_RECORD
        Column(
            modifier = Modifier
                .width(width.dp)
                .fillMaxHeight()
        ) {
            repeat(9) {
                BadmintonItem(
                    row = it,
                    column = column,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            color = Color(0xff999999),
                            shape = RectangleShape,
                            width = 0.5.dp,
                        )
                )
            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumnWinLoss(column: Int) {
        BadmintonItemDivider()
        Column(
            modifier = Modifier
                .width(WIDTH_WIN_LOSS.dp)
                .fillMaxHeight()
        ) {
            repeat(9) {
                BadmintonItem(
                    row = it,
                    column = column,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            color = Color(0xff999999),
                            shape = RectangleShape,
                            width = 0.5.dp,
                        )
                )
            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumnTeamPointChange(column: Int) {
        Column(
            modifier = Modifier
                .width(WIDTH_POINT_CHANGE.dp)
                .fillMaxHeight()
        ) {
            repeat(9) {
                BadmintonItem(
                    row = it,
                    column = column,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            color = Color(0xff999999),
                            shape = RectangleShape,
                            width = 0.5.dp,
                        )
                )
            }
        }
    }

    @Composable
    private fun RowScope.BadmintonColumnPlayerPointAfter(column: Int) {
        Column(
            modifier = Modifier
                .width(WIDTH_POINT_AFTER.dp)
                .fillMaxHeight()
        ) {
            repeat(9) {
                BadmintonItem(
                    row = it,
                    column = column,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .border(
                            color = Color(0xff999999),
                            shape = RectangleShape,
                            width = 0.5.dp,
                        )
                )
            }
        }
    }

    @Composable
    private fun BadmintonItem(row: Int, column: Int, modifier: Modifier) {
        Box(
            modifier = modifier
                .padding(1.dp)
                .background(
                    color = if (row == 0) Color(0xffCCCCCC) else Color.White,
                )
        ) {
            if (row == 0) {
                when (column) {
                    0 -> {
                        BadmintonItemFunction()
                    }
                    in 1..8 -> {
                        BadmintonItemTeamTop(column - 1)
                    }
                    9 -> {
                        BadmintonItemTitle("胜负")
                    }
                    10 -> {
                        BadmintonItemTitle("积分变动")
                    }
                    11 -> {
                        BadmintonItemTitle("赛后积分")
                    }
                }
            } else {
                when (column) {
                    0 -> {
                        BadmintonItemTeamLeft(row - 1)
                    }
                    in 1..8 -> {
                        BadmintonItemGameRecord(
                            firstTeamIndex = row - 1,
                            secondTeamIndex = column - 1
                        )
                    }
                    9 -> {
                        BadmintonItemWinLoss(row - 1)
                    }
                    10 -> {
                        BadmintonItemPointChange(row - 1)
                    }
                    11 -> {
                        BadmintonItemPointAfter(row - 1)
                    }
                }
            }
        }
    }

    @Composable
    private fun BoxScope.BadmintonItemFunction() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onSettingClick()
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val displayDay by viewModel.displayMatchDay.observeAsState()
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = displayDay.orEmpty(),
                color = Color(0xff111111),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = "点此设置",
                color = Color.Blue,
                fontSize = 12.sp,
            )
            Spacer(modifier = Modifier.weight(1f))
        }
    }

    @Composable
    private fun BoxScope.BadmintonItemTeamTop(index: Int) {
        val teamMap by viewModel.teamMap.observeAsState()
        val team = teamMap?.get(index)
        team?.let {
            Column (
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Text(
                    text = createDisplayTextOfPlayer(team.player1),
                    color = Color(0xff333333),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = createDisplayTextOfPlayer(team.player2),
                    color = Color(0xff333333),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }

    @Composable
    private fun BoxScope.BadmintonItemTeamLeft(index: Int) {
        val teamMap by viewModel.teamMap.observeAsState()
        val team = teamMap?.get(index)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    onTeamClick(index)
                }
        ) {
            team?.let {
                Column (
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = createDisplayTextOfPlayer(team.player1),
                        color = Color(0xff333333),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = createDisplayTextOfPlayer(team.player2),
                        color = Color(0xff333333),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }

    @Composable
    private fun BadmintonItemGameRecord(firstTeamIndex: Int, secondTeamIndex: Int) {
        if (firstTeamIndex == secondTeamIndex) {
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        ToastUtils.showShort("不可点击")
                    }
                    .background(color = Color(0xff999999))
            )
        } else {
            val gameRecordMap by viewModel.gameRecordMap.observeAsState()
            val teamMap by viewModel.teamMap.observeAsState()
            val gameRecordKey = generateGameRecordKey(firstTeamIndex, secondTeamIndex)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        onGameRecordClick(firstTeamIndex, secondTeamIndex)
                    }
            ) {
                gameRecordMap?.get(gameRecordKey)?.let { record ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = "${record.firstScore}:${record.secondScore}",
                            color = Color(0xff333333),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val firstTeam = teamMap?.get(record.firstTeamIndex)
                        val secondTeam = teamMap?.get(record.secondTeamIndex)
                        if (firstTeam != null && secondTeam != null) {
                            val pointDiff = calTeamMatchPointDiff(firstTeam, secondTeam)
                            Text(
                                text = "分差${pointDiff?:"???"}",
                                color = Color(0xff333333),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                            )
                            val pointChange = pointDiff?.let { diff ->
                                calTeamPointChange(
                                    pointDiff = diff,
                                    result = calGameResult(record),
                                    teams = firstTeam to secondTeam
                                )
                            }
                            Text(
                                text = if(pointChange == null) {
                                    "???"
                                } else if(pointChange < 0) {
                                    pointChange.toString()
                                } else {
                                    "+$pointChange"
                                },
                                color = if(pointChange == null) {
                                    Color.Blue
                                } else if(pointChange > 0) {
                                    Color.Blue
                                } else if(pointChange < 0) {
                                    Color.Red
                                } else {
                                    Color(0xff333333)
                                },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun BoxScope.BadmintonItemTitle(title: String) {
        Text(
            text = title,
            color = Color(0xff333333),
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
    }

    @Composable
    private fun BadmintonItemDivider() {
        Spacer(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(color = Color.Black)
        )
    }

    @Composable
    private fun BoxScope.BadmintonItemWinLoss(teamIndex: Int) {
        val gameRecordMap by viewModel.gameRecordMap.observeAsState()
        val relatedGameRecords = gameRecordMap?.filter { it.key.startsWith("$teamIndex") }?.values
        val win = relatedGameRecords?.count { calGameResult(it) == EnumGameResult.WIN } ?:0
        val loss = relatedGameRecords?.count { calGameResult(it) == EnumGameResult.LOSS } ?:0
        if (!relatedGameRecords.isNullOrEmpty()) {
            Text(
                text = "${win}胜${loss}负",
                color = Color(0xff333333),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun BoxScope.BadmintonItemPointChange(firstTeamIndex: Int) {
        val gameRecordMap by viewModel.gameRecordMap.observeAsState()
        val teamMap by viewModel.teamMap.observeAsState()

        val firstTeam = teamMap?.get(firstTeamIndex)
        val relatedGameRecords = gameRecordMap?.filter { it.key.startsWith("$firstTeamIndex") }?.values
        val allOpponentsHasScore = relatedGameRecords?.map { it.secondTeamIndex }
            ?.map { teamMap?.get(it) }
            ?.all { it?.calTeamAvgPoint() != null }
        if (firstTeam?.calTeamAvgPoint() != null &&
            relatedGameRecords?.size.orZero() == teamMap?.size.orZero() - 1 &&
            allOpponentsHasScore == true) {
            val pointChange = relatedGameRecords.sumOf { record ->
                val secondTeamIndex = record.secondTeamIndex
                val secondTeam = teamMap?.get(secondTeamIndex)
                if (secondTeam == null) {
                    0
                } else {
                    val gameResult = calGameResult(record)
                    val pointDiff = calTeamMatchPointDiff(firstTeam, secondTeam)
                    calTeamPointChange(
                        pointDiff = pointDiff ?: 0,
                        result = gameResult,
                        teams = firstTeam to secondTeam,
                    ) ?: 0
                }
            }
            Text(
                text = if (pointChange < 0) {
                    "${pointChange}分"
                } else {
                    "+${pointChange}分"
                },
                color = if(pointChange > 0) {
                    Color.Blue
                } else if(pointChange < 0) {
                    Color.Red
                } else {
                    Color(0xff333333)
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }

    @Composable
    private fun BadmintonItemPointAfter(firstTeamIndex: Int) {
        val gameRecordMap by viewModel.gameRecordMap.observeAsState()
        val teamMap by viewModel.teamMap.observeAsState()

        val firstTeam = teamMap?.get(firstTeamIndex)
        val relatedGameRecords = gameRecordMap?.filter { it.key.startsWith("$firstTeamIndex") }?.values
        val allOpponentsHasScore = relatedGameRecords?.map { it.secondTeamIndex }
            ?.map { teamMap?.get(it) }
            ?.all { it?.calTeamAvgPoint() != null }
        if (firstTeam?.calTeamAvgPoint() != null &&
            relatedGameRecords?.size.orZero() == teamMap?.size.orZero() - 1 &&
            allOpponentsHasScore == true) {
            val pointChange = relatedGameRecords.sumOf { record ->
                val secondTeamIndex = record.secondTeamIndex
                val secondTeam = teamMap?.get(secondTeamIndex)
                if (secondTeam == null) {
                    0
                } else {
                    val gameResult = calGameResult(record)
                    val pointDiff = calTeamMatchPointDiff(firstTeam, secondTeam)
                    calTeamPointChange(
                        pointDiff = pointDiff ?: 0,
                        result = gameResult,
                        teams = firstTeam to secondTeam,
                    ) ?: 0
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Column (
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        text = "${firstTeam.player1.name}(${firstTeam.player1.calMatchPoint().orZero() + pointChange})",
                        color = Color(0xff333333),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    Text(
                        text = "${firstTeam.player2.name}(${firstTeam.player2.calMatchPoint().orZero() + pointChange})",
                        color = Color(0xff333333),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }
    }
}