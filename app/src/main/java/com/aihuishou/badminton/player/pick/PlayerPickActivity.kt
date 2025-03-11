package com.aihuishou.badminton.player.pick

import android.content.Intent
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aihuishou.badminton.basic.BasicActivity
import com.aihuishou.badminton.data.NameAndPoint
import com.aihuishou.badminton.ext.orZero
import com.aihuishou.badminton.player.edit.dialog.PlayerEditCallback
import com.aihuishou.badminton.player.edit.dialog.PlayerEditDialog
import com.aihuishou.badminton.player.edit.dialog.PlayerRemoveCallback
import com.aihuishou.badminton.player.edit.dialog.PlayerRemoveDialog
import com.aihuishou.badminton.ui.theme.ComposeTheme
import com.aihuishou.badminton.utils.StorageUtil
import com.blankj.utilcode.util.ToastUtils

class PlayerPickActivity: BasicActivity() {

    private var teamIndex: Int = -1

    private val viewModel: PlayerPickViewModel by viewModels()

    override fun initObservers() = Unit

    override fun setupContent() {
        setContent {
            ComposeTheme {
                PlayerPickContent()
            }
        }
    }

    override fun initData() {
        teamIndex = intent.getIntExtra("index", -1)
        viewModel.nameAndPointList.value = StorageUtil.getPlayerPoints()
    }

    private fun confirmSelection() {
        val selectedIds = viewModel.selectedIdList.value
        if (selectedIds?.size.orZero() != 2) {
            ToastUtils.showShort("人员数量有误")
        } else {
            val player1 =
                viewModel.nameAndPointList.value?.firstOrNull { it.id == selectedIds?.getOrNull(0) }
            val player2 =
                viewModel.nameAndPointList.value?.firstOrNull { it.id == selectedIds?.getOrNull(1) }
            if (player1 == null || player2 == null) {
                ToastUtils.showShort("人员数量有误")
            } else {
                val intent = Intent()
                intent.putExtra("index", teamIndex)
                intent.putExtra("player1", player1)
                intent.putExtra("player2", player2)
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }

    @Composable
    private fun PlayerPickContent() {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "选择人员",
                fontSize = 18.sp,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.height(5.dp))
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                val players by viewModel.nameAndPointList.observeAsState()
                val selectedIds by viewModel.selectedIdList.observeAsState()
                players?.forEachIndexed { index, nameAndPoint ->
                    val isSelected = selectedIds?.contains(nameAndPoint.id) == true
                    PlayerItem(isSelected, index, nameAndPoint)
                }
            }
            Box(
                modifier = Modifier
                    .size(100.dp, 40.dp)
                    .background(
                        shape = RoundedCornerShape(50),
                        color = Color(0xffF9E72C)
                    )
                    .clickable {
                        confirmSelection()
                    }
            ) {
                Text(
                    text = "确认选择",
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
    private fun PlayerItem(isSelected: Boolean, index: Int, player: NameAndPoint) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = if (index % 2 == 0) Color.Gray else Color.White,
                )
                .clickable {
                    val selection = viewModel.selectedIdList.value?.toMutableList()?: mutableListOf()
                    if (isSelected) {
                        selection.remove(player.id)
                    } else {
                        selection.add(player.id)
                    }
                    viewModel.selectedIdList.value = selection
                }
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = if(isSelected) "✅" else " ",
                fontSize = 16.sp,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = player.id.toString(),
                fontSize = 16.sp,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = player.name.orEmpty(),
                fontSize = 16.sp,
                color = Color(0xff111111),
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = player.point.orZero().toString(),
                fontSize = 16.sp,
                color = Color(0xff111111),
            )
        }
    }
}