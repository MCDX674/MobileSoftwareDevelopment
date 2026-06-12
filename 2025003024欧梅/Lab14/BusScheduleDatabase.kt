package com.example.busschedule.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// 数据库版本 1，关联 Entity
@Database(entities = [BusSchedule::class], version = 1, exportSchema = false)
abstract class BusScheduleDatabase : RoomDatabase() {

    // 向外暴露 DAO
    abstract fun busScheduleDao(): BusScheduleDao

    companion object {
        // 单例
        @Volatile
        private var INSTANCE: BusScheduleDatabase? = null

        fun getDatabase(context: Context): BusScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BusScheduleDatabase::class.java,
                    "bus_schedule_database"
                )
                    // 加载 assets 里的预置数据库
                    .createFromAsset("database/bus_schedule.db")
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}