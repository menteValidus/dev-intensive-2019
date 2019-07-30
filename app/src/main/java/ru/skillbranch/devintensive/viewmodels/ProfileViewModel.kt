package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.models.Profile
import ru.skillbranch.devintensive.repositories.PreferencesRepository

class  ProfileViewModel : ViewModel() {
    private val repository : PreferencesRepository = PreferencesRepository
    private  val profileData = MediatorLiveData<Profile>()
    init {
        Log.d("M_ProfileViewModel","init view model")
        profileData.value = repository.getProfile()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("M_ProfileViewModel","view model cleared")
    }

    fun getProfileData() : LiveData<Profile> = profileData

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value  = profile
    }
}