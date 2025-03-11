package com.aihuishou.badminton.player.edit.dialog

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aihuishou.badminton.R
import com.aihuishou.badminton.data.NameAndPoint
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.blankj.utilcode.util.ToastUtils
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class PlayerEditDialog(private val activity: Activity) {

    private var customDialog: CustomDialog? = null

    private var nameAndPoint: NameAndPoint? = null

    private var callback: PlayerEditCallback? = null

    fun showDialog(nameAndPoint: NameAndPoint, callback : PlayerEditCallback) {
        this.nameAndPoint = nameAndPoint
        this.callback = callback
        customDialog = CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog>(R.layout.dialog_compose_view) {
                override fun onBind(dialog: CustomDialog, v: View) {
                    v.findViewById<ComposeView>(R.id.cv_content).setContent {
                        ComposeTheme {
                            PlayerEditContent()
                        }
                    }
                }
            })
            .setMaskColor(android.graphics.Color.parseColor("#cc000000"))
            .setCancelable(false)
            .show(activity)
    }

    private fun onPlayerEdited(name: String, point: Int?) {
        if (name.isEmpty()) {
            ToastUtils.showShort("请输入姓名")
            return
        }
        nameAndPoint?.copy(
            name = name,
            point = point,
        )?.let {
            callback?.onPlayerEditResult(it)
            customDialog?.dismiss()
        }

    }

    @Composable
    fun PlayerEditContent() {
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
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "创建或编辑",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.height(15.dp))
            var inputName by remember {
                mutableStateOf(nameAndPoint?.name.orEmpty())
            }
            var inputPoint by remember {
                mutableStateOf(nameAndPoint?.point?.toString().orEmpty())
            }
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                BasicTextField(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .width(100.dp)
                        .wrapContentHeight(),
                    value = inputName,
                    onValueChange = {
                        inputName = it
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
                            if (inputName.isEmpty()) {
                                Text(
                                    text = "队员姓名",
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
                    value = inputPoint,
                    onValueChange = {
                        inputPoint = it
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
                            if (inputPoint.isEmpty()) {
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
            Spacer(modifier = Modifier.height(15.dp))
            Row (
                modifier = Modifier.wrapContentSize()
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp, 40.dp)
                        .background(
                            shape = RoundedCornerShape(50),
                            color = Color(0xffF9E72C)
                        )
                        .clickable {
                            onPlayerEdited(inputName, inputPoint.toIntOrNull())
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
                Spacer(modifier = Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp, 40.dp)
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
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

}

interface PlayerEditCallback {
    fun onPlayerEditResult(nameAndPoint: NameAndPoint)
}
