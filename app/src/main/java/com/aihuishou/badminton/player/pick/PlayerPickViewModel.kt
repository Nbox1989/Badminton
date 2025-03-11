package com.aihuishou.badminton.player.pick

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aihuishou.badminton.data.NameAndPoint

class PlayerPickViewModel: ViewModel() {

    val nameAndPointList = MutableLiveData<List<NameAndPoint>?>()

    val selectedIdList = MutableLiveData<List<Int>?>()

}