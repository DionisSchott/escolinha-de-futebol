package com.dionis.escolinhajdb.di

import com.dionis.escolinhajdb.data.model.Player
import com.dionis.escolinhajdb.data.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        storageReference: StorageReference
//        appPreferences: SharedPreferences,
//        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database, storageReference//,appPreferences,gson
        )
    }

    @Provides
    @Singleton
    fun providePlayerRepository(
        database: FirebaseFirestore,
        storageReference: StorageReference,
    ): PlayerRepository {
        return PlayerRepositoryImpl (database, storageReference)
    }

    @Provides
    @Singleton
    fun provideListRepository(
        database: FirebaseFirestore
    ): ListRepository {
        return ListRepositoryImpl (database)
    }


}