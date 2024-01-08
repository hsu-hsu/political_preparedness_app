package com.example.android.politicalpreparedness

import android.app.Application
import com.example.android.politicalpreparedness.database.ElectionDao
import com.example.android.politicalpreparedness.database.ElectionDatabase
import com.example.android.politicalpreparedness.database.LocalDataSource
import com.example.android.politicalpreparedness.election.ElectionsViewModel
import com.example.android.politicalpreparedness.election.VoterInfoViewModel
import com.example.android.politicalpreparedness.network.CivicsApi
import com.example.android.politicalpreparedness.network.CivicsApiService
import com.example.android.politicalpreparedness.network.NetworkDataSource
import com.example.android.politicalpreparedness.network.models.ElectionModel
import com.example.android.politicalpreparedness.repository.DefaultElectionsRepository
import com.example.android.politicalpreparedness.repository.ElectionDataSource
import com.example.android.politicalpreparedness.repository.ElectionsRepository
import com.example.android.politicalpreparedness.representative.RepresentativeViewModel
import com.example.android.politicalpreparedness.utils.GeocoderHelper
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class PoliticalPreparedApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        val module = module {
            viewModel { (election: ElectionModel) ->
                VoterInfoViewModel(get(), election)
            }
            viewModel { ElectionsViewModel(get()) }
            viewModel { RepresentativeViewModel(get()) }
            factory { GeocoderHelper.Factory(Dispatchers.IO) }
            single { ElectionDatabase.getInstance(this@PoliticalPreparedApp).electionDao as ElectionDao }
            single { CivicsApi.create() as CivicsApiService }
            single(qualifier = named("local")) {
                LocalDataSource(
                    get(),
                    Dispatchers.IO
                ) as ElectionDataSource
            }
            single(qualifier = named("remote")) {
                NetworkDataSource(
                    get(),
                    Dispatchers.IO
                ) as ElectionDataSource
            }
            single { SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()) as DateFormat }
            single {
                DefaultElectionsRepository(
                    get<ElectionDataSource>(qualifier = named("local")),
                    get<ElectionDataSource>(qualifier = named("remote")),
                    Dispatchers.IO,
                ) as ElectionsRepository
            }
        }
        startKoin {
            androidContext(this@PoliticalPreparedApp)
            modules(listOf(module))
        }
    }
}