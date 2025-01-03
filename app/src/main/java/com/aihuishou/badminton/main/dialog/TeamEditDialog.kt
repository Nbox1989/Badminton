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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.aihuishou.badminton.R
import com.aihuishou.badminton.data.GameRecord
import com.aihuishou.badminton.data.Player
import com.aihuishou.badminton.data.Team
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class TeamEditDialog(private val activity: Activity, private val team: Team?) {

    private var customDialog: CustomDialog? = null

    private var callback: ((Team?) -> Unit)? = null

    /** menu, create, modify, delete **/
    private val displayContentType = MutableLiveData("menu")

    fun showDialog(callback: (Team?) -> Unit) {
        this.callback = callback
        customDialog = CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog>(R.layout.dialog_compose_view) {
                override fun onBind(dialog: CustomDialog, v: View) {
                    v.findViewById<ComposeView>(R.id.cv_content).setContent {
                        ComposeTheme {
                            TeamEditContent()
                        }
                    }
                }
            })
            .setMaskColor(android.graphics.Color.parseColor("#cc000000"))
            .setCancelable(false)
            .show(activity)
    }

    @Composable
    fun TeamEditContent() {
        val displayContent by displayContentType.observeAsState()
        when (displayContent) {
            "menu" -> {
                TeamEditContentMenuList()
            }
            "create" -> {
                TeamEditContentCreate()
            }
            "modify"-> {
                TeamEditContentModify()
            }
            "delete"-> {
                TeamEditContentDelete()
            }
        }
    }

    @Composable
    fun TeamEditContentMenuList() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val list = if (team == null) {
                listOf(
                    "创建队伍" to {
                        displayContentType.value = "create"
                    },
                    "取消" to {
                        customDialog?.dismiss()
                    }
                )
            } else {
                listOf(
                    "修改队伍信息" to {
                        displayContentType.value = "modify"
                    },
                    "删除队伍" to {
                        displayContentType.value = "delete"
                    },
                    "取消" to {
                        customDialog?.dismiss()
                    }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            list.forEach {
                TeamEditContentMenu(it.first) {
                    it.second.invoke()
                }
                Spacer(modifier = Modifier.height(15.dp))
            }
            Spacer(modifier = Modifier.height(25.dp))
        }
    }

    @Composable
    private fun TeamEditContentMenu(title: String, onClick:()-> Unit) {
        Box (
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .size(200.dp, 50.dp)
                .background(
                    shape = RoundedCornerShape(50),
                    color = Color(0xffF9E72C)
                )
                .clickable {
                    onClick.invoke()
                }
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = Color(0xff333333),
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }

    @Composable
    fun TeamEditContentCreate() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "创建新队伍",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.height(10.dp))
            var inputName1 by remember {
                mutableStateOf("")
            }
            var inputName2 by remember {
                mutableStateOf("")
            }
            var inputPoint1 by remember {
                mutableStateOf("")
            }
            var inputPoint2 by remember {
                mutableStateOf("")
            }
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputName1,
                    onValueChange = {
                        inputName1 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputName1.isEmpty()) {
                                Text(
                                    text = "队员1姓名",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputName2,
                    onValueChange = {
                        inputName2 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputName2.isEmpty()) {
                                Text(
                                    text = "队员2姓名",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputPoint1,
                    onValueChange = {
                        inputPoint1 = it
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
                            if (inputPoint1.isEmpty()) {
                                Text(
                                    text = "积分(可空)",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputPoint2,
                    onValueChange = {
                        inputPoint2 = it
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
                            if (inputPoint2.isEmpty()) {
                                Text(
                                    text = "积分(可空)",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .background(
                        shape = RoundedCornerShape(50),
                        color = Color(0xffF9E72C)
                    )
                    .clickable {
                        val newTeam = Team(
                            player1 = Player(
                                name = inputName1,
                                point = inputPoint1.toIntOrNull(),
                            ),
                            player2 = Player(
                                name = inputName2,
                                point = inputPoint2.toIntOrNull(),
                            )
                        )
                        callback?.invoke(newTeam)
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "确认创建队伍",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(50),
                        color = Color(0xff999999)
                    )
                    .clickable {
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    @Composable
    fun TeamEditContentModify() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "修改队伍信息",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.height(10.dp))
            var inputName1 by remember {
                mutableStateOf("")
            }
            var inputName2 by remember {
                mutableStateOf("")
            }
            var inputPoint1 by remember {
                mutableStateOf("")
            }
            var inputPoint2 by remember {
                mutableStateOf("")
            }
            LaunchedEffect(key1 = "TeamEditContentModify") {
                inputName1 = team?.player1?.name?:""
                inputName2 = team?.player2?.name?:""
                inputPoint1 = team?.player1?.point?.toString()?:""
                inputPoint2 = team?.player2?.point?.toString()?:""
            }
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputName1,
                    onValueChange = {
                        inputName1 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputName1.isEmpty()) {
                                Text(
                                    text = "队员1姓名",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputName2,
                    onValueChange = {
                        inputName2 = it
                    },
                    textStyle = TextStyle(
                        color = Color(0xff111111),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    cursorBrush = SolidColor(Color(0xff0091FF)),
                    decorationBox = { innerTextField ->
                        Box {
                            if (inputName2.isEmpty()) {
                                Text(
                                    text = "队员2姓名",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputPoint1,
                    onValueChange = {
                        inputPoint1 = it
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
                            if (inputPoint1.isEmpty()) {
                                Text(
                                    text = "积分(可空)",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputPoint2,
                    onValueChange = {
                        inputPoint2 = it
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
                            if (inputPoint2.isEmpty()) {
                                Text(
                                    text = "积分(可空)",
                                    fontSize = 14.sp,
                                    color = Color(0xffcccccc)
                                )
                            }
                            innerTextField()
                        }
                    },
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .background(
                        shape = RoundedCornerShape(50),
                        color = Color(0xffF9E72C)
                    )
                    .clickable {
                        val newTeam = Team(
                            player1 = Player(
                                name = inputName1,
                                point = inputPoint1.toIntOrNull(),
                            ),
                            player2 = Player(
                                name = inputName2,
                                point = inputPoint2.toIntOrNull(),
                            )
                        )
                        callback?.invoke(newTeam)
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "确认修改",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(50),
                        color = Color(0xff999999)
                    )
                    .clickable {
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    @Composable
    fun TeamEditContentDelete() {
        Column(
            modifier = Modifier
                .wrapContentSize()
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "删除队伍后，相关联的对局记录都会被删除，是否继续？",
                fontSize = 12.sp,
                color = Color.Red,
            )
            Spacer(modifier = Modifier.height(20.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .background(
                        shape = RoundedCornerShape(50),
                        color = Color(0xffF9E72C)
                    )
                    .clickable {
                        callback?.invoke(null)
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "确认删除队伍 ${team?.displayName()} ",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(15.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .border(
                        width = 1.dp,
                        shape = RoundedCornerShape(50),
                        color = Color(0xff999999)
                    )
                    .clickable {
                        customDialog?.dismiss()
                    }
            ) {
                Text(
                    text = "取消",
                    fontSize = 14.sp,
                    color = Color(0xff333333),
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
