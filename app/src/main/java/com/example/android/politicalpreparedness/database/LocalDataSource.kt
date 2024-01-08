package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.repository.ElectionDataSource
import com.example.android.politicalpreparedness.network.models.Address
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.State
import com.example.android.politicalpreparedness.repository.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class LocalDataSource(
        private val database: ElectionDao,
        private val ioDispatcher: CoroutineDispatcher,
) : ElectionDataSource {

    override suspend fun getElections(): Result<List<Election>> {
        return withContext(ioDispatcher) {
            return@withContext try {
                Result.Success(database.getAllSync())
            } catch (ex: Exception) {
                Result.Failure(ex)
            }
        }
    }

    override fun observerElections(): LiveData<Result<List<Election>>> {
        return database.getAll().map {
            Result.Success(it)
        }
    }

    override suspend fun saveElections(elections: List<Election>) {
        withContext(ioDispatcher) {
            elections.map { it.copy(saved = false) }
            database.saveAll(elections)
        }
    }

    override suspend fun markAsSaved(election: Election) {
        withContext(ioDispatcher) {
            database.save(election)
        }
    }

    override suspend fun deleteAll() {
        withContext(ioDispatcher) {
            database.deleteAll()
        }
    }

    override suspend fun getDetails(electionId: Int, address: String): Result<State?> {
        return Result.Failure(IllegalStateException("No details stored in local db"))
    }

    override suspend fun getRepresentatives(address: Address): Result<RepresentativeResponse> {
        return Result.Failure(IllegalStateException("No representative stored in local db"))
    }
}