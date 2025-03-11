package com.aihuishou.badminton.player.edit.dialog

import android.app.Activity
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aihuishou.badminton.R
import com.aihuishou.badminton.data.NameAndPoint
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.kongzue.dialogx.dialogs.CustomDialog
import com.kongzue.dialogx.interfaces.OnBindView

class PlayerRemoveDialog(private val activity: Activity) {

    private var customDialog: CustomDialog? = null

    private var nameAndPoint: NameAndPoint? = null

    private var callback: PlayerRemoveCallback? = null

    fun showDialog(nameAndPoint: NameAndPoint, callback : PlayerRemoveCallback) {
        this.nameAndPoint = nameAndPoint
        this.callback = callback
        customDialog = CustomDialog.build()
            .setCustomView(object : OnBindView<CustomDialog>(R.layout.dialog_compose_view) {
                override fun onBind(dialog: CustomDialog, v: View) {
                    v.findViewById<ComposeView>(R.id.cv_content).setContent {
                        ComposeTheme {
                            PlayerRemoveContent()
                        }
                    }
                }
            })
            .setMaskColor(android.graphics.Color.parseColor("#cc000000"))
            .setCancelable(false)
            .show(activity)
    }

    private fun onPlayerRemoveConfirmed() {
        nameAndPoint?.let {
            callback?.onPlayerRemove(it)
            customDialog?.dismiss()
        }

    }

    @Composable
    fun PlayerRemoveContent() {
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
                text = "确认删除${nameAndPoint?.name}?",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xff111111),
            )
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
                            onPlayerRemoveConfirmed()
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

interface PlayerRemoveCallback {
    fun onPlayerRemove(nameAndPoint: NameAndPoint)
}
