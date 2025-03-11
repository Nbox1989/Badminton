package com.aihuishou.badminton.player.edit

import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
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

class PlayerListActivity: BasicActivity() {

    private val viewModel: PlayerListViewModel by viewModels()

    override fun initObservers() {
        viewModel.nameAndPointList.observe(this) {
            StorageUtil.savePlayerPoints(it.orEmpty())
        }
    }

    override fun setupContent() {
        setContent {
            ComposeTheme {
                PlayerListContent()
            }
        }
    }

    override fun initData() {
        viewModel.nameAndPointList.value = StorageUtil.getPlayerPoints()
    }

    private fun tryAddPlayer() {
        var newId = viewModel.nameAndPointList.value?.size.orZero() + 1
        viewModel.nameAndPointList.value?.sortedBy { it.id }?.forEachIndexed { index, nameAndPoint ->
            if (index + 1 != nameAndPoint.id) {
                newId = index + 1
                return@forEachIndexed
            }
        }
        PlayerEditDialog(this@PlayerListActivity).showDialog(
            nameAndPoint = NameAndPoint(id = newId, name = "", point = null),
            callback = object: PlayerEditCallback {
                override fun onPlayerEditResult(nameAndPoint: NameAndPoint) {
                    val list = viewModel.nameAndPointList.value?.toMutableList() ?: mutableListOf()
                    list.add(nameAndPoint)
                    viewModel.nameAndPointList.value = list.sortedBy { it.id  }
                }
            }
        )
    }

    private fun tryEditPlayer(player: NameAndPoint) {
        PlayerEditDialog(this@PlayerListActivity).showDialog(
            nameAndPoint = player,
            callback = object: PlayerEditCallback {
                override fun onPlayerEditResult(nameAndPoint: NameAndPoint) {
                    val list = viewModel.nameAndPointList.value?.toMutableList() ?: mutableListOf()
                    list.removeAll { it.id == player.id }
                    list.add(nameAndPoint)
                    viewModel.nameAndPointList.value = list.sortedBy { it.id  }
                }
            }
        )
    }

    private fun tryRemovePlayer(player: NameAndPoint) {
        PlayerRemoveDialog(this@PlayerListActivity).showDialog(
            nameAndPoint = player,
            callback = object: PlayerRemoveCallback {
                override fun onPlayerRemove(nameAndPoint: NameAndPoint) {
                    val list = viewModel.nameAndPointList.value?.toMutableList() ?: mutableListOf()
                    list.removeAll { it.id == player.id }
                    viewModel.nameAndPointList.value = list.sortedBy { it.id  }
                }
            }
        )
    }

    @Composable
    private fun PlayerListContent() {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "添加人员",
                fontSize = 18.sp,
                color = Color(0xff111111),
                modifier = Modifier.clickable {
                    tryAddPlayer()
                }
            )
            Spacer(modifier = Modifier.height(5.dp))
            Column (
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                val players by viewModel.nameAndPointList.observeAsState()
                players?.forEachIndexed { index, nameAndPoint ->
                    PlayerItem(index, nameAndPoint)
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    @Composable
    private fun PlayerItem(index: Int, player: NameAndPoint) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = if (index % 2 == 0) Color.Gray else Color.White,
                )
                .padding(vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ){
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
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "修改",
                fontSize = 14.sp,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    tryEditPlayer(player)
                }
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = "删除",
                fontSize = 14.sp,
                color = Color.Blue,
                modifier = Modifier.clickable {
                    tryRemovePlayer(player)
                }
            )
            Spacer(modifier = Modifier.width(20.dp))
        }
    }
}