package com.aihuishou.badminton.main.dialog

import android.app.Activity
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aihuishou.badminton.R
import com.aihuishou.badminton.data.GameRecord
import com.aihuishou.badminton.data.Team
import com.aihuishou.badminton.ext.orZero
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class InsertGameRecordDialog(
    private val activity: Activity,
    private val gameRecord: GameRecord,
    private val teams: Pair<Team, Team>,
) {

    private var customDialog: CustomDialog? = null

    private var callback: ((GameRecord) -> Unit)? = null

    fun showDialog(callback: (GameRecord) -> Unit) {
        this.callback = callback
        customDialog = CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog>(R.layout.dialog_compose_view) {
                override fun onBind(dialog: CustomDialog, v: View) {
                    v.findViewById<ComposeView>(R.id.cv_content).setContent {
                        ComposeTheme {
                            GameRecordDialogContent()
                        }
                    }
                }
            })
            .setMaskColor(android.graphics.Color.parseColor("#cc000000"))
            .setCancelable(false)
            .show(activity)
    }

    private fun setCallback(score1: Int, score2: Int) {
        val newRecord = GameRecord(
            firstScore = score1,
            secondScore = score2,
            firstTeamIndex = gameRecord.firstTeamIndex,
            secondTeamIndex = gameRecord.secondTeamIndex,
        )
        callback?.invoke(newRecord)
    }

    @Composable
    fun GameRecordDialogContent() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var inputScore1 by remember {
                mutableStateOf("")
            }
            var inputScore2 by remember {
                mutableStateOf("")
            }
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${teams.first.player1.name}\n${teams.first.player2.name}",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                )
                LaunchedEffect(key1 = "GameRecordDialogContent") {
                    inputScore1 = gameRecord.firstScore.takeIf { it.orZero() > 0 }?.toString().orEmpty()
                    inputScore2 = gameRecord.secondScore.takeIf { it.orZero() > 0 }?.toString().orEmpty()
                }
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(50.dp)
                        .wrapContentHeight(),
                    value = inputScore1,
                    onValueChange = {
                        inputScore1 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.End,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputScore1.isEmpty()) {
                                Text(
                                    text = "比分",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                Text(
                    text = ":",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                )
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(50.dp)
                        .wrapContentHeight(),
                    value = inputScore2,
                    onValueChange = {
                        inputScore2 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputScore2.isEmpty()) {
                                Text(
                                    text = "比分",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                Text(
                    text = "${teams.second.player1.name}\n${teams.second.player2.name}",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                )
            }
            Row(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .wrapContentSize()
                        .border(
                            width = 1.dp,
                            shape = RoundedCornerShape(50),
                            color = Color(0xff999999)
                        )
                        .clickable {
                            customDialog?.dismiss()
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.width(50.dp))
                Text(
                    text = "确认",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .wrapContentSize()
                        .background(
                            shape = RoundedCornerShape(50),
                            color = Color(0xffF9E72C)
                        )
                        .clickable {
                            setCallback(inputScore1.toIntOrNull()?:0, inputScore2.toIntOrNull()?:0)
                            customDialog?.dismiss()
                        }
                        .padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }
        }
    }
}
