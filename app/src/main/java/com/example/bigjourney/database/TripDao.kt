package com.example.bigjourney.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.bigjourney.model.Trip

@Dao
interface TripDao {

    @Query("SELECT * FROM Trip")
    fun getAllTrip() : LiveData<List<Trip>>

    @Query("SELECT COUNT(*) FROM Trip")
    fun getTripCount(): LiveData<Int>

    @Insert
    suspend fun addTrip(trip: Trip)

    @Query("SELECT * FROM Trip WHERE user_id = :userId")
    fun getUserTrips(userId: String): LiveData<List<Trip>>

    @Query("SELECT COUNT(*) FROM Trip WHERE user_id = :userId")
    fun getUserTripCount(userId: String): LiveData<Int>

    @Query("DELETE FROM Trip WHERE tid IN (:tripIds)")
    suspend fun deleteTrips(tripIds: List<Long>)  // Η παράμετρος tripIds είναι ο τύπος List<Long>


}