package com.example.android.politicalpreparedness.election

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDirections
import com.example.android.politicalpreparedness.network.models.ElectionModel
import com.example.android.politicalpreparedness.network.models.toDomainModel
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.launch

//TODO: Construct ViewModel and provide election datasource
class ElectionsViewModel(private val repository: ElectionsRepository) : ViewModel() {

    private val _dataLoading = MutableLiveData(false)
    val dataLoading: LiveData<Boolean> = _dataLoading

    private val _navigateTo = MutableLiveData<NavDirections?>()
    val navigateTo: LiveData<NavDirections?> = _navigateTo

    private val elections: LiveData<List<ElectionModel>> = Transformations.map(repository.observeElections()) {
        when (it) {
            is Result.Failure -> {
                emptyList()
            }
            is Result.Success -> {
                it.data.toDomainModel()
            }
            is Result.Loading -> {
                upcomingElections.value
            }
        }
    }

    val upcomingElections: LiveData<List<ElectionModel>> = elections

    val savedElections: LiveData<List<ElectionModel>> = Transformations.map(elections) {
        it.filter { it.saved }
    }

    fun refresh() {
        _dataLoading.value = true
        viewModelScope.launch {
            refreshElections()
            _dataLoading.value = false
        }
    }

    private suspend fun refreshElections() {
        repository.refreshElections()
    }

    fun onUpcomingClicked(electionModel: ElectionModel) {
        _navigateTo.value = ElectionsFragmentDirections
            .actionElectionsFragmentToVoterInfoFragment(electionModel)
    }

    fun onSavedClicked(electionModel: ElectionModel) {
        _navigateTo.value = ElectionsFragmentDirections
            .actionElectionsFragmentToVoterInfoFragment(electionModel)
    }

    fun navigateCompleted() {
        _navigateTo.value = null
    }
}