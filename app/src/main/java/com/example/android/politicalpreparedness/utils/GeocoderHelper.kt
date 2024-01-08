package com.example.android.politicalpreparedness.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import kotlinx.coroutines.*
import java.util.*

class GeocoderHelper(
    private val geocoder: Geocoder,
    private val ioDispatcher: CoroutineDispatcher
) {

    class Factory(private val ioDispatcher: CoroutineDispatcher) {
        fun create(context: Context): GeocoderHelper {
            return getGeocoder(context, ioDispatcher)
        }
    }

    companion object {
        private fun getGeocoder(
            context: Context,
            ioDispatcher: CoroutineDispatcher
        ): GeocoderHelper =
            GeocoderHelper(Geocoder(context, Locale.getDefault()), ioDispatcher)
    }

    suspend fun getAddressFromLocation(location: Location): Address? {
        return if (android.os.Build.VERSION.SDK_INT >= VERSION_CODES.TIRAMISU) {
            getNewAddress(location)
        } else {
            withContext(ioDispatcher) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(VERSION_CODES.TIRAMISU)
    private suspend fun getNewAddress(location: Location): Address? {
        return suspendCancellableCoroutine<Address?> { cancellableCont ->
            geocoder.getFromLocation(location.latitude, location.longitude, 1) {
                cancellableCont.resume(it.firstOrNull(), null)
            }
        }
    }
}