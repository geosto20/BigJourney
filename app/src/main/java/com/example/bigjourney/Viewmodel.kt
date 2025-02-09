package com.example.bigjourney

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bigjourney.model.Trip
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class TripViewModel : ViewModel() {

    private val tripDao = MainApplication.tripDatabase.getTripDao()

    private val userId: String? = FirebaseAuth.getInstance().currentUser?.uid // Παίρνουμε τον userId

    val tripList: LiveData<List<Trip>> = tripDao.getAllTrip()

    val tripCount: LiveData<Int> = tripDao.getTripCount()


    fun addTrip(location: String, startDate: Date, endDate: Date) {

            viewModelScope.launch(Dispatchers.IO) {
                tripDao.addTrip(Trip( location = location, startDate = startDate, endDate = endDate))
            }

    }


    fun deleteTrips(tripsToDelete: List<Trip>) {
        val tripIds = tripsToDelete.mapNotNull { it.tid?.toLong() }

            viewModelScope.launch(Dispatchers.IO) {
                tripDao.deleteTrips(tripIds.filterNotNull())
            }

    }


}