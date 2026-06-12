package com.example.busschedule.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import com.example.busschedule.data.BusScheduleDao
import com.example.busschedule.data.BusScheduleDatabase
import com.example.busschedule.data.BusSchedule
import kotlinx.coroutines.flow.Flow

class BusScheduleViewModel(
    private val busScheduleDao: BusScheduleDao
) : ViewModel() {

    fun getFullSchedule(): Flow<List<BusSchedule>> {
        return busScheduleDao.getAll()
    }

    fun getScheduleFor(stopName: String): Flow<List<BusSchedule>> {
        return busScheduleDao.getByStopName(stopName)
    }

    companion object {
        val factory = viewModelFactory {
            initializer {
                val application = checkNotNull(this[APPLICATION_KEY])
                val database = BusScheduleDatabase.getDatabase(application)
                BusScheduleViewModel(database.busScheduleDao())
            }
        }
    }
}