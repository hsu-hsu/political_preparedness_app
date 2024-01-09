package com.example.android.politicalpreparedness.representative

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.repository.Result
import com.example.android.politicalpreparedness.repository.dataOrThrow
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class RepresentativeViewModel(private val repository: ElectionsRepository, private val savedStateHandle: SavedStateHandle) : ViewModel() {

    //TODO: Establish live data for representatives and address

    //TODO: Create function to fetch representatives from API from a provided address

    private val _line1 = MutableLiveData("")
    val line1: MutableLiveData<String> = _line1
    private val _line2 = MutableLiveData("")
    val line2: MutableLiveData<String> = _line2
    private val _city = MutableLiveData("")
    val city: MutableLiveData<String> = _city
    private val _state = MutableLiveData("")
    val state: MutableLiveData<String> = _state
    private val _zip = MutableLiveData("")
    val zip: MutableLiveData<String> = _zip

    val dataLoading: SingleLiveEvent<Boolean> = SingleLiveEvent()
    val message: SingleLiveEvent<Int> = SingleLiveEvent()
    val messageString = SingleLiveEvent<String>()

    //val list: MutableLiveData<Result<List<Representative>>?> = savedStateHandle["representatives"]!!

    private val _representatives: MutableLiveData<Result<List<Representative>>?> = MutableLiveData()
    val representatives: LiveData<List<Representative>> = Transformations.map(_representatives) {
        when (it) {
            is Result.Failure -> emptyList()
            is Result.Success -> it.data
            is Result.Loading -> emptyList()
            else -> {
                emptyList()
            }
        }
    }

    init {
        val list: List<Representative>? = savedStateHandle["representatives"]
        Log.i("List", list?.size.toString())
        if(list != null) {
            _representatives.value = Result.Success(list)
        }
    }

    fun searchForRepresentatives(address: Address?) {
        if(address == null) {
            message.value = R.string.representative_failed_parse_location
            return
        }
        _line1.value = address.line1
        _line2.value = address.line2.orEmpty()
        _city.value = address.city
        _state.value = address.state
        _zip.value = address.zip
        searchRepresentativesIfFormValid(address)
    }

    fun searchForMyRepresentatives() {
        searchRepresentativesIfFormValid(
            Address(
                requireNotNull(line1.value),
                line2.value,
                requireNotNull(city.value),
                requireNotNull(state.value),
                requireNotNull(zip.value)
            )
        )
    }

    private fun searchRepresentativesIfFormValid(address: Address) {
        if (address.line1.isBlank()) {
            message.value = R.string.error_missing_first_line_address
            return
        }
        if (address.city.isBlank()) {
            message.value = R.string.error_missing_city
            return
        }
        if (address.state.isBlank()) {
            message.value = R.string.error_missing_state
            return
        }
        if (address.zip.isBlank()) {
            message.value = R.string.error_missing_zip
            return
        }
        search(address)
    }

    private fun search(address: Address) {
        dataLoading.value = true
        viewModelScope.launch {
            _representatives.value = repository.searchRepresentatives(address)
            when (val result = _representatives.value) {
                is Result.Failure -> messageString.value = result.exception.message
                is Result.Success -> savedStateHandle["representatives"] = _representatives.value?.dataOrThrow()
                is Result.Loading,
                null -> {
                }
            }
            dataLoading.value = false
        }
    }

    fun setState(state: String?) {
        _state.value = state.orEmpty()
    }

    /**
     *  The following code will prove helpful in constructing a representative from the API. This code combines the two nodes of the RepresentativeResponse into a single official :

    val (offices, officials) = getRepresentativesDeferred.await()
    _representatives.value = offices.flatMap { office -> office.getRepresentatives(officials) }

    Note: getRepresentatives in the above code represents the method used to fetch data from the API
    Note: _representatives in the above code represents the established mutable live data housing representatives

     */

    //TODO: Create function get address from geo location

    //TODO: Create function to get address from individual fields

}
