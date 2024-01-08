package com.example.android.politicalpreparedness.repository

import androidx.lifecycle.LiveData
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.representative.model.Representative

interface ElectionsRepository {
    suspend fun getElections(force: Boolean): Result<List<Election>>
    suspend fun refreshElections()
    fun observeElections(): LiveData<Result<List<Election>>>
    suspend fun markAsSaved(election: Election, saved: Boolean)
    suspend fun getElectionDetails(electionId: Int, address: String): Result<State?>
    suspend fun searchRepresentatives(address: Address): Result<List<Representative>>
}