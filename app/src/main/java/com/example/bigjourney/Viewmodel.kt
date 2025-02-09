package com.example.bigjourney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.bigjourney.model.Trip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TripViewModel(private val userId: String) : ViewModel() {
    private val tripDao = MainApplication.tripDatabase.getTripDao()

    val tripList: LiveData<List<Trip>> = tripDao.getUserTrips(userId)
    val tripCount: LiveData<Int> = tripDao.getUserTripCount(userId)

    fun addTrip(location: String, startDate: Date, endDate: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            tripDao.addTrip(Trip(userId = userId, location = location, startDate = startDate, endDate = endDate))
        }
    }

    fun deleteTrips(tripsToDelete: List<Trip>) {
        val tripIds = tripsToDelete.mapNotNull { it.tid?.toLong() }
        viewModelScope.launch(Dispatchers.IO) {
            tripDao.deleteTrips(tripIds)
        }
    }
}


class TripViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TripViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TripViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}