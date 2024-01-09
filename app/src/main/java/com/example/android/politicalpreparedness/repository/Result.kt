package com.example.android.politicalpreparedness.repository

import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.ElectionModel
import com.example.android.politicalpreparedness.representative.model.Representative

sealed class Result<out T> {
    data class Failure<T>(val exception: Exception) : Result<T>()
    data class Success<T>(val data: T) : Result<T>()
    class Loading<T>() : Result<T>()
}

fun <T> Result<List<T>>.dataOrThrow(): List<T> {
    if(this is Result.Success) {
        return this.data
    }else{
        throw IllegalArgumentException("Result must be success type")
    }
}