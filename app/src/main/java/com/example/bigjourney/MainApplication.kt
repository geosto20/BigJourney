package com.example.bigjourney

import android.app.Application
import androidx.room.Room
import com.example.bigjourney.database.MIGRATION_1_2
import com.example.bigjourney.database.MIGRATION_2_1
import com.example.bigjourney.database.TripDatabase

class MainApplication : Application() {



    companion object {
        val tripDatabase: TripDatabase by lazy {
            Room.databaseBuilder(
                instance.applicationContext,
                TripDatabase::class.java,
                TripDatabase.NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }

        lateinit var instance: MainApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }


}
