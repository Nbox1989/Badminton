package com.aihuishou.badminton.player.edit

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aihuishou.badminton.data.NameAndPoint

class PlayerListViewModel: ViewModel() {

    val nameAndPointList = MutableLiveData<List<NameAndPoint>?>()

}