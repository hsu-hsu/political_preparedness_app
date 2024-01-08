package com.example.android.politicalpreparedness.election

import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.network.models.ElectionModel
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.network.models.toDataModel
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.launch

class VoterInfoViewModel(
    private val repository: ElectionsRepository,
    val election: ElectionModel,
) : ViewModel() {

    //TODO: Add live data to hold voter info

    //TODO: Add var and methods to populate voter info

    //TODO: Add var and methods to support loading URLs

    //TODO: Add var and methods to save and remove elections to local database
    //TODO: cont'd -- Populate initial state of save button to reflect proper action based on election saved status
    val errorMessage: LiveData<Int?>
        get() = Transformations.map(_electionDetails) {
            if (it is Result.Failure) {
                R.string.error_failed_load_voter_info
            } else {
                null
            }
        }

    private val _electionDetails: MutableLiveData<Result<State?>> = MutableLiveData()
    val electionDetails: LiveData<State?> = Transformations.map(_electionDetails) {
        when (it) {
            is Result.Success -> it.data
            else -> null
        }
    }

    private val _navigateBack: MutableLiveData<Boolean> = MutableLiveData()
    val navigateBack: LiveData<Boolean>
        get() = _navigateBack

    fun loadDetails(address: Address?) {
        viewModelScope.launch {
            val exactAddress = "${address?.getAddressLine(0)}"
            val response = repository.getElectionDetails(election.id, exactAddress)
            _electionDetails.value = response
        }
    }

    fun onActionClick() {
        viewModelScope.launch {
            repository.markAsSaved(election.toDataModel(), election.saved.not())
            _navigateBack.value = true
        }
    }

    fun navigateCompleted() {
        _navigateBack.value = false
    }

    /**
     * Hint: The saved state can be accomplished in multiple ways. It is directly related to how elections are saved/removed from the database.
     */

}