package com.aihuishou.badminton.main.dialog

import android.app.Activity
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.aihuishou.badminton.R
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.aihuishou.badminton.utils.StorageUtil
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class SettingsDialog(private val activity: Activity) {

    private var customDialog: CustomDialog? = null

    private var listener: SettingListener? = null

    /** menu, restore, grade **/
    private val displayContentType = MutableLiveData("menu")

    fun showDialog(listener: SettingListener) {
        this.listener = listener
        customDialog = CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog>(R.layout.dialog_compose_view) {
                override fun onBind(dialog: CustomDialog, v: View) {
                    v.findViewById<ComposeView>(R.id.cv_content).setContent {
                        ComposeTheme {
                            SettingsContent()
                        }
                    }
                }
            })
            .setMaskColor(android.graphics.Color.parseColor("#cc000000"))
            .setCancelable(false)
            .show(activity)
    }

    @Composable
    fun SettingsContent() {
        val displayContent by displayContentType.observeAsState()
        when (displayContent) {
            "menu" -> {
                SettingsContentMenuList()
            }
            "restore" -> {
                SettingsContentRestore()
            }
            "grade"-> {
                SettingsContentGrade()
            }
        }
    }

    @Composable
    fun SettingsContentMenuList() {
        Box(
            modifier = Modifier.wrapContentSize()
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(10.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))
                listOf(
                    "切换日期" to {
                        displayContentType.value = "restore"
                    },
                    "屏幕截图" to {
                        customDialog?.dismiss()
                        listener?.onScreenShot()
                    },
                    "人员维护" to {
                        customDialog?.dismiss()
                        listener?.onEditPlayers()
                    },
                    "更新积分" to {
                        customDialog?.dismiss()
                        listener?.onUpdatePlayerPoints()
                    },
                ).forEach {
                    TeamEditContentMenu(it.first) {
                        it.second.invoke()
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }
                Spacer(modifier = Modifier.height(25.dp))
            }
            Image(
                painter = painterResource(id = R.mipmap.ic_dialog_close),
                contentDescription = null,
                modifier = Modifier
                    .padding(top = 5.dp, end = 5.dp)
                    .clickable {
                        customDialog?.dismiss()
                    }
                    .align(Alignment.TopEnd)
                    .size(20.dp)
                    .padding(5.dp)
            )
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
    fun SettingsContentRestore() {
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
                text = "选择日期",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff111111),
            )
            var selectedDay by remember {
                mutableStateOf("")
            }
            Column (
                modifier = Modifier
                    .size(250.dp, 150.dp)
                    .verticalScroll(rememberScrollState())
            ){
                val daysList = StorageUtil.getMatchDataKeys()
                daysList.forEach {
                    val isSelected = selectedDay == it
                    Text(
                        text = it,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isSelected) Color(0xff111111) else Color(0xff999999),
                        modifier = Modifier
                            .clickable {
                                selectedDay = it
                            }
                            .padding(vertical = 2.dp)
                    )
                }
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
                        if (selectedDay.isNotEmpty()) {
                            listener?.loadMatchData(selectedDay)
                            customDialog?.dismiss()
                        }
                    }
            ) {
                Text(
                    text = "确认",
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
    fun SettingsContentGrade() {
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

            Spacer(modifier = Modifier.height(10.dp))
            Box (
                modifier = Modifier
                    .size(250.dp, 50.dp)
                    .background(
                        shape = RoundedCornerShape(50),
                        color = Color(0xffF9E72C)
                    )
                    .clickable {

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
}

interface SettingListener {
    fun loadMatchData(key: String)
    fun onScreenShot()
    fun onEditPlayers()
    fun onUpdatePlayerPoints()
}