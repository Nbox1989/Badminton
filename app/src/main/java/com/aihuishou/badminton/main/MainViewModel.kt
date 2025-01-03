package com.aihuishou.badminton.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.aihuishou.badminton.data.GameRecord
import com.aihuishou.badminton.data.Team

class MainViewModel: ViewModel() {

    val displayMatchDay = MutableLiveData<String?>()

    val teamMap = MutableLiveData<Map<Int, Team>?>()

    val gameRecordMap = MutableLiveData<Map<String, GameRecord>?>()

}